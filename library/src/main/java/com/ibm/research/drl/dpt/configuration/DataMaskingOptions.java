/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataMaskingOptions implements Serializable {

    private final DataTypeFormat inputFormat;
    private final DataTypeFormat outputFormat;
    private final boolean identifyRelationships;

    private final DatasetOptions datasetOptions;

    private final Map<String, DataMaskingTarget> toBeMasked;
    private final Map<String, FieldRelationship> predefinedRelationships;

    public DataMaskingOptions(DataTypeFormat inputFormat,
                              DataTypeFormat outputFormat,
                              Map<String, DataMaskingTarget> toBeMasked,
                              boolean identifyRelationships,
                              Map<String, FieldRelationship> predefinedRelationships,
                              DatasetOptions datasetOptions) {

        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.identifyRelationships = identifyRelationships;

        this.datasetOptions = datasetOptions;

        this.toBeMasked = toBeMasked;
        this.predefinedRelationships = predefinedRelationships;

        if (!validateRelationships(predefinedRelationships)) {
            throw new RuntimeException("Cyclic dependency detected");
        }
    }

    @JsonCreator
    public DataMaskingOptions(
            @JsonProperty(value = "inputFormat", required = true)
            DataTypeFormat inputFormat,
            @JsonProperty(value = "outputFormat", required = true)
            DataTypeFormat outputFormat,
            @JsonProperty("delimiter")
            char delimiter,
            @JsonProperty("quoteChar")
            char quoteChar,
            @JsonProperty("hasHeader")
            boolean hasHeader,
            @JsonProperty(value = "trimFields")
            boolean trimFields,
            @JsonProperty(value = "toBeMasked", required = true)
            Map<String, JsonNode> toBeMasked,
            @JsonProperty(value = "identifyRelationships", defaultValue = "false")
            boolean identifyRelationships,
            @JsonProperty("predefinedRelationships")
            JsonNode predefinedRelationships
    ) throws IOException {
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.identifyRelationships = identifyRelationships;

        if (inputFormat == DataTypeFormat.CSV) {
            this.datasetOptions = new CSVDatasetOptions(hasHeader, delimiter, quoteChar, trimFields);
        } else {
            this.datasetOptions = null;
        }

        this.toBeMasked = preserveBackCompatibility(toBeMasked);
        this.predefinedRelationships = predefinedRelationshipsFromJSON(predefinedRelationships);

        try (InputStream inputStream = DataMaskingOptions.class.getResourceAsStream("/dataformat.json")) {
            Map<DataTypeFormat, DataFormatProperties> formatPropertiesMap = DataFormatPropertiesHelper.buildProperties(inputStream);
            validate(formatPropertiesMap);

            if (!validateRelationships(this.predefinedRelationships)) {
                throw new RuntimeException("Cyclic dependency detected");
            }
        }
    }

    public static void validateField(JsonNode configuration, String key, JsonNodeType expectedType) throws MisconfigurationException {

        JsonNode node = configuration.get(key);

        if (node == null) {
            throw new MisconfigurationException("Missing key " + key + " from configuration");
        }

        if (node.getNodeType() != expectedType) {
            throw new MisconfigurationException("Key " + key + " has wrong type. Expected is: " + expectedType.toString());
        }

    }

    public static boolean validateRelationships(Map<String, FieldRelationship> predefinedRelationships) {
        return null == predefinedRelationships || predefinedRelationships.isEmpty() || hasNoCyclicDependencies(predefinedRelationships);
    }

    private static boolean hasNoCyclicDependencies(Map<String, FieldRelationship> predefinedRelationships) {
        for (String fieldName : predefinedRelationships.keySet()) {
            if (hasCyclicDependencies(fieldName, predefinedRelationships)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasCyclicDependencies(String fieldName, Map<String, FieldRelationship> predefinedRelationships) {
        FieldRelationship fieldRelationship = predefinedRelationships.get(fieldName);
        Queue<String> dependencies = new LinkedList<>();

        for (RelationshipOperand operand : fieldRelationship.getOperands()) {
            dependencies.add(operand.getName());
        }

        Set<String> visited = new HashSet<>();
        visited.add(fieldName);

        while (!dependencies.isEmpty()) {
            String dependency = dependencies.poll();
            FieldRelationship f = predefinedRelationships.get(dependency);

            if (f == null) {
                continue;
            }

            for (RelationshipOperand operand : f.getOperands()) {
                String operandName = operand.getName();
                if (visited.contains(operandName)) {
                    return true;
                }

                dependencies.add(operand.getName());
            }

            visited.add(dependency);

        }

        return false;
    }

    public DatasetOptions getDatasetOptions() {
        return datasetOptions;
    }

    public DataTypeFormat getInputFormat() {
        return inputFormat;
    }

    public DataTypeFormat getOutputFormat() {
        return outputFormat;
    }

    public Map<String, DataMaskingTarget> getToBeMasked() {
        return toBeMasked;
    }

    public Map<String, FieldRelationship> getPredefinedRelationships() {
        return predefinedRelationships;
    }

    private Map<String, FieldRelationship> predefinedRelationshipsFromJSON(JsonNode node) throws IOException {
        Map<String, FieldRelationship> predefinedRelationships = new HashMap<>();

        if (node != null) {
            Iterator<JsonNode> iterator = node.elements();

            while (iterator.hasNext()) {
                JsonNode element = iterator.next();
                FieldRelationship fieldRelationship = JsonUtils.MAPPER.readValue(element.toString(), FieldRelationship.class);
                String fieldName = fieldRelationship.getFieldName();
                predefinedRelationships.put(fieldName, fieldRelationship);
            }
        }

        return predefinedRelationships;
    }


    private Set<DataTypeFormat> findSupportedFormats(Map<DataTypeFormat, DataFormatProperties> formatPropertiesMap) {
        Set<DataTypeFormat> formatsThatSupportMasking = new HashSet<>();

        formatPropertiesMap.forEach((type, properties) -> {
            if (properties.supportsMasking()) {
                formatsThatSupportMasking.add(type);
            }
        });

        return formatsThatSupportMasking;
    }

    private void validate(Map<DataTypeFormat, DataFormatProperties> formatPropertiesMap) {
        Set<DataTypeFormat> formatsThatSupportMasking = findSupportedFormats(formatPropertiesMap);

        if (this.inputFormat == null) {
            throw new RuntimeException("unknown input format. Supported ones are: " + StringUtils.join(formatsThatSupportMasking, ","));
        }

        if (this.outputFormat == null) {
            throw new RuntimeException("unknown output format. Supported ones are: " + StringUtils.join(DataTypeFormat.values(), ","));
        }

        DataFormatProperties formatProperties = formatPropertiesMap.get(this.inputFormat);

        if (formatProperties == null || !formatProperties.supportsMasking()) {
            throw new RuntimeException("input format does not support masking. Formats that support masking are: "
                    + StringUtils.join(formatsThatSupportMasking, ","));
        }

        if (!formatProperties.getValidOutputFormats().contains(this.outputFormat)) {
            throw new RuntimeException("input format does not support: " + this.outputFormat.name() + " as output. Valid output formats: "
                    + StringUtils.join(formatProperties.getValidOutputFormats(), ","));
        }
    }

    private Map<String, DataMaskingTarget> preserveBackCompatibility(Map<String, JsonNode> toBeMasked) {
        return toBeMasked.entrySet().stream().map(entry -> {
            JsonNode fieldValue = entry.getValue();
            String fieldName = entry.getKey();

            final DataMaskingTarget target;
            if (fieldValue.isObject()) {
                String providerTypeValue = fieldValue.get("providerType").asText();
                String targetPath = fieldValue.get("targetPath").asText();
                target = new DataMaskingTarget(ProviderType.valueOf(providerTypeValue), targetPath);
            } else if (fieldValue.isTextual()) {
                String providerTypeValue = fieldValue.asText();
                target = new DataMaskingTarget(ProviderType.valueOf(providerTypeValue), fieldName);
            } else {
                throw new RuntimeException("Invalid type for the entry " + fieldName + " of toBeMasked");
            }
            return new AbstractMap.SimpleEntry<>(fieldName, target);
        }).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }
}

