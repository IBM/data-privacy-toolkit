/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicineIdentifierTest {

    @Test
    public void testIsOfThisType() {
        Identifier identifier = new MedicineIdentifier();

        String medicine = "Drotrecogin alfa";
        assertTrue(identifier.isOfThisType(medicine));
        medicine = "Drotrecogin ALFA";
        assertTrue(identifier.isOfThisType(medicine));
    }
}
