/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterHistogramTest {

    @Test
    public void testHistogram() {

        String input = "aaaa";

        int[] histogram = CharacterHistogram.generateHistogram(input);

        assertEquals(4, histogram[0]);
        for(int i = 1; i < histogram.length; i++) {
            assertEquals(0, histogram[i]);
        }

        input = "abcde!!#$$90809";
        histogram = CharacterHistogram.generateHistogram(input);

        assertEquals(1, histogram[0]);
        assertEquals(1, histogram[1]);
        assertEquals(1, histogram[2]);
        assertEquals(1, histogram[3]);
        assertEquals(1, histogram[4]);
        for(int i = 5; i < histogram.length; i++) {
            assertEquals(0, histogram[i]);
        }
    }
}


