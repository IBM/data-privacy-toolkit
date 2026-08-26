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
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** FHIRMaskingProvider FHIR datatype. */
public class FHIRMaskingProvider extends AbstractComplexMaskingProvider<String> {
    /** Map of FHIR resource type names to their masking providers. */
    private final Map<String, FHIRGenericMaskingProvider> maskingProviderMap;

    /**
     * Constructs a FHIRMaskingProvider.
     * @param maskingConfiguration the maskingConfiguration
     * @param factory the factory
     */
    public FHIRMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("fhir", maskingConfiguration, new HashSet<>(), factory);
    }

    /**
     * Constructs a FHIRMaskingProvider.
     * @param random the random
     * @param maskingConfiguration the maskingConfiguration
     * @param factory the factory
     */
    public FHIRMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("fhir", maskingConfiguration, new HashSet<>(), factory);
    }

    /**
     * Constructs a FHIRMaskingProvider.
     * @param random the random
     * @param maskingConfiguration the maskingConfiguration
     * @param maskedFields the maskedFields
     * @param factory the factory
     */
    public FHIRMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, Set<String> maskedFields, MaskingProviderFactory factory) {
        this("fhir", maskingConfiguration, maskedFields, factory);
    }

    /**
     * Loads masking rules for a given FHIR resource from configuration.
     *
     * @param resourceName         the FHIR resource name
     * @param maskingConfiguration the masking configuration
     * @return the collection of masking rule strings
     */
    public static Collection<String> loadRulesForResource(String resourceName, MaskingConfiguration maskingConfiguration) {
        return maskingConfiguration.getStringValueWithPrefixMatch("fhir.maskingConf." + resourceName + ".");
    }

    /**
     * Loads masking rules for a given FHIR resource from the masking targets map.
     *
     * @param resourceName the FHIR resource name
     * @param toBeMasked   the map of fields to masking targets
     * @return the collection of masking rule strings
     */
    public static Collection<String> loadRulesForResource(String resourceName, Map<String, DataMaskingTarget> toBeMasked) {
        List<String> rules = new ArrayList<>();

        String prefix = resourceName + ".";

        for (Map.Entry<String, DataMaskingTarget> entry : toBeMasked.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                rules.add(entry.getValue().getProviderType().name());
            }
        }

        return rules;
    }

    private FHIRMaskingProvider(String complexType, MaskingConfiguration maskingConfiguration, Set<String> maskedFields, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        String enabledResourcesConf = maskingConfiguration.getStringValue("fhir.resources.enabled");
        String[] enabledResources = enabledResourcesConf.split(",");

        this.maskingProviderMap = new HashMap<>();

        for (String enabledResource : enabledResources) {
            String basePath = maskingConfiguration.getStringValue("fhir.basePath." + enabledResource);
            if (basePath == null) {
                basePath = "/fhir/" + enabledResource;
            }

            Collection<String> maskingConfigurations = loadRulesForResource(enabledResource, maskingConfiguration);
            FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration(basePath, maskingConfigurations, maskingConfiguration);
            this.maskingProviderMap.put(enabledResource.toLowerCase(), new FHIRGenericMaskingProvider(resourceConfiguration,
                    maskingConfiguration, maskedFields, this.factory));
        }

    }

    /**
     * Constructs a FHIRMaskingProvider.
     * @param random the random
     * @param maskingConfiguration the maskingConfiguration
     * @param maskedFields the maskedFields
     * @param toBeMasked the toBeMasked
     * @param factory the factory
     */
    public FHIRMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration,
                               Set<String> maskedFields, Map<String, DataMaskingTarget> toBeMasked, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        String enabledResourcesConf = maskingConfiguration.getStringValue("fhir.resources.enabled");
        Set<String> enabledResources = new HashSet<>(Arrays.asList(enabledResourcesConf.split(",")));

        this.maskingProviderMap = new HashMap<>();

        for (String enabledResource : enabledResources) {
            String basePath = maskingConfiguration.getStringValue("fhir.basePath." + enabledResource);
            if (basePath == null) {
                basePath = "/fhir/" + enabledResource;
            }

            Collection<String> maskingConfigurations = loadRulesForResource(enabledResource, toBeMasked);
            FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration(basePath, maskingConfigurations, maskingConfiguration);
            this.maskingProviderMap.put(enabledResource.toLowerCase(), new FHIRGenericMaskingProvider(resourceConfiguration,
                    maskingConfiguration, maskedFields, this.factory));
        }
    }

    private JsonNode maskResource(JsonNode resource) {
        if (resource == null || resource.isNull() || !resource.isObject()) {
            return resource;
        }

        JsonNode resourceNode = resource.get("resourceType");

        if (resourceNode == null || resourceNode.isNull() || !resourceNode.isTextual()) {
            return resource;
        }

        String resourceType = resourceNode.asText();

        String resourceTypeName = resourceType.toLowerCase();
        FHIRGenericMaskingProvider maskingProvider = maskingProviderMap.get(resourceTypeName);

        if (maskingProvider != null) {
            return maskingProvider.mask(resource);
        }

        return resource;
    }

    /*
    The masking provider works on FHIR objects that look like this:
        {
                "resourceType":"ContactPoint",
                "system":"email",
                "value":"p.heuvel@gmail.com",
                "use":"home"
        }

     */

    /**
     * Masks a JsonNode object.
     * @param resource the JsonNode to mask
     * @return the masked JsonNode
     */
    public String mask(JsonNode resource) {
        JsonNode maskedValue = maskResource(resource);

        if (maskedValue == null) {
            return null;
        }

        return maskedValue.toString();
    }

    /**
     * Masks a String object.
     * @param identifier the String to mask
     * @return the masked String
     */
    public String mask(String identifier) {

        try {
            JsonNode resource = JsonUtils.MAPPER.readTree(identifier);

            String maskedResource = mask(resource);
            if (maskedResource == null) {
                return identifier;
            }

            return maskedResource;

        } catch (IOException e) {
            return "";
        }
    }
}

