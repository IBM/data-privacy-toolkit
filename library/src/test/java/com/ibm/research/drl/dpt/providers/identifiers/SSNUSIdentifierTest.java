/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.SSNUS;
import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SSNUSIdentifierTest {

    @Test
    public void testOfThisType() {
        SSNUSIdentifier identifier = new SSNUSIdentifier();

        String ssnValue = "123-12-1234";
        assertTrue(identifier.isOfThisType(ssnValue));

        ssnValue = "1234-12-1234";
        assertFalse(identifier.isOfThisType(ssnValue));
        ssnValue = "12a-12-1234";
        assertFalse(identifier.isOfThisType(ssnValue));
        ssnValue = "123-123-1234";
        assertFalse(identifier.isOfThisType(ssnValue));
        ssnValue = "123-12-12345";
        assertFalse(identifier.isOfThisType(ssnValue));
        ssnValue = "123-12-a234";
        assertFalse(identifier.isOfThisType(ssnValue));
        ssnValue = "123-1b-1234";
        assertFalse(identifier.isOfThisType(ssnValue));

        //invalid group number
        ssnValue = "000-12-1234";
        assertFalse(identifier.isOfThisType(ssnValue));
    }

    @Test
    public void testWithPrefix() {
        SSNUSIdentifier identifier = new SSNUSIdentifier();

        Tuple<String, Tuple<Boolean, Tuple<Integer, Integer>>>[] expectedResults = new Tuple[]{
                new Tuple<>("SS # of 123-44-1234", new Tuple<>(true, new Tuple<>(8, 11))),
                new Tuple<>("SS: 123-44-1234", new Tuple<>(true, new Tuple<>(4, 11)))
        };

        for(Tuple<String, Tuple<Boolean, Tuple<Integer, Integer>>> expectedResult: expectedResults) {
            String value = expectedResult.getFirst();
            Tuple<Boolean, Tuple<Integer, Integer>> expected = expectedResult.getSecond();

            Tuple<Boolean, Tuple<Integer, Integer>> actual = identifier.isOfThisTypeWithOffset(value);
            assertEquals(expected.getFirst(), actual.getFirst());

            assertEquals(expected.getSecond().getFirst(), actual.getSecond().getFirst());
            assertEquals(expected.getSecond().getSecond(), actual.getSecond().getSecond());
        }
    }

    @Test
    public void testParse() {
        SSNUSIdentifier identifier = new SSNUSIdentifier();

        String ssnValue = "123-12-1234";
        SSNUS ssn = identifier.parseSSNUS(ssnValue);
        assertNotNull(ssn);
        assertEquals("123", ssn.getAreaNumber());
        assertEquals("12", ssn.getGroup());
        assertEquals("1234", ssn.getSerialNumber());
    }
}
