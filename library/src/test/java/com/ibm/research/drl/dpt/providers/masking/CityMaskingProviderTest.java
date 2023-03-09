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
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CityMaskingProviderTest {

    @Test
    public void testMask() {
        CityMaskingProvider maskingProvider = new CityMaskingProvider();

        // different values
        String originalCity = "Dublin";
        String maskedCity = maskingProvider.mask(originalCity);
        assertNotEquals(originalCity, maskedCity);
    }

    @Test
    public void testPseudorandom() {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("city.mask.pseudorandom", true);

        MaskingProvider maskingProvider = new CityMaskingProvider(maskingConfiguration);

        String originalCity = "Dublin";
        String maskedCity = maskingProvider.mask(originalCity);

        String firstMask = maskedCity;

        for(int i = 0; i < 100; i++) {
            maskedCity = maskingProvider.mask(originalCity);
            assertEquals(firstMask, maskedCity);
        }

    }

    @Test
    public void testPseudorandomLocalization() throws Exception {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("city.mask.pseudorandom", true);

        MaskingProvider maskingProvider = new CityMaskingProvider(maskingConfiguration);

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.CITY, Collections.singletonList("gr"));
        Set<String> greekValues = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekValues.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        String originalCity = "Πάτρα";
        String maskedCity = maskingProvider.mask(originalCity);

        String firstMask = maskedCity;

        for(int i = 0; i < 100; i++) {
            maskedCity = maskingProvider.mask(originalCity);
            assertEquals(firstMask, maskedCity);
            assertTrue(greekValues.contains(maskedCity.toUpperCase()));
        }

    }

    @Test
    public void testLocalization() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new CityMaskingProvider();

        String greekOriginalValue = "Πάτρα";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.CITY, Collections.singletonList("gr"));
        Set<String> greekValues = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekValues.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(greekOriginalValue);
            assertTrue(greekValues.contains(maskedValue.toUpperCase()));
        }
    }

    @Test
    public void testLocalizationClosest() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("city.mask.closest", true);
        MaskingProvider maskingProvider = new CityMaskingProvider(configuration);

        String greekOriginalValue = "Πάτρα";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.CITY, Collections.singletonList("gr"));
        Set<String> greekValues = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekValues.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(greekOriginalValue);
            assertTrue(greekValues.contains(maskedValue.toUpperCase()));
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration closestConfiguration = new DefaultMaskingConfiguration("closest");
        closestConfiguration.setValue("city.mask.closest", true);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, closestConfiguration
        };

        String originalCity = "Dublin";

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            CityMaskingProvider maskingProvider = new CityMaskingProvider(maskingConfiguration);

            int N = 1000000;

            long startMillis = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                String maskedCity = maskingProvider.mask(originalCity);
            }

            long diff = System.currentTimeMillis() - startMillis;
            System.out.printf("%s: %d operations took %d milliseconds (%f per op)%n",
                    maskingConfiguration.getName(), N, diff, (double) diff / N);
        }
    }

    @Test
    public void testMaskClosest() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("city.mask.closest", true);

        CityMaskingProvider cityMaskingProvider = new CityMaskingProvider(configuration);

        String originalCity = "Dublin";
        String[] neighbors = {
                "Lucan",
                "Tallaght",
                "Blanchardstown",
                "Wolverhampton",
                "Stoke-on-Trent",
                "Dún Laoghaire",
                "Manchester",
                "Swords",
                "Donaghmede",
                "Warrington",
                "Preston",
                "St Helens",
                "Swansea",
                "Liverpool",
                "Blackpool",
                "Cork",
                "Limerick",
                "Belfast",
                "Preston",
                "Dublin"
        };

        List<String> neighborsList = Arrays.asList(neighbors);

        for(int i =0; i < 1_000; i++) {
            String maskedCity = cityMaskingProvider.mask(originalCity);
            assertTrue(neighborsList.contains(maskedCity));
        }
    }
}
