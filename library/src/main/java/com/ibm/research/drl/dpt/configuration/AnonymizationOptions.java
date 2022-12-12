/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationLossMetricFactory;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetric;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.exceptions.RiskOptionsMisconfigurationException;
import com.ibm.research.drl.dpt.risk.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnonymizationOptions {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final List<PrivacyConstraint> privacyConstraints;
    private final List<ColumnInformation> columnInformation;
    private final DatasetOptions datasetOptions;
    private final double suppressionRate;
    private final InformationMetric informationLoss;
    private final Map<String, String> riskOptions;
    private final RiskMetric riskMetric;
    private final boolean estimateUniqueness;

    private static final String CLASS_NAME_FIELD = "className";
    private static final String SUPPRESSION_FIELD = "suppression";

    @JsonCreator
    public AnonymizationOptions(
            @JsonProperty("delimiter")
                    char delimiter,
            @JsonProperty("quoteChar")
                    char quoteChar,
            @JsonProperty(value = "hasHeader", defaultValue = "false")
                    boolean hasHeader,
            @JsonProperty(value = "trimFields")
                    boolean trimFields,
            @JsonProperty(value = "estimateUniqueness", required = true)
            boolean estimateUniqueness,
            @JsonProperty(value = "riskMetric", required = true)
                    JsonNode riskMetricNode,
            @JsonProperty(value = "riskMetricOptions", required = true)
                    JsonNode riskMetricOptionsNode,
            @JsonProperty(value = "hierarchies", required = true)
                    JsonNode hierarchiesNode,
            @JsonProperty(value = "informationLoss", required = true)
            String informationLoss,
            @JsonProperty(value = "options")
            JsonNode options,
            @JsonProperty(value = "columnInformation", required = true)
            JsonNode columnInformation,
            @JsonProperty(value = "privacyConstraints", required = true)
            JsonNode privacyConstraints
    ) {
        Map<String, GeneralizationHierarchy> hierarchies = hierarchiesFromJSON(hierarchiesNode);
        this.columnInformation = columnInformationFromJSON(columnInformation, hierarchies);
        this.privacyConstraints = privacyConstraintsFromJSON(privacyConstraints);

        this.datasetOptions = new CSVDatasetOptions(
                hasHeader,
                delimiter,
                quoteChar,
                trimFields
        );

        this.estimateUniqueness = estimateUniqueness;

            try {
                riskMetric = generateRiskMetric(riskMetricNode);
                riskOptions = generateRiskOptions(riskMetricOptionsNode);
                riskMetric.validateOptions(riskOptions);
            } catch (IllegalArgumentException e) {
                throw new RiskOptionsMisconfigurationException(e.getMessage());
            }

            this.informationLoss = InformationLossMetricFactory.getInstance(informationLoss);


        if (Objects.nonNull(options) && options.isObject() && options.has(SUPPRESSION_FIELD)) {
            try {
                JsonNode suppressionNode = options.get(SUPPRESSION_FIELD);
                if (suppressionNode.isNumber())
                    this.suppressionRate = suppressionNode.asDouble();
                else
                    throw new MisconfigurationException("Error parsing suppression rate:" + suppressionNode.asText());
            } catch (Exception e) {
                throw new MisconfigurationException("Error parsing suppression rate:" + options.get(SUPPRESSION_FIELD));
            }
        } else {
            this.suppressionRate = 0.0;
        }
    }

    private Map<String, String> generateRiskOptions(JsonNode riskMetricOptions) {
        return mapper.convertValue(riskMetricOptions, new TypeReference<>() {});
    }

    private RiskMetric generateRiskMetric(final JsonNode riskMetric) {


        if (riskMetric.isNull()) {
            return new RiskMetric() {
                @Override
                public String getName() {
                    return null;
                }

                @Override
                public String getShortName() {
                    return null;
                }

                @Override
                public double report() {
                    return 0;
                }

                @Override
                public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
                    return null;
                }

                @Override
                public void validateOptions(Map<String, String> options) {
                    /* there is nothing to validate here */
                }
            };
        }

        if (riskMetric.isTextual()) {
            final String riskMetricName = riskMetric.asText();
            switch (riskMetricName) {
                case "ZAYATZ":
                    return new ZayatzEstimator();
                case "KRM":
                    return new KRatioMetric();
                case "FKRM":
                    return new FKRatioMetric();
                case "BINOM":
                    return new BinomialRiskMetric();
                default:
                    throw new RiskOptionsMisconfigurationException("Unknown risk metric: " + riskMetricName);
            }
        } else {
            throw new RiskOptionsMisconfigurationException("The riskMetric field must be textual");
        }
    }

    public double getSuppressionRate() {
        return suppressionRate;
    }

    public List<PrivacyConstraint> getPrivacyConstraints() {
        return privacyConstraints;
    }

    public List<ColumnInformation> getColumnInformation() {
        return columnInformation;
    }

    public static List<PrivacyConstraint> privacyConstraintsFromJSON(JsonNode node) {
        if (node.isNull() || !node.isArray()) {
            throw new MisconfigurationException("Privacy constraints is not an array");
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();

        for (JsonNode element : node) {
            if (element.isNull() || !element.isObject()) {
                throw new MisconfigurationException("Element in privacy constraints array is null or not an object");
            }

            JsonNode nameNode = element.get("name");
            if (nameNode == null || nameNode.isNull() || !nameNode.isTextual()) {
                throw new MisconfigurationException("Name of privacy constraint missing or null or not a text");
            }

            String name = nameNode.asText();
            switch (name) {
                case "k":
                    JsonNode kNode = element.get("k");
                    if (kNode == null || kNode.isNull() || !kNode.isInt()) {
                        throw new MisconfigurationException("parameter for k is either missing, null or not an integer");
                    }
                    
                    int kValue = kNode.intValue();
                    
                    if (kValue <= 1) {
                        throw new MisconfigurationException("k value must be greater than 1");
                    }
                    
                    privacyConstraints.add(new KAnonymity(kNode.intValue()));
                    break;
                case "distinctL":
                    JsonNode lNode = element.get("l");
                    if (lNode == null || lNode.isNull() || !lNode.isInt()) {
                        throw new MisconfigurationException("parameter for distinctL is either missing, null or not an integer");
                    }
                    
                    int lValue = lNode.intValue();

                    if (lValue < 1) {
                        throw new MisconfigurationException("l value must be >=1");
                    }
                    
                    privacyConstraints.add(new DistinctLDiversity(lValue));
                    break;
                case "entropyL":
                    JsonNode leNode = element.get("l");
                    if (leNode == null || leNode.isNull() || !leNode.isInt()) {
                        throw new MisconfigurationException("parameter for entropyL is either missing, null or not an integer");
                    }
                    
                    privacyConstraints.add(new EntropyLDiversity(leNode.intValue()));
                    break;
                case "recursiveCL":
                    JsonNode lcNode = element.get("l");
                    if (lcNode == null || lcNode.isNull() || !lcNode.isInt()) {
                        throw new MisconfigurationException("parameter for recursiveCL is either missing, null or not an integer");
                    }

                    JsonNode cNode = element.get("c");
                    if (cNode == null || cNode.isNull() || !cNode.isInt()) {
                        throw new MisconfigurationException("parameter for recursiveCL is either missing, null or not an integer");
                    }
                    privacyConstraints.add(new RecursiveCLDiversity(lcNode.intValue(), cNode.asDouble()));
                    break;
                case "tCloseness":
                    JsonNode tNode = element.get("t");
                    if (tNode == null || tNode.isNull() || !tNode.isDouble()) {
                        throw new MisconfigurationException("parameter t for tCloseness is either missing, null or not a number");
                    }
                    
                    privacyConstraints.add(new TCloseness(tNode.doubleValue()));
                    break;
                default:
                    throw new MisconfigurationException("unknown privacy constraint name");
            }
        }

        return privacyConstraints;
    }
   
    public static GeneralizationHierarchy getHierarchyFromJsonNode(JsonNode specification) {
        GeneralizationHierarchy hierarchy;

        switch (specification.getNodeType()) {
            case ARRAY:
                hierarchy = createMaterializedHierarchy(specification);
                break;
            case STRING:
                hierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(specification.asText());

                if (null == hierarchy) {
                    throw new MisconfigurationException(specification.asText() + " is not a well known hierarchy type");
                }
                break;
            case OBJECT:
                hierarchy = buildFromObjectSpecification(specification);
                break;
            default:
                throw new MisconfigurationException("Hierarchy is not recognized");
        }
        
        return hierarchy;
    }
    
    public static Map<String, GeneralizationHierarchy> hierarchiesFromJSON(JsonNode hierarchies) {
        if (hierarchies == null || hierarchies.isNull() || !hierarchies.isObject()) {
            throw new MisconfigurationException("hierarchies key is either missing or null or not an object");
        }

        Map<String, GeneralizationHierarchy> hierarchyMap = new HashMap<>();

        Iterator<Map.Entry<String, JsonNode>> hierarchyFields = hierarchies.fields();

        while (hierarchyFields.hasNext()) {
            Map.Entry<String, JsonNode> hiearchySpecification = hierarchyFields.next();

            String hierarchyName = hiearchySpecification.getKey();
            JsonNode specification = hiearchySpecification.getValue();

            GeneralizationHierarchy hierarchy;

            switch (specification.getNodeType()) {
                case ARRAY:
                    hierarchy = createMaterializedHierarchy(specification);
                    break;
                case STRING:
                    hierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(specification.asText());

                    if (null == hierarchy) {
                        throw new MisconfigurationException("Hierarchy " + specification.asText() + " is not a well known hierarchy type");
                    }
                    break;
                case OBJECT:
                    hierarchy = buildFromObjectSpecification(specification);
                    break;
                default:
                    throw new MisconfigurationException("Hierarchy " + hierarchyName + " is not recognized");
            }

            hierarchyMap.put(hierarchyName, hierarchy);
        }

        return hierarchyMap;
    }


    private static GeneralizationHierarchy buildFromObjectSpecification(JsonNode specification) {
        if (!specification.has(CLASS_NAME_FIELD)) throw new MisconfigurationException("Missing hierarchy class name");

        try {
            Class<?> hierarchy = Class.forName(specification.get(CLASS_NAME_FIELD).asText());

            Constructor<?> constructor = hierarchy.getConstructor(JsonNode.class);

            return (GeneralizationHierarchy) constructor.newInstance(specification.get("options"));
        } catch (ClassNotFoundException e) {
            throw new MisconfigurationException("Hierarchy class " + specification.get(CLASS_NAME_FIELD).asText() + " not found");
        } catch (NoSuchMethodException e) {
            throw new MisconfigurationException("Hierarchy class " + specification.get(CLASS_NAME_FIELD).asText() + " missed constructor with one com.fasterxml.jackson.databind.JsonNode parameter");
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new MisconfigurationException("Error instantiating new object of " + specification.get(CLASS_NAME_FIELD).asText());
        }
    }

    private static GeneralizationHierarchy createMaterializedHierarchy(JsonNode hierarchyValues) {

        MaterializedHierarchy materializedHierarchy = new MaterializedHierarchy();

        for (JsonNode hierarchyEntry : hierarchyValues) {
            Iterator<JsonNode> entryIterator = hierarchyEntry.iterator();
            List<String> entries = new ArrayList<>();

            while (entryIterator.hasNext()) {
                String v = entryIterator.next().asText();
                entries.add(v);
            }

            if (entries.isEmpty()) {
                throw new MisconfigurationException("empty entries for hierarchy");
            }

            materializedHierarchy.add(entries);
        }

        return materializedHierarchy;
    }

    private static String listColumnTypes() {
       ColumnType[] values = ColumnType.values();
       return StringUtils.join(values, ",");
    }

    public static List<ColumnInformation> columnInformationFromJSON(JsonNode contents, Map<String, GeneralizationHierarchy> defaultHierarchyMap) {

        List<ColumnInformation> columnInformationList = new ArrayList<>();

        if (contents == null || !contents.isArray()) {
            throw new MisconfigurationException("ColumnInformation is either missing or not an array");
        }

        for (JsonNode entry : contents) {
            if (!entry.isObject()) {
                throw new MisconfigurationException("Entry inside the columnInformation array is not an object");
            }

            JsonNode typeNode = entry.get("type");
            if (typeNode == null || !typeNode.isTextual()) {
                throw new MisconfigurationException("Type for column is either missing or not a text");
            }

            ColumnType columnType;
            try {
                columnType = ColumnType.valueOf(typeNode.asText());
            } catch (IllegalArgumentException e) {
                throw new MisconfigurationException("Invalid column type. Valid values are: " + listColumnTypes());
            }

            final boolean isForLinking = entry.has("isForLinking") && entry.get("isForLinking").asBoolean();

            
            switch (columnType) {
                case QUASI:
                    final double weight;

                    final JsonNode weightNode = entry.get("weight");

                    if (weightNode != null) {
                        if (!weightNode.isNumber()) {
                            throw new MisconfigurationException("Weight for column is not a number");
                        }

                        weight = weightNode.asDouble();

                        if (weight < 0.0) {
                            throw new MisconfigurationException("Weight for a column cannot be negative");
                        }
                    } else {
                        weight = 1.0;
                    }
                    
                    JsonNode isCategoricalNode = entry.get("isCategorical");
                    if (isCategoricalNode == null || !isCategoricalNode.isBoolean()) {
                        throw new MisconfigurationException("isCategorical for column is either missing or not a boolean");
                    }

                    final boolean isCategorical = isCategoricalNode.asBoolean();
                    
                    if (isCategorical) {
                        JsonNode hierarchyNode = entry.get("hierarchy");
                        if (hierarchyNode == null || !hierarchyNode.isTextual() || hierarchyNode.isNull()) {
                            throw new MisconfigurationException("Hierarchy element is either missing or not text");
                        }

                        final String hierarchyName = hierarchyNode.asText();
                        final GeneralizationHierarchy hierarchy = defaultHierarchyMap.get(hierarchyName);

                        if (hierarchy == null) {
                            throw new MisconfigurationException("Cannot find the hierarchy: " + hierarchyName);
                        }

                       

                        int maximumLevel = -1;
                        final JsonNode maximumLevelNode = entry.get("maximumLevel");

                        if (maximumLevelNode != null) {
                            if (!maximumLevelNode.isInt()) {
                                throw new MisconfigurationException("MaximumLevel for column is not an integer");
                            }

                            maximumLevel = entry.get("maximumLevel").asInt();
                            if (maximumLevel < -1) {
                                throw new MisconfigurationException("MaximumLevel for column is invalid negative integer");
                            }

                            if (maximumLevel > hierarchy.getHeight()) {
                                maximumLevel = hierarchy.getHeight();
                            }
                        }

                        columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI, weight, maximumLevel, isForLinking));
                    } else {
                        List<Double> sortedValues = new ArrayList<>(); 
                        columnInformationList.add(new NumericalRange(sortedValues, ColumnType.QUASI, weight, isForLinking));
                    }
                    break;
                case SENSITIVE:
                    columnInformationList.add(new SensitiveColumnInformation(isForLinking));
                    break;
                default:
                    columnInformationList.add(new DefaultColumnInformation(isForLinking));
                    break;
            }
        }


        return columnInformationList;
    }

    public boolean isEstimateUniqueness() {
        return estimateUniqueness;
    }

    public InformationMetric getInformationLoss() {
        return informationLoss;
    }

    public RiskMetric getRiskMetric() {
        return riskMetric;
    }

    public Map<String, String> getRiskOptions() {
        return riskOptions;
    }

    public DatasetOptions getDatasetOptions() {
        return datasetOptions;
    }
}
