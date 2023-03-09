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
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.ICDv9Identifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ICDv9MaskingProviderTest {

    @Test
    public void testMaskConvertToCategory() {
        MaskingProvider maskingProvider = new ICDv9MaskingProvider();

        // default configuration is to convert to category
        String originalICD = "002.0";
        String maskedICD = maskingProvider.mask(originalICD);
        assertNotEquals(originalICD, maskedICD);
        assertEquals("002", maskedICD);

        originalICD = "Typhoid Fever";
        maskedICD = maskingProvider.mask(originalICD);
        assertNotEquals(originalICD, maskedICD);
        assertEquals("Typhoid and paratyphoid fevers", maskedICD);
    }

    @Test
    @Disabled
    public void testPerformance() {
        MaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();
        MaskingConfiguration chapterConfiguration = new DefaultMaskingConfiguration();

        chapterConfiguration.setValue("icd.randomize.category", false);
        chapterConfiguration.setValue("icd.randomize.chapter", true);

        MaskingConfiguration[] configurations = new MaskingConfiguration[]{defaultMaskingConfiguration, chapterConfiguration};

        for (MaskingConfiguration configuration : configurations) {

            ICDv9MaskingProvider maskingProvider = new ICDv9MaskingProvider(configuration);

            int N = 1000000;
            String[] originalICDs = {"002.0", "Typhoid Fever"};

            for (String originalICD : originalICDs) {

                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedICD = maskingProvider.mask(originalICD);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %d operations took %d milliseconds (%f per op)%n",
                        originalICD, N, diff, (double) diff / N);
            }
        }
    }

    @Test
    public void testMaskConvertToChapter() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();

        configuration.setValue("icd.randomize.category", false);
        configuration.setValue("icd.randomize.chapter", true);

        MaskingProvider maskingProvider = new ICDv9MaskingProvider(configuration);

        // default configuration is to convert to category
        String originalICD = "002.0";
        String maskedICD = maskingProvider.mask(originalICD);
        assertNotEquals(originalICD, maskedICD);
        assertEquals("001-139", maskedICD);

        //test that format is preserved
        originalICD = "Typhoid Fever";
        maskedICD = maskingProvider.mask(originalICD);
        assertNotEquals(originalICD, maskedICD);
        assertEquals("infectious and parasitic diseases", maskedICD);

        //check that case is ignored
        originalICD = "Typhoid Fever".toLowerCase();
        maskedICD = maskingProvider.mask(originalICD);
        assertNotEquals(originalICD, maskedICD);
        assertEquals("infectious and parasitic diseases", maskedICD);
    }

    @Test
    public void testMaskInvalidValue() throws Exception {

        ICDv9MaskingProvider maskingProvider = new ICDv9MaskingProvider();
        ICDv9Identifier identifier = new ICDv9Identifier();

        String invalidValue = "adsadsad";
        String maskedValue = maskingProvider.mask(invalidValue);

        assertNotEquals(maskedValue, invalidValue);
        assertTrue(identifier.isOfThisType(maskedValue));
    }
}
