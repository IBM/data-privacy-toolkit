/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientIDIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        PatientIDIdentifier identifier = new PatientIDIdentifier();

        String[] validIDs = {"553-455-222-566"};
        for (String id: validIDs) {
            assertTrue(identifier.isOfThisType(id));
        }

        String[] invalidIDs = {"55A-455-222-566", "555-444-333"};
        for (String id: invalidIDs) {
            assertFalse(identifier.isOfThisType(id));
        }
    }
}
