/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRResourceMaskingConfigurationTest {

    @Test
    public void testParsing() throws Exception {
        Collection<String> deviceMaskConf = new ArrayList<>();
        deviceMaskConf.add("/owner:Reference");

        FHIRResourceMaskingConfiguration resourceConfiguration = new FHIRResourceMaskingConfiguration("/fhir/Device", deviceMaskConf);

        assertEquals("/fhir/Device", resourceConfiguration.getBasePath());
        assertEquals(1, resourceConfiguration.getFields().size());
        assertEquals("/owner", resourceConfiguration.getFields().get(0).getPath());
        assertEquals("Reference", resourceConfiguration.getFields().get(0).getFhirType());
    }

}


