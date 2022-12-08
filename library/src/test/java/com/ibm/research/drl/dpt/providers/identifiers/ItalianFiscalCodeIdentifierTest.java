/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItalianFiscalCodeIdentifierTest {
    @Test
    public void checkValid() {
        Identifier identifier = new ItalianFiscalCodeIdentifier();

        for (String valid : new String[]{
                "BRGSFN81P10L682K",
                "MRTMTT25D09F205Z",
                "MLLSNT82P65Z404U",
        }) {
            assertTrue(identifier.isOfThisType(valid), valid);
        }
    }

    @Test
    public void testInvalid() {
        Identifier identifier = new ItalianFiscalCodeIdentifier();

        for (String invalid : new String[]{
                "MLLSNT82P65Z4049",
                "MLLSNT82P65Z404D",
                "MLLSNT82P65Z404J",
                "MLL-NT8-P65Z404U",
                "MLLSNT82P65ZJDS404U",
                "MLLSNT8204U"
        }) {
            assertFalse(identifier.isOfThisType(invalid), invalid);
        }
    }
}