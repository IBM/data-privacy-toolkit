/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IMSIIdentifierTest {

    @Test
    public void testIsOfThisType() {
        Identifier identifier = new IMSIIdentifier();

        String imsi = "310150123456789";
        assertTrue(identifier.isOfThisType(imsi));
    }

    @Test
    public void testInvalid() {
        Identifier identifier = new IMSIIdentifier();

        String imsi = "foobar";
        assertFalse(identifier.isOfThisType(imsi));

        imsi = "1234455666"; //invalid length
        assertFalse(identifier.isOfThisType(imsi));

        imsi = "1234455666123a5"; //contains letter
        assertFalse(identifier.isOfThisType(imsi));

        imsi = "000000566612345"; //invalid MCC, MNC
        assertFalse(identifier.isOfThisType(imsi));
    }
}
