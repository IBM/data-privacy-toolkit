/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneralizationMaskingProviderTest {
    @Test
    public void testGeneralizationGender() throws Exception {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("generalization.masking.hierarchyLevel", 1);
        maskingConfiguration.setValue("generalization.masking.hierarchyName", "gender");

        JsonNode hierarchyMapNode = JsonUtils.MAPPER.readTree("{\"gender\": \"GENDER\"}");
        maskingConfiguration.setValue("generalization.masking.hierarchyMap", hierarchyMapNode);

        MaskingProvider maskingProvider = new GeneralizationMaskingProvider(
                new SecureRandom(), maskingConfiguration);

        String originalValue = "Male";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals("*", maskedValue);
    }

    @Test
    public void testGeneralizationGenderLevel0() throws Exception {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("generalization.masking.hierarchyLevel", 0);
        maskingConfiguration.setValue("generalization.masking.hierarchyName", "gender");

        JsonNode hierarchyMapNode = JsonUtils.MAPPER.readTree("{\"gender\": \"GENDER\"}");
        maskingConfiguration.setValue("generalization.masking.hierarchyMap", hierarchyMapNode);

        MaskingProvider maskingProvider = new GeneralizationMaskingProvider(new SecureRandom(), maskingConfiguration);

        String originalValue = "Male";
        String maskedValue = maskingProvider.mask(originalValue);

        assertEquals(maskedValue.toUpperCase(), originalValue.toUpperCase());
    }
}