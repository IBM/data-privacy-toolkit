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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.NamesManager;
import com.ibm.research.drl.dpt.models.Gender;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class NameMaskingProviderTest {
    private final NamesManager.Names names = NamesManager.instance();
    private final MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

    @Test
    public void testMask() {
        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(factory);

        int count1 = 0;
        int count2 = 0;
        
        for(int i = 0; i < 100; i++) {
            String name = "John";
            String res = nameMaskingProvider.mask(name);
            assertThat(names.getGender(name) == names.getGender(res) || Gender.both == names.getGender(res), is(true));

            //capitalization shouldn't matter
            name = "JoHn";
            res = nameMaskingProvider.mask(name);
            
            if (!name.equals(res)) {
                count1++;
            }

            assertThat(names.getGender(name) == names.getGender(res) || Gender.both == names.getGender(res), is(true));

            //female detection
            name = "Mary";
            res = nameMaskingProvider.mask(name);
            if(!name.equals(res)) {
                count2++;
            }

            assertThat(names.getGender(name) == names.getGender(res) || Gender.both == names.getGender(res), is(true));
        }
        
        assertTrue(count1 > 0);
        assertTrue(count2 > 0);

        //surname detection
        String name = "Smith";
        int surnameCount = 0;
        for(int i = 0; i < 100; i++) { 
            String res = nameMaskingProvider.mask(name);
            assertTrue(names.isLastName(res));
            if (!name.equals(res)) {
                surnameCount++;
            }
        }
        assertTrue(surnameCount > 0);

    }

    @Test
    public void testMaskVirtualField() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("default.masking.provider", "HASH");
        maskingConfiguration.setValue("names.mask.virtualField", "__name__");

        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(maskingConfiguration, factory);
        HashMaskingProvider hashMaskingProvider = new HashMaskingProvider();

        String originalValue = "John Smith";
        String[] originalTokens = originalValue.split(" ");

        String maskedValue = nameMaskingProvider.mask(originalValue);

        String[] tokens = maskedValue.split(" ");
        assertEquals(2, tokens.length);
        assertEquals(hashMaskingProvider.mask(originalTokens[0]), tokens[0]);
        assertEquals(hashMaskingProvider.mask(originalTokens[1]), tokens[1]);
    }

    @Test
    public void testMaskVirtualFieldFromConfFile() throws Exception {
        ConfigurationManager configurationManager = ConfigurationManager.load(new ObjectMapper().readTree(this.getClass().getResourceAsStream("/name_virtualfield.json")));

        MaskingConfiguration maskingConfiguration = configurationManager.getDefaultConfiguration();

        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(maskingConfiguration, factory);
        HashMaskingProvider hashMaskingProvider = new HashMaskingProvider();

        String originalValue = "John Smith";
        String[] originalTokens = originalValue.split(" ");

        String maskedValue = nameMaskingProvider.mask(originalValue);

        String[] tokens = maskedValue.split(" ");
        assertEquals(2, tokens.length);
        assertEquals(hashMaskingProvider.mask(originalTokens[0]), tokens[0]);
        assertEquals(hashMaskingProvider.mask(originalTokens[1]), tokens[1]);
    }
    
    @Test
    public void testWithWhitespaces() {
        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(factory);

        String name = "John Washington";
        String res = nameMaskingProvider.mask(name);
        
        assertNotEquals(res, name);
        assertEquals(2, res.split(" ").length);
    }
    
    @Test
    public void testWithCustomSeparator() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("names.masking.separator", "\\^");
        maskingConfiguration.setValue("names.masking.whitespace", "^");
        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(maskingConfiguration, factory);

        String name = "John^Washington";
        String res = nameMaskingProvider.mask(name);

        System.out.println(res);
        
        assertNotEquals(res, name);
        assertEquals(2, res.split("\\^").length);
    }

    @Test
    public void testLocalizationFirstName() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new NameMaskingProvider(this.factory);

        String greekOriginalValue = "Γιώργος";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.FIRST_NAME_MALE, Collections.singletonList("gr"));
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
    public void testLocalizationLastName() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new NameMaskingProvider(factory);

        String greekOriginalValue = "Παπαδόπουλος";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.LAST_NAME, Collections.singletonList("gr"));
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
    public void testMaskNameAndSurname() throws  Exception {
        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(factory);

        String name = "John Smith";
        String res = nameMaskingProvider.mask(name);
        assertNotEquals(name, res);
        assertEquals(2, res.split(" ").length);
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration unisexConfiguration = new DefaultMaskingConfiguration();
        unisexConfiguration.setValue("names.masking.allowAnyGender", true);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, unisexConfiguration
        };

        String[] originalValues = new String[]{"John"};

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            NameMaskingProvider maskingProvider = new NameMaskingProvider(maskingConfiguration, this.factory);

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

    @Test
    public void testMaskAnyGenderNotAllowed() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("names.masking.allowAnyGender", false);

        NameMaskingProvider nameMaskingProvider = new NameMaskingProvider(configuration, this.factory);

        String nameToMask = "Mary";
        Gender genderToMask = names.getGender(nameToMask);

        for(int i = 0; i < 100; i++) {
            String maskedName = nameMaskingProvider.mask(nameToMask);
            Gender maskedGender = names.getGender(maskedName);
            assertTrue(genderToMask == maskedGender || maskedGender == Gender.both);
        }

        nameToMask = "John";
        genderToMask = names.getGender(nameToMask);

        for(int i = 0; i < 100; i++) {
            String maskedName = nameMaskingProvider.mask(nameToMask);
            Gender maskedGender = names.getGender(maskedName);
            assertTrue(genderToMask == maskedGender || maskedGender == Gender.both);
        }
    }

    @Test
    public void testFromAdrian() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();

        String name = "Vamshi Thotempudi";

        MaskingProvider nameMaskingProvider = new NameMaskingProvider(configuration, this.factory);


        String[] masked = nameMaskingProvider.mask(name).split(" ");
        assertEquals(name.split(" ").length, masked.length);

        name = "Test123";

        masked = nameMaskingProvider.mask(name).split(" ");
        assertEquals(name.split(" ").length, masked.length);


        name = "dkfljaslkf fjaslfjalk fdlkajflka faldjflkas";

        masked = nameMaskingProvider.mask(name).split(" ");
        assertEquals(name.split(" ").length, masked.length);
    }

    @Test
    public void pseudoRandom() throws Exception {
        String original1 = "John Doe";
        String original2 = "John James Smith";
        String original3 = "Frederik Smith";


        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();

        configuration.setValue("names.token.consistence", false);
        configuration.setValue("names.mask.pseudorandom", true);

        MaskingProvider mp = new NameMaskingProvider(configuration, this.factory);

        String maskedName1 = mp.mask(original1);
        String maskedName2 = mp.mask(original2);
        String maskedName3 = mp.mask(original3);

        assertEquals(maskedName1.split(" ")[0], maskedName2.split(" ")[0]);
        assertEquals(maskedName2.split(" ")[2], maskedName3.split(" ")[1]);
    }
}
