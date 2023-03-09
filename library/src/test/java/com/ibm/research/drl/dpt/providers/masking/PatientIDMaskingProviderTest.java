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
import com.ibm.research.drl.dpt.providers.identifiers.PatientIDIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PatientIDMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        PatientIDMaskingProvider patientIDMaskingProvider = new PatientIDMaskingProvider();
        String originalID = "555-666-777-888";
        String maskedID = patientIDMaskingProvider.mask(originalID);

        assertTrue(new PatientIDIdentifier().isOfThisType(maskedID));
        assertNotEquals(maskedID, originalID);

        //test preservation of 1 group
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("patientID.groups.preserve", 1);
        patientIDMaskingProvider = new PatientIDMaskingProvider(new SecureRandom(), configuration);
        maskedID = patientIDMaskingProvider.mask(originalID);

        assertTrue(new PatientIDIdentifier().isOfThisType(maskedID));
        assertTrue(maskedID.startsWith("555-"));
        assertNotEquals(maskedID, originalID);
    }

    @Test
    public void testMaskInvalidValue() throws Exception {
        PatientIDMaskingProvider patientIDMaskingProvider = new PatientIDMaskingProvider();

        String originalID = "junkID";
        String maskedID = patientIDMaskingProvider.mask(originalID);

        assertTrue(new PatientIDIdentifier().isOfThisType(maskedID));
        assertNotEquals(maskedID, originalID);

    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration preserveConfiguration = new DefaultMaskingConfiguration("preserve");
        preserveConfiguration.setValue("patientID.groups.preserve", 1);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, preserveConfiguration
        };

        String[] originalValues = new String[]{
                "555-666-777-888"
        };

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            PatientIDMaskingProvider maskingProvider = new PatientIDMaskingProvider(maskingConfiguration);

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %s: %d operations took %d milliseconds (%f per op)%n",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N);
            }
        }
    }
}
