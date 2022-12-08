/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaritalStatusIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        MaritalStatusIdentifier maritalStatusIdentifier = new MaritalStatusIdentifier();
        String status = "Single";
        assertTrue(maritalStatusIdentifier.isOfThisType(status));
    }
}
