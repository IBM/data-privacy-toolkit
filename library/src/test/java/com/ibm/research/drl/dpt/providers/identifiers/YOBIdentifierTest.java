/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YOBIdentifierTest {

    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);


    @Test
    public void testYOBIdentifier() {
        Identifier identifier = new YOBIdentifier();

        assertTrue(identifier.isOfThisType("1950"));
        assertTrue(identifier.isOfThisType("" + currentYear));
        assertFalse(identifier.isOfThisType("" + (currentYear + 1)));
        assertFalse(identifier.isOfThisType("" + (currentYear - 101)));
    }
}
