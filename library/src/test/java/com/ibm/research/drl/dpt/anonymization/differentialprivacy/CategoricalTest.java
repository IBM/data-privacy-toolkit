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

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CategoricalTest {

    @Test
    public void testNegativeEpsilonThroughOptions() {
        assertThrows(RuntimeException.class, () -> {
            Categorical mechanism = new Categorical();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setHierarchy(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2")));
            options.setEpsilon(-10);
            mechanism.setOptions(options);
        });
    }

    @Test
    public void testNullHierarchy() {
        assertThrows(RuntimeException.class, () -> {
            Categorical mechanism = new Categorical();

            DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
            options.setEpsilon(10);
            options.setHierarchy(null);
            mechanism.setOptions(options);
        });
    }

    @Test
    public void testCorrectResults() {
        Categorical mechanism = new Categorical();

        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();

        List<String> terms = Arrays.asList("Red", "Green", "Blue");
        Set<String> values = new HashSet<>();

        for (String s : terms) {
            values.add(s.toUpperCase());
        }

        options.setHierarchy(GeneralizationHierarchyFactory.getGenericFromFixedSet(terms));
        options.setEpsilon(5);
        mechanism.setOptions(options);

        Random random = new Random();
        int countCorrect = 0;
        for(int i = 0; i < 1000; i++) {
            int randomPos = random.nextInt(terms.size());
            String randomized = mechanism.randomise(terms.get(randomPos));
            
            assertTrue(values.contains(randomized));

            if (randomized.equals(terms.get(randomPos).toUpperCase())) {
                countCorrect++;
            }
        }

        assertTrue(countCorrect > 0);
    }

    @Test
    @Disabled
    public void testPerformance() {

        Categorical mechanism = new Categorical();

        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();

        List<String> terms = Arrays.asList("Red", "Green", "Blue");
        Set<String> values = new HashSet<>();

        for (String s : terms) {
            values.add(s.toUpperCase());
        }

        options.setHierarchy(GeneralizationHierarchyFactory.getGenericFromFixedSet(terms));
        options.setEpsilon(5);
        mechanism.setOptions(options);
        
        long start = System.currentTimeMillis();

        for(int i = 0; i < 1000000; i++) {
            String randomized = mechanism.randomise(terms.get(i % 3));
            if (randomized == null) {
                System.out.println("oops");
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("total time: " + (end - start));
    }

    @Test
    @Disabled
    public void testDays() {
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        for(int i = Calendar.MONDAY; i <= Calendar.FRIDAY; i++) {
            hierarchy.add("" + i, "Weekday", "*");
        }

        hierarchy.add("" + Calendar.SATURDAY, "Weekend", "*");
        hierarchy.add("" + Calendar.SUNDAY, "Weekend", "*");

        Categorical mechanism = new Categorical();
        DifferentialPrivacyMechanismOptions options = new DifferentialPrivacyMechanismOptions();
        options.setHierarchy(hierarchy);
        options.setEpsilon(1.0);
        mechanism.setOptions(options);
        String originalDay = "" + Calendar.WEDNESDAY;

        int weekdays = 0;
        int weekends = 0;
        int sameDay = 0;

        for (int i=0;i<1000;i++) {
            int randomisedDay = Integer.parseInt(mechanism.randomise(originalDay));

            if (randomisedDay == Calendar.WEDNESDAY) {
                sameDay++;
            } else if (randomisedDay >= Calendar.MONDAY && randomisedDay <= Calendar.FRIDAY) {
                weekdays++;
            }
            else {
                weekends++;
            }
        }

        assertTrue(sameDay > weekdays/4);
        assertTrue(weekdays/4 > weekends/2);
    }
}
