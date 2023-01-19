/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeometricTest {
    @Test
    public void testNegativeEpsilonThroughOptions() {
        assertThrows(RuntimeException.class, () -> {
            Geometric mechanism = new Geometric();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(-10);
            mechanism.setOptions(options);
        });
    }

    @Test
    public void testInvalidBounds() {
        assertThrows(RuntimeException.class, () -> {
            Geometric mechanism = new Geometric();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(10);
            options.setBounds(10, 4);

            mechanism.setOptions(options);
        });
    }

    @Test
    public void testCorrectResultsWithOptions() {
        Geometric mechanism = new Geometric();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setEpsilon(10);
        options.setBounds(-10, 10);
        mechanism.setOptions(options);

        int count = 0;

        for(int i = 0; i < 1000; i++) {
            String randomized = mechanism.randomise("1.0");
            double d = Double.parseDouble(randomized);

            if (d != 1.0) {
                count++;
            }
        }

        assertTrue(count > 0);
    }

    @Test
    public void testValidateMechanismOutput() {
        Geometric mechanism = new Geometric();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();

        double epsilon = 10;
        double range = 10;
        options.setEpsilon(epsilon);
        options.setBounds(-range, range);
        mechanism.setOptions(options);

        double countInput = 0;
        double countInputPlusOne = 0;

        for(int i = 0; i < 100000; i++) {
            String randomized = mechanism.randomise("1.0");
            double d = Double.parseDouble(randomized);

            if (d == 1.0) {
                countInput += 1.0;
            } else if (d == 2.0) {
                countInputPlusOne += 1.0;
            }
        }

        countInput *= Math.exp(- epsilon / 2 / range);

        assertThat(countInput, allOf(greaterThan(countInputPlusOne * 0.9), lessThan(countInputPlusOne * 1.1)));
    }

    @Test
    @Disabled
    public void testPerformance() {
        Geometric mechanism = new Geometric();
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

