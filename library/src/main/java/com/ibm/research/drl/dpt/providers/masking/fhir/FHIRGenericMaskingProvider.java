/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FHIRGenericMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {
    private final List<FHIRResourceMaskingAction> maskingActionList;
    private final Set<String> maskedFields;

    private final FHIRBaseDomainResourceMaskingProvider fhirBaseDomainResourceMaskingProvider;

    private AbstractComplexMaskingProvider<JsonNode> createFHIRMaskingProvider(String fhirType, String fullPath, MaskingConfiguration maskingConfiguration) {
        if (fhirType.startsWith("FHIR_")) {
            fhirType = fhirType.substring(5);
        }

        switch (fhirType) {
            case "Address":
                return new FHIRAddressMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Annotation":
                return new FHIRAnnotationMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Attachment":
                return new FHIRAttachmentMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "CodeableConcept":
                return new FHIRCodeableConceptMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Coding":
                return new FHIRCodingMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "ContactPoint":
                return new FHIRContactPointMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "HumanName":
                return new FHIRHumanNameMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Identifier":
                return new FHIRIdentifierMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Period":
                return new FHIRPeriodMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Quantity":
                return new FHIRQuantityMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Range":
                return new FHIRRangeMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Ratio":
                return new FHIRRatioMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Reference":
                return new FHIRReferenceMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "SampledData":
                return new FHIRSampledDataMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Timing":
                return new FHIRTimingMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath, this.factory);
            case "Delete":
                return new FHIRNullMaskingProvider(getConfigurationForSubfield(fullPath, maskingConfiguration),
                        maskedFields, fullPath);
            default:
                throw new RuntimeException("unsupported fhir type provider: " + fhirType);
        }
    }

    private List<FHIRResourceMaskingAction> buildMaskingActions(FHIRResourceMaskingConfiguration resourceConfiguration,
                                                                MaskingConfiguration maskingConfiguration) {
        List<FHIRResourceMaskingAction> maskingActions = new ArrayList<>();

        List<FHIRResourceField> fields = resourceConfiguration.getFields();

        String basePath = resourceConfiguration.getBasePath();

        for (FHIRResourceField field : fields) {

            String path = field.getPath();
            String fullPath = basePath + path;

            MaskingProvider maskingProvider = null;
            AbstractComplexMaskingProvider abstractComplexMaskingProvider = null;
            boolean isDelete = false;

            String fhirType = field.getFhirType();

            if (fhirType.equals("Delete")) {
                isDelete = true;
            } else if (fhirType.startsWith("FHIR_")) {
                abstractComplexMaskingProvider = createFHIRMaskingProvider(fhirType, fullPath, maskingConfiguration);
            } else {
                MaskingConfiguration nameMaskingConfiguration = getConfigurationForSubfield(fullPath, maskingConfiguration);
                maskingProvider = this.factory.get(ProviderType.valueOf(fhirType), nameMaskingConfiguration);
            }

            maskingActions.add(new FHIRResourceMaskingAction(fullPath, path, maskingProvider,
                    abstractComplexMaskingProvider, isDelete));
        }

        return maskingActions;
    }

    public FHIRGenericMaskingProvider(FHIRResourceMaskingConfiguration resourceConfiguration,
                                      MaskingConfiguration maskingConfiguration, Set<String> maskedFields, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.maskedFields = maskedFields;

        this.maskingActionList = buildMaskingActions(resourceConfiguration, maskingConfiguration);
        this.fhirBaseDomainResourceMaskingProvider = new FHIRBaseDomainResourceMaskingProvider(maskingConfiguration, maskedFields, resourceConfiguration.getBasePath(), this.factory);
    }

    private void maskFinalPathComplex(JsonNode node, JsonNode valueNode, String path,
                                      AbstractComplexMaskingProvider<JsonNode> abstractComplexMaskingProvider) {
        if (valueNode == null) {
            return;
        }

        if (valueNode.isObject()) {
            JsonNode maskedValueNode = abstractComplexMaskingProvider.mask(valueNode);
            ((ObjectNode) node).set(path, maskedValueNode);
        } else if (valueNode.isArray()) {
            Iterator<JsonNode> items = valueNode.elements();
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

            while (items.hasNext()) {
                JsonNode item = items.next();
                JsonNode maskedItem = abstractComplexMaskingProvider.mask(item);
                arrayNode.add(maskedItem);
            }

            ((ObjectNode) node).set(path, arrayNode);
        }
    }

    private ValueNode getValueNode(JsonNode originalValueNode, String maskedValue) {
        switch (originalValueNode.getNodeType()) {
            case NUMBER:
                if (originalValueNode.isInt()) {
                    return new IntNode(Integer.parseInt(maskedValue));

                } else if (originalValueNode.isFloat()) {
                    return new FloatNode(Float.parseFloat(maskedValue));
                } else {
                    return new DoubleNode(Double.parseDouble(maskedValue));
                }
            case BOOLEAN:
                return BooleanNode.valueOf(Boolean.parseBoolean(maskedValue));
            case BINARY:
                return new BinaryNode(maskedValue.getBytes());
            case STRING:
            default:
                return new TextNode(maskedValue);
        }
    }

    private void putValue(JsonNode node, JsonNode valueNode, String path, String maskedValue) {
        ((ObjectNode) node).set(path, getValueNode(valueNode, maskedValue));
    }

    private void maskFinalPathSimple(JsonNode node, JsonNode valueNode, String path, MaskingProvider maskingProvider) {

        if (valueNode == null) {
            return;
        }

        if (valueNode.isObject()) {
            return;
        }

        if (valueNode.isArray()) {
            Iterator<JsonNode> items = valueNode.elements();
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

            while (items.hasNext()) {
                JsonNode item = items.next();

                if (item.isNull() || item.isObject() || item.isArray()) {
                    arrayNode.add(item);
                    continue;
                }

                if (!item.isNull()) {
                    String maskedItem = maskingProvider.mask(item.asText());
                    arrayNode.add(getValueNode(item, maskedItem));
                }
            }

            ((ObjectNode) node).set(path, arrayNode);
        } else {
            if (!valueNode.isNull()) {
                String value = valueNode.asText();
                String maskedValue = maskingProvider.mask(value);
                putValue(node, valueNode, path, maskedValue);
            }
        }
    }

    private void maskNode(JsonNode rootNode, JsonNode node, String[] paths, int pathIndex, FHIRResourceMaskingAction maskingAction) {
        String path = paths[pathIndex];

        if (pathIndex < (paths.length - 1)) {
            JsonNode subNode = node.get(path);

            if (subNode == null) {
                return;
            }

            if (subNode.isArray()) {
                Iterator<JsonNode> items = subNode.elements();
                while (items.hasNext()) {
                    maskNode(rootNode, items.next(), paths, pathIndex + 1, maskingAction);
                }
            } else if (subNode.isObject()) {
                maskNode(rootNode, subNode, paths, pathIndex + 1, maskingAction);
            }
        } else {

            if (maskingAction.isDelete()) {
                ((ObjectNode) node).set(path, NullNode.getInstance());
                return;
            }

            if (maskingAction.getAbstractComplexMaskingProvider() != null) {
                AbstractComplexMaskingProvider<JsonNode> abstractComplexMaskingProvider = maskingAction.getAbstractComplexMaskingProvider();
                JsonNode valueNode = node.get(path);
                maskFinalPathComplex(node, valueNode, path, abstractComplexMaskingProvider);
            } else {
                MaskingProvider maskingProvider = maskingAction.getMaskingProvider();
                JsonNode valueNode = node.get(path);
                maskFinalPathSimple(node, valueNode, path, maskingProvider);
            }
        }

    }

    public JsonNode mask(JsonNode node) {
        this.fhirBaseDomainResourceMaskingProvider.mask(node);

        for (FHIRResourceMaskingAction maskingAction : this.maskingActionList) {
            if (!isAlreadyMasked(maskingAction.getFullPath())) {
                String[] paths = maskingAction.getPaths();
                maskNode(node, node, paths, 0, maskingAction);
            }
        }


        return node;
    }
}


