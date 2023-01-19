/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LaplaceTest {

    @Test
    public void testNegativeEpsilonThroughOptions() {
        assertThrows(RuntimeException.class, () -> {
            Laplace mechanism = new Laplace();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(-10);
            mechanism.setOptions(options);
        });
    }

    @Test
    public void testInvalidBounds() {
        assertThrows(RuntimeException.class, () -> {
            Laplace mechanism = new Laplace();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(10);
            options.setBounds(10, 4);

            mechanism.setOptions(options);
        });
    }

    @Test
    public void testCorrectResultsWithOptions() {

        Laplace mechanism = new Laplace();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(10);
        options.setBounds(-10, 10);
        mechanism.setOptions(options);

        int count = 0;
        
        for(int i = 0; i < 1000; i++) {
            String randomized = mechanism.randomise("1.0");
            Double d = Double.parseDouble(randomized);
            
            if (d != 1.0) {
                count++;
            }
        }
        
        assertTrue(count > 0);

    }

    @Test
    @Disabled
    public void testPerformance() {

        Laplace mechanism = new Laplace();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(10);
        options.setBounds(-10, 10);
        mechanism.setOptions(options);

        long start = System.currentTimeMillis();

        for(int i = 0; i < 1000000; i++) {
            String randomized = mechanism.randomise("1.0");
            if (randomized == null) {
                System.out.println("oops");
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("total time: " + (end - start));
    }
}
