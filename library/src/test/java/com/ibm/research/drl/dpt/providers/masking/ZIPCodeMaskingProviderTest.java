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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZIPCodeMaskingProviderTest {

    @Test
    public void testMinimumPopulationNoPrefixDoesNotMatchMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", false);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 20000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 18750
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("000", maskedZipcode);
    }

    @Test
    public void testMinimumPopulationNoPrefixMatchesMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", false);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 18000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 18750
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("00601", maskedZipcode);
    }

    @Test
    public void testMinimumPopulationWithPrefixMatchesMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationPrefixDigits", 3);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 18000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 1214568
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("006", maskedZipcode);
    }

    @Test
    public void testMinimumPopulationWithPrefixDoesNotMatchMinimum() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationPrefixDigits", 3);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 10000000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601"; //we know its population is 1214568
        String maskedZipcode = maskingProvider.mask(zipcode);

        assertEquals("000", maskedZipcode);
    }

    @Test
    public void testNoMinimumPopulation() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("zipcode.mask.countryCode", "US");
        maskingConfiguration.setValue("zipcode.mask.requireMinimumPopulation", false);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationUsePrefix", true);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulationPrefixDigits", 3);
        maskingConfiguration.setValue("zipcode.mask.minimumPopulation", 10000000);

        MaskingProvider maskingProvider = new ZIPCodeMaskingProvider(maskingConfiguration);

        String zipcode = "00601";

        int randomOK = 0;

        for(int i = 0; i < 1000; i++) {
            String maskedZipcode = maskingProvider.mask(zipcode);
            if(!maskedZipcode.equals(zipcode)) {
                randomOK++;
            }
        }

        assertTrue(randomOK > 0);
    }
}

