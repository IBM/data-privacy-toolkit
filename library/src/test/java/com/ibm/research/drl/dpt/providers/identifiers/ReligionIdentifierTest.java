/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReligionIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        ReligionIdentifier religionIdentifier = new ReligionIdentifier();
        String religion = "Buddhist";
        assertTrue(religionIdentifier.isOfThisType(religion));
    }
}
