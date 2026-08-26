/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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

/** FHIRMaskingUtils FHIR datatype. */
public class FHIRMaskingUtils {
    /** Constructs a FHIRMaskingUtils. */
    public FHIRMaskingUtils() {}

    /**
     * Masks a resource ID.
     *
     * @param id               the resource ID
     * @param preserveIdPrefix whether to preserve the prefix before the last '/'
     * @param maskingProvider  the masking provider
     * @return the masked resource ID
     */
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

    /**
     * Builds a set from a comma-separated string.
     *
     * @param value       the comma-separated string
     * @param toUppercase whether to convert values to uppercase
     * @return the resulting set
     */
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

    /**
     * Pre-processes a FHIR JSON node by wrapping it under its resource type path.
     *
     * @param node the FHIR JSON node
     * @return the pre-processed node, or null if resourceType is absent
     */
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

    /**
     * Pre-processes a FHIR JSON string.
     *
     * @param object the FHIR JSON string
     * @return the pre-processed JSON string, or null
     * @throws IOException if parsing fails
     */
    public static String preprocessFHIRObject(String object) throws IOException {
        JsonNode node = JsonUtils.MAPPER.readTree(object);

        JsonNode processedNode = preprocessFHIRObject(node);

        if (processedNode == null) {
            return null;
        }

        return processedNode.toString();
    }

    /**
     * Post-processes a FHIR JSON node by extracting the first child.
     *
     * @param node the wrapped FHIR JSON node
     * @return the first child node, or null
     */
    public static JsonNode postProcessFHIRObject(JsonNode node) {
        Iterator<JsonNode> iterator = node.iterator();

        if (!iterator.hasNext()) {
            return null;
        }

        return iterator.next();
    }

    /**
     * Post-processes a FHIR JSON string.
     *
     * @param object the wrapped FHIR JSON string
     * @return the post-processed JSON string, or null
     * @throws IOException if parsing fails
     */
    public static String postProcessFHIRObject(String object) throws IOException {
        JsonNode node = JsonUtils.MAPPER.readTree(object);
        JsonNode processedNode = postProcessFHIRObject(node);

        if (processedNode == null) {
            return null;
        }

        return processedNode.toString();
    }

    /**
     * Masks a collection of FHIR identifiers.
     *
     * @param identifiers    the identifiers to mask
     * @param maskingProvider the identifier masking provider
     * @return the masked identifiers
     */
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

    /**
     * Masks a collection of FHIR references.
     *
     * @param identifiers    the references to mask
     * @param maskingProvider the reference masking provider
     * @return the masked references
     */
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

    /**
     * Masks a collection of FHIR annotations.
     *
     * @param identifiers    the annotations to mask
     * @param maskingProvider the annotation masking provider
     * @return the masked annotations
     */
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

    /**
     * Masks a collection of FHIR contact points.
     *
     * @param telecoms       the contact points to mask
     * @param maskingProvider the contact point masking provider
     * @return the masked contact points
     */
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

    /**
     * Masks a collection of FHIR codeable concepts.
     *
     * @param codeableConcepts the codeable concepts to mask
     * @param maskingProvider  the codeable concept masking provider
     * @return the masked codeable concepts
     */
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

    /**
     * Masks a collection of FHIR addresses.
     *
     * @param telecoms       the addresses to mask
     * @param maskingProvider the address masking provider
     * @return the masked addresses
     */
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


