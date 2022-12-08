/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class POBOXIdentifierTest {

    @Test
    public void recognizesValidPOBoxes() {
        String[] validPOBoxes = {
                "P.O. BOX 334",
                "P.O.BOX-334",
                "PO BOX 297",
                "PO BOX 1001",
                "PO Box 41",
                "POBOX 14321412",
                "POBOX 14321412",

                "PO BOX -0145",
                "PO BOX 00145",
                "PO BOX G",
                "PO BOX 11890",
        };

        Identifier identifier = new POBOXIdentifier();

        for (final String POBOX : validPOBoxes) {
            assertTrue(identifier.isOfThisType(POBOX), POBOX);
        }
    }

}
