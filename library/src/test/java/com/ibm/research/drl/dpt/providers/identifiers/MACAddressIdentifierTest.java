/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MACAddressIdentifierTest {

    @Test
    public void testIsOfThisType() throws Exception {
        MACAddressIdentifier identifier = new MACAddressIdentifier();

        assertTrue(identifier.isOfThisType("00:0a:95:9d:68:16"));
        assertTrue(identifier.isOfThisType("00:0A:95:9D:68:16"));
        assertFalse(identifier.isOfThisType("somethin else"));
    }

}
