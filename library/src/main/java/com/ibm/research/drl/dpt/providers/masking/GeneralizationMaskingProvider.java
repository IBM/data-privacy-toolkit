/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;

public class GeneralizationMaskingProvider implements MaskingProvider {
    private static final Logger log = LogManager.getLogger(GeneralizationMaskingProvider.class);

    private final int hierarchyLevel;
    private final GeneralizationHierarchy hierarchy;
    private final boolean randomizeOnFail;

    public GeneralizationMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.hierarchyLevel = maskingConfiguration.getIntValue("generalization.masking.hierarchyLevel");

        if (this.hierarchyLevel < 0) {
            String msg = "generalization.masking.hierarchyLevel must be >= 0";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        this.randomizeOnFail = maskingConfiguration.getBooleanValue("generalization.masking.randomizeOnFail");
        String hierarchyName = maskingConfiguration.getStringValue("generalization.masking.hierarchyName");
        JsonNode hierarchyMapNode = Objects.requireNonNull(maskingConfiguration.getJsonNodeValue("generalization.masking.hierarchyMap"));

        Map<String, GeneralizationHierarchy> hierarchyMap = AnonymizationOptions.hierarchiesFromJSON(hierarchyMapNode);

        this.hierarchy = hierarchyMap.get(hierarchyName);

        if (this.hierarchy == null) {
            String msg = "Unable to find the hierarchy with name: " + hierarchyName;
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public String mask(String identifier) {
        return hierarchy.encode(identifier, this.hierarchyLevel, this.randomizeOnFail);
    }
}
