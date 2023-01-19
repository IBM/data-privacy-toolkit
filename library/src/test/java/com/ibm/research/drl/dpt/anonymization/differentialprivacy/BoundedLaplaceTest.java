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


public class BoundedLaplaceTest {

    @Test
    public void testNegativeEpsilonThroughOptions() {
        assertThrows(RuntimeException.class, () -> {
            BoundedLaplace mechanism = new BoundedLaplace();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(-10);

            mechanism.setOptions(options);
        });
    }

    @Test
    public void testInvalidBounds() {
        assertThrows(RuntimeException.class, () -> {
            BoundedLaplace mechanism = new BoundedLaplace();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(10);
            options.setBounds(10, 4);

            mechanism.setOptions(options);
        });
    }

    @Test
    public void testWayOffTheDomain() {

        DPMechanism mechanism = new BoundedLaplace();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(5);
        options.setBounds(-10, 10);
        mechanism.setOptions(options);

        for(int i = 0; i < 1; i++) {
            String randomized = mechanism.randomise("100.0");
            double d = Double.parseDouble(randomized);

            assertTrue(d >= -10.0);
            assertTrue(d <= 10.0);
        }
    }
    
    @Test
    public void testCorrectResultsWithOptions() {

        BoundedLaplace mechanism = new BoundedLaplace();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(10);
        options.setBounds(-10, 10);
        mechanism.setOptions(options);

        int count = 0;

        for(int i = 0; i < 1000; i++) {
            String randomized = mechanism.randomise("1.0");
            double d = Double.parseDouble(randomized);

            assertTrue(d >= -10.0);
            assertTrue(d <= 10.0);

            if (d != 1.0) {
                count++;
            }
        }

        assertTrue(count > 0);

    }


    @Test
    @Disabled
    public void testPerformance() {

        BoundedLaplace mechanism = new BoundedLaplace();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(1);
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
