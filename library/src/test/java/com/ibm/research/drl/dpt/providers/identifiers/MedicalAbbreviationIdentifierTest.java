/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/

package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MedicalAbbreviationIdentifierTest {
    
    @Test
    public void testIsOfThisType() {
        MedicalAbbreviationIdentifier identifier = new MedicalAbbreviationIdentifier();
        
        String[] validTerms = {
                "AC",
                "ETT"
        };
        
        for(String term: validTerms) {
            assertTrue(identifier.isOfThisType(term), term);
        }

        String[] invalidTerms = {
                "ac",
                "John",
                "Jack",
                "A",
                "Date"
        };

        for(String term: invalidTerms) {
            assertFalse(identifier.isOfThisType(term), term);
        } 
    }
}
