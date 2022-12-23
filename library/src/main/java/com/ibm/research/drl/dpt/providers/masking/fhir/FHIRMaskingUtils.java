/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.*;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.fhir.datatypes.*;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.util.*;

public class FHIRMaskingUtils {
    public static String maskResourceId(String id, boolean preserveIdPrefix, MaskingProvider maskingProvider) {
        if (!preserveIdPrefix) {
            return maskingProvider.mask(id);
        }

        int slashIndex = id.lastIndexOf('/');
        if (slashIndex == -1) {
            return maskingProvider.mask(id);
        }

        String prefix = id.substring(0, slashIndex + 1);

        return prefix + maskingProvider.mask(id.substring(slashIndex + 1));
    }

    public static Set<String> setFromString(String value, boolean toUppercase) {
        Set<String> set = new HashSet<>();

        if (value != null && !value.isEmpty()) {
            for (String v : value.split(",")) {
                if (toUppercase) {
                    set.add(v.toUpperCase());
                } else {
                    set.add(v);
                }
            }
        }

        return set;
    }

    public static JsonNode preprocessFHIRObject(JsonNode node) {
        JsonNode typeNode = node.get("resourceType");

        if (typeNode == null) {
            return null;
        }

        String resourceType = typeNode.asText();
        String key = "/fhir/" + resourceType;

        ObjectNode processedNode = new ObjectNode(JsonNodeFactory.instance);
        processedNode.set(key, node);

        return processedNode;

    }

    public static String preprocessFHIRObject(String object) throws IOException {
        JsonNode node = JsonUtils.MAPPER.readTree(object);

        JsonNode processedNode = preprocessFHIRObject(node);

        if (processedNode == null) {
            return null;
        }

        return processedNode.toString();
    }

    public static JsonNode postProcessFHIRObject(JsonNode node) {
        Iterator<JsonNode> iterator = node.iterator();

        if (!iterator.hasNext()) {
            return null;
        }

        return iterator.next();
    }

    public static String postProcessFHIRObject(String object) throws IOException {
        JsonNode node = JsonUtils.MAPPER.readTree(object);
        JsonNode processedNode = postProcessFHIRObject(node);

        if (processedNode == null) {
            return null;
        }

        return processedNode.toString();
    }

    public static Collection<FHIRIdentifier> maskIdentifiers(Collection<FHIRIdentifier> identifiers, FHIRIdentifierMaskingProvider maskingProvider) {
        if (identifiers == null || identifiers.isEmpty()) {
            return identifiers;
        }

        Collection<FHIRIdentifier> maskedIdentifiers = new ArrayList<>();
        for (FHIRIdentifier identifier : identifiers) {
            maskedIdentifiers.add(maskingProvider.mask(identifier));
        }

        return maskedIdentifiers;
    }

    public static Collection<FHIRReference> maskReferences(Collection<FHIRReference> identifiers, FHIRReferenceMaskingProvider maskingProvider) {

        if (identifiers == null || identifiers.isEmpty()) {
            return identifiers;
        }

        Collection<FHIRReference> maskedIdentifiers = new ArrayList<>();
        for (FHIRReference identifier : identifiers) {
            maskedIdentifiers.add(maskingProvider.mask(identifier));
        }

        return maskedIdentifiers;
    }

    public static Collection<FHIRAnnotation>
    maskAnnotations(Collection<FHIRAnnotation> identifiers, FHIRAnnotationMaskingProvider maskingProvider) {

        if (identifiers == null || identifiers.isEmpty()) {
            return identifiers;
        }

        Collection<FHIRAnnotation> maskedIdentifiers = new ArrayList<>();
        for (FHIRAnnotation identifier : identifiers) {
            maskedIdentifiers.add(maskingProvider.mask(identifier));
        }

        return maskedIdentifiers;
    }

    public static Collection<FHIRContactPoint> maskTelecoms(Collection<FHIRContactPoint> telecoms, FHIRContactPointMaskingProvider maskingProvider) {

        if (telecoms == null || telecoms.isEmpty()) {
            return telecoms;
        }

        Collection<FHIRContactPoint> maskedTelecoms = new ArrayList<>();
        for (FHIRContactPoint telecom : telecoms) {
            maskedTelecoms.add(maskingProvider.mask(telecom));
        }

        return maskedTelecoms;
    }

    public static Collection<FHIRCodeableConcept> maskCodeableConcepts(Collection<FHIRCodeableConcept> codeableConcepts, FHIRCodeableConceptMaskingProvider maskingProvider) {

        if (codeableConcepts == null || codeableConcepts.isEmpty()) {
            return codeableConcepts;
        }

        Collection<FHIRCodeableConcept> maskedConcepts = new ArrayList<>();
        for (FHIRCodeableConcept codeableConcept : codeableConcepts) {
            maskedConcepts.add(maskingProvider.mask(codeableConcept));
        }

        return maskedConcepts;
    }

    public static Collection<FHIRAddress> maskAddresses(Collection<FHIRAddress> telecoms, FHIRAddressMaskingProvider maskingProvider) {

        if (telecoms == null || telecoms.isEmpty()) {
            return telecoms;
        }

        Collection<FHIRAddress> maskedTelecoms = new ArrayList<>();
        for (FHIRAddress telecom : telecoms) {
            maskedTelecoms.add(maskingProvider.mask(telecom));
        }

        return maskedTelecoms;
    }
}


