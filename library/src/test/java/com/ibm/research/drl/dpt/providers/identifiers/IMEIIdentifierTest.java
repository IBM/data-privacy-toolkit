/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IMEIIdentifierTest {

    @Test
    public void testIsOfThisType() {
        IMEIIdentifier identifier = new IMEIIdentifier();

        String imei = "012837001234567"; //OK
        assertTrue(identifier.isOfThisType(imei));

        imei = "012837001234561"; //invalid check digit
        assertFalse(identifier.isOfThisType(imei));

        imei = "12312313"; //short
        assertFalse(identifier.isOfThisType(imei));

        imei = "001013001a34567"; //contains letters
        assertFalse(identifier.isOfThisType(imei));
    }
}
