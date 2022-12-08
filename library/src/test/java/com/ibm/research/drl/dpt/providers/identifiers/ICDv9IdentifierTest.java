/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ICDv9IdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        ICDv9Identifier identifier = new ICDv9Identifier();

        String icdCode = "004.8";
        assertTrue(identifier.isOfThisType(icdCode));

        String icdShortName = "Staph Food Poisoning";
        assertTrue(identifier.isOfThisType(icdShortName));

        String icdShortNameLower = "Staph Food Poisoning".toLowerCase();
        assertTrue(identifier.isOfThisType(icdShortNameLower));

        String icdFullName = "Staphylococcal Food Poisoning";
        assertTrue(identifier.isOfThisType(icdFullName));
    }
}
