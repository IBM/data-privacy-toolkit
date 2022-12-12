/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.util.EntropyUtilities;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntropyUtilitiesTest {

    @Test
    public void testEntropyCalculation() {

        Collection<Long> values = Arrays.asList(2L, 3L);

        double expected = - (0.4*Math.log(0.4) + 0.6*Math.log(0.6));
        double entropy = EntropyUtilities.calculateEntropy(values, 5L);

        assertEquals(expected, entropy, 0.1);

    }
}
