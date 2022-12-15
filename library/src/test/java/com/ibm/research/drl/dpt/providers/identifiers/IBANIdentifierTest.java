/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IBANIdentifierTest {

    @Test
    public void testIsOfThisType() {
        IBANIdentifier identifier = new IBANIdentifier();

        String iban = "IE71WZXH31864186813343";
        assertTrue(identifier.isOfThisType(iban));

        iban = "GR98";
        assertFalse(identifier.isOfThisType(iban));
    }
}
