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
import com.ibm.research.drl.dpt.providers.identifiers.RaceEthnicityIdentifier;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class RaceEthnicityMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        MaskingProvider maskingProvider = new RaceEthnicityMaskingProvider();
        RaceEthnicityIdentifier identifier = new RaceEthnicityIdentifier();

        String originalRace = "white";

        int randomizationOK = 0;

        for(int i = 0; i < 10; i++) {
            String maskedRace = maskingProvider.mask(originalRace);
            assertTrue(identifier.isOfThisType(maskedRace));
            if (!maskedRace.equals(originalRace)) {
                randomizationOK++;
            }
        }
        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testProbabilistic() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("race.mask.probabilityBased", true);

        RaceEthnicityMaskingProvider maskingProvider = new RaceEthnicityMaskingProvider(maskingConfiguration);
        RaceEthnicityIdentifier identifier = new RaceEthnicityIdentifier();

        String originalReligion = "White";

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String maskedReligion = maskingProvider.mask(originalReligion);
            assertTrue(identifier.isOfThisType(maskedReligion));

            if (!maskedReligion.equals(originalReligion)) {
                randomizationOK++;
            }
        }
        
        assertTrue(randomizationOK > 0);
    }
    
    @Test
    public void testLocalization() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new RaceEthnicityMaskingProvider();

        String greekRace = "Ασιάτης";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.RACE_ETHNICITY, Collections.singletonList("gr"));
        Set<String> greekRaces = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekRaces.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 100; i++) {
            String maskedRace = maskingProvider.mask(greekRace);
            assertTrue(greekRaces.contains(maskedRace.toUpperCase()));
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration
        };

        String[] originalValues = new String[]{"white"};

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            RaceEthnicityMaskingProvider maskingProvider = new RaceEthnicityMaskingProvider(maskingConfiguration);

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

