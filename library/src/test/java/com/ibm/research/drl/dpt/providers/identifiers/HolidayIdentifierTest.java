/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;



public class HolidayIdentifierTest {
    
    @Test
    public void testIsOfThisType() {
        HolidayIdentifier identifier = new HolidayIdentifier();
        
        String[] validTerms = new String[] {
                "Christmas",
                "Easter",
                "New Year's Eve",
                "Hanukkah"
        };
        
        for(String term: validTerms) {
            assertTrue(identifier.isOfThisType(term), term);
        }
        
    }
}
