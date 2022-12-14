/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

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
