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
import com.ibm.research.drl.dpt.providers.identifiers.CountyIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountyMaskingProviderTest {

    @Test
    public void testPseudorandom() {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("county.mask.pseudorandom", true);

        MaskingProvider maskingProvider = new CountyMaskingProvider(maskingConfiguration);

        String originalCity = "Italy";
        String firstMask = maskingProvider.mask(originalCity);

        for(int i = 0; i < 100; i++) {
            String maskedCity = maskingProvider.mask(originalCity);
            assertEquals(firstMask, maskedCity);
        }

    }

    @Test
    public void testMask() {
        Identifier identifier = new CountyIdentifier();
        MaskingProvider maskingProvider = new CountyMaskingProvider();

        String originalValue = "Pendleton County";
        assertTrue(identifier.isOfThisType(originalValue));

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(originalValue);
            assertTrue(identifier.isOfThisType(maskedValue));

            if (!maskedValue.equals(originalValue)) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }
}

