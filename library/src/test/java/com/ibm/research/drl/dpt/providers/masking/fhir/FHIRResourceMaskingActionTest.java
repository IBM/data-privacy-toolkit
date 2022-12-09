/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FHIRResourceMaskingActionTest {

    @Test
    public void testPaths() {
        FHIRResourceMaskingAction maskingAction = new FHIRResourceMaskingAction(
                "/fhir/Device/owner/details",
                "/owner/details",
                null,
                null,
                false
        );

        String[] paths = maskingAction.getPaths();
        assertEquals(2, paths.length);
        assertEquals("owner", paths[0]);
        assertEquals("details", paths[1]);
    }
}


