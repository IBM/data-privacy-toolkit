/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HospitalIdentifierTest {

    @Test
    public void testIdentifier() {
        Identifier identifier = new HospitalIdentifier();

        String hospitalName = "York Hospital";
        assertTrue(identifier.isOfThisType(hospitalName));

        hospitalName = "york hospital";
        assertTrue(identifier.isOfThisType(hospitalName));
    }

    @Test
    public void testIdentifierGreek() {
        Identifier identifier = new HospitalIdentifier();

        String hospitalName = "ΠΕΠΑΓΝΗ";
        assertTrue(identifier.isOfThisType(hospitalName));
    }
}
