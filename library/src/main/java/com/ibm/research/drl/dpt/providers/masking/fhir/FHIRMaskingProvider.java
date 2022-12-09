/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.*;

public class FHIRMaskingProvider extends AbstractComplexMaskingProvider<String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, FHIRGenericMaskingProvider> maskingProviderMap ;

    public FHIRMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("fhir", maskingConfiguration, new HashSet<String>(), factory);
    }

    public FHIRMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("fhir", maskingConfiguration, new HashSet<>(), factory);
    }

    public FHIRMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, Set<String> maskedFields, MaskingProviderFactory factory) {
        this("fhir", maskingConfiguration, maskedFields, factory);
    }

    private InputStream loadResource(String name, boolean external) {
        if (external) {
            try {
                return new FileInputStream(name);
            }
            catch (Exception e) {
                throw new RuntimeException("unable to load external resource: " + name);
            }
        }

        return this.getClass().getResourceAsStream(name);
    }

    public static Collection<String> loadRulesForResource(String resourceName, MaskingConfiguration maskingConfiguration) {
        return maskingConfiguration.getStringValueWithPrefixMatch("fhir.maskingConf." + resourceName + ".");
    }

    public static Collection<String> loadRulesForResource(String resourceName, Map<String, DataMaskingTarget> toBeMasked) {
        List<String> rules = new ArrayList<>();
        
        String prefix = resourceName + ".";
        
        for(Map.Entry<String, DataMaskingTarget> entry: toBeMasked.entrySet()) {
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

        for(String enabledResource: enabledResources) {
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
    
    public FHIRMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration,
                               Set<String> maskedFields, Map<String, DataMaskingTarget> toBeMasked, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        String enabledResourcesConf = maskingConfiguration.getStringValue("fhir.resources.enabled");
        Set<String> enabledResources = new HashSet<>(Arrays.asList(enabledResourcesConf.split(",")));
        
        this.maskingProviderMap = new HashMap<>();

        for(String enabledResource: enabledResources) {
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
            return  maskingProvider.mask(resource);
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

    public String mask(JsonNode resource) {
        JsonNode maskedValue = maskResource(resource);

        if(maskedValue == null) {
            return null;
        }

        return maskedValue.toString();
    }

    public String mask(String identifier) {

        try {
            JsonNode resource = mapper.readTree(identifier);

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

