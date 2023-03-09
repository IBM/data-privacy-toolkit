/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinaryTest {
    
    @Test
    public void testNegativeEpsilonThroughOptions() {
        assertThrows(RuntimeException.class, () -> {
            Binary binaryMechanism = new Binary();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(-10);
            binaryMechanism.setOptions(options);
        });
    }

    @Test
    public void testUndefinedEpsilon() {
        assertThrows(RuntimeException.class, () -> {
            Binary binaryMechanism = new Binary();
            String randomized = binaryMechanism.randomise("true");
            System.out.println(randomized);
        });
    }

    @Test
    public void testUndefinedBinaryValues() {
        assertThrows(RuntimeException.class, () -> {
            Binary binaryMechanism = new Binary();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(10);
            binaryMechanism.setOptions(options);

            String randomized = binaryMechanism.randomise("true");
            System.out.println(randomized);
        });
    }

    @Test
    public void testBinaryValuesAreTheSame() {
        assertThrows(RuntimeException.class, () -> {
            Binary binaryMechanism = new Binary();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(10);
            options.setBinaryValues("yes", "yes");
            binaryMechanism.setOptions(options);

            String randomized = binaryMechanism.randomise("yes");
            System.out.println(randomized);
        });
    }
    
    @Test
    public void testCustomBinaryData() {

        Binary binaryMechanism = new Binary();

        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setBinaryValues("yes", "no");
        options.setEpsilon(10);
        binaryMechanism.setOptions(options);
        
        String randomized = binaryMechanism.randomise("yes");
        assertTrue(randomized.equals("yes") || randomized.equals("no"));
    }
    
    @Test
    public void testCorrectResults() {
        
        Binary binaryMechanism = new Binary();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setBinaryValues("true", "false");
        options.setEpsilon(1);
        binaryMechanism.setOptions(options);
        
        int countFalse = 0;
        int countTrue = 0;
        
        for(int i = 0; i < 1000; i++) {
            String randomized = binaryMechanism.randomise("true");
            
            if (randomized.equals("false")) {
                countFalse++;
            }
            else if (randomized.equals("true")) {
                countTrue++;
            }
            else {
                assertFalse(true, randomized);
            }
        }
        
        assertTrue(countFalse > 0);
        assertTrue(countTrue > 0);
        assertTrue(countTrue > countFalse);
    }
    
    @Test
    @Disabled
    public void testPerformance() {
        Binary binaryMechanism = new Binary();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setBinaryValues("true", "false");
        options.setEpsilon(1);
        binaryMechanism.setOptions(options);
        
        long start = System.currentTimeMillis();

        for(int i = 0; i < 1000000; i++) {
            String randomized = binaryMechanism.randomise("true");
            if (randomized == null) {
                System.out.println("oops");
            }
        }
        
        long end = System.currentTimeMillis();

        System.out.println("total time: " + (end - start));
    }
}

