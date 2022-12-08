package com.ibm.research.drl.dpt.util;
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MutableTupleTest {

    @Test
    public void testMutation() {
        MutableTuple<String, String> tuple = new MutableTuple<>("a", "b");

        assertEquals("a", tuple.getFirst());
        assertEquals("b", tuple.getSecond());

        tuple.setFirst("c");
        assertEquals("c", tuple.getFirst());
        assertEquals("b", tuple.getSecond());

        tuple.setSecond("d");
        assertEquals("c", tuple.getFirst());
        assertEquals("d", tuple.getSecond());
    }
}
