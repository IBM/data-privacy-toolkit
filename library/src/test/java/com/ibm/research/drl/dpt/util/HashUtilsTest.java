/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HashUtilsTest {

    @Test
    public void testHashUtils() {
        long l = HashUtils.longFromHash("000000");
        assertNotNull(l);

        long originalValue = l;
        for(int i = 0; i < 1000; i++) {
            l = HashUtils.longFromHash("000000");
            assertEquals(originalValue, l);
        }
    }

    @Test
    public void testNull() {
        Long l = HashUtils.longFromHash(null);
        assertNotNull(l);
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;

        long start = System.currentTimeMillis();

        for(int i = 0; i < N; i++) {
            var l = HashUtils.longFromHash("000000");
        }

        System.out.println("N: " + N + ", time: " + (System.currentTimeMillis() - start));
    }
}
