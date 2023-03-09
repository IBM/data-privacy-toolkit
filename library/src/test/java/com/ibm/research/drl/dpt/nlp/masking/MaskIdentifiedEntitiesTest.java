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
package com.ibm.research.drl.dpt.nlp.masking;


import com.ibm.research.drl.dpt.configuration.*;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntityType;
import com.ibm.research.drl.dpt.nlp.PartOfSpeechType;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.CityIdentifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class MaskIdentifiedEntitiesTest {
    private final static IdentifiedEntityType nameType = new IdentifiedEntityType("NAME", "NAME", IdentifiedEntityType.UNKNOWN_SOURCE);
    private final static IdentifiedEntityType addressType = new IdentifiedEntityType("ADDRESS", "ADDRESS", IdentifiedEntityType.UNKNOWN_SOURCE);
    private final static IdentifiedEntityType cityType = new IdentifiedEntityType("LOCATION", "CITY", IdentifiedEntityType.UNKNOWN_SOURCE);
    
    @Test
    public void systemDoesNotBreakIfNoIdentifiedEntitiesAreFound() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.PLAIN, DataTypeFormat.PLAIN,
                        Collections.emptyMap(), false, null, null);

        MaskIdentifiedEntities ftMask = new MaskIdentifiedEntities(configurationManager, dataMaskingOptions, new MaskingProviderFactory(
                new ConfigurationManager(new DefaultMaskingConfiguration()),
                Collections.emptyMap()
        ));

        List<IdentifiedEntity> maskedEntities = ftMask.maskEntities(Collections.emptyList());

        assertNotNull(maskedEntities);
        assertTrue(maskedEntities.isEmpty());
    }

    @Test
    public void testMixed() throws Exception {
        try (
                InputStream conf = MaskIdentifiedEntitiesTest.class.getResourceAsStream("/ft_mask.json");
                InputStream dmo = MaskIdentifiedEntitiesTest.class.getResourceAsStream("/ft_mask.json");
        ) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(conf));

            DataMaskingOptions dataMaskingOptions = JsonUtils.MAPPER.readValue(dmo, DataMaskingOptions.class);

            MaskIdentifiedEntities ftMask = new MaskIdentifiedEntities(configurationManager, dataMaskingOptions, new MaskingProviderFactory(
                    new ConfigurationManager(new DefaultMaskingConfiguration()),
                    Collections.emptyMap()
            ));

            List<IdentifiedEntity> toMask = List.of(
                    new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN)));

            List<IdentifiedEntity> masked = ftMask.maskEntities(toMask);

            assertEquals(1, masked.size());
            String first = masked.get(0).getText();

            masked = ftMask.maskEntities(toMask);
            assertEquals(1, masked.size());
            String second = masked.get(0).getText();

            assertEquals(first, second);
        }
    }

    @Test
    public void testUsesSubtype() {
        String originalValue = "Rome";

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("CITY", new DataMaskingTarget(ProviderType.CITY, "dummy"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.PLAIN, DataTypeFormat.PLAIN,
                toBeMasked, false, null, null);

        MaskIdentifiedEntities ftMask = new MaskIdentifiedEntities(new ConfigurationManager(), dataMaskingOptions, new MaskingProviderFactory(
                new ConfigurationManager(new DefaultMaskingConfiguration()),
                Collections.emptyMap()
        ));
        
        List<IdentifiedEntity> identifiedEntities = new ArrayList<>();
        identifiedEntities.add(new IdentifiedEntity(originalValue, 0, 4, Collections.singleton(cityType), Collections.singleton(PartOfSpeechType.UNKNOWN)));
        
        List<IdentifiedEntity> maskedEntities = ftMask.maskEntities(identifiedEntities);
       
        IdentifiedEntity maskedEntity = maskedEntities.get(0);
        
        //if the subtype is used we get a random city
        CityIdentifier cityIdentifier = new CityIdentifier();
        assertTrue(cityIdentifier.isOfThisType(originalValue));
       
        assertTrue(cityIdentifier.isOfThisType(maskedEntity.getText()));
    }
    
    @Test
    public void allIdentifiedEntitiesAreMasked() {
        MaskingProviderFactory mockedFactory = mock(MaskingProviderFactory.class);
        MaskingProvider mockedMaskingProvider = mock(MaskingProvider.class);
        when(mockedFactory.get(any(ProviderType.class), any(MaskingConfiguration.class))).thenReturn(mockedMaskingProvider);

        String maskingValue = "BAR";
        when(mockedMaskingProvider.mask(anyString(), eq("NAME"))).thenReturn(maskingValue);

        List<IdentifiedEntity> toMask = Arrays.asList(
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN)),
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN)),
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN)),
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN)),
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN))
        );

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("NAME", new DataMaskingTarget(ProviderType.NAME, "dummy"));
        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.PLAIN, DataTypeFormat.PLAIN,
                toBeMasked, false, null, null);

        MaskIdentifiedEntities ftMask = new MaskIdentifiedEntities(new ConfigurationManager(), dataMaskingOptions, mockedFactory);

        List<IdentifiedEntity> maskedEntities = ftMask.maskEntities(toMask);

        assertNotNull(maskedEntities);
        assertThat(maskedEntities.size(), is(toMask.size()));

        for (IdentifiedEntity maskedEntity : maskedEntities) {
            assertThat(maskedEntity.getText(), is(maskingValue));
        }
    }

    @Test
    public void identifiedEntitiesAreTreatedByTypeCorrectly() {
        MaskingProviderFactory mockedFactory = mock(MaskingProviderFactory.class);
        MaskingProvider mockedNameMP = mock(MaskingProvider.class);
        MaskingProvider mockedAddressMP = mock(MaskingProvider.class);
        when(mockedFactory.get(ArgumentMatchers.same(ProviderType.NAME), any(MaskingConfiguration.class))).thenReturn(mockedNameMP);
        when(mockedFactory.get(ArgumentMatchers.same(ProviderType.ADDRESS), any(MaskingConfiguration.class))).thenReturn(mockedAddressMP);

        String maskingName = "BAR";
        when(mockedNameMP.mask(anyString(), eq("NAME"))).thenReturn(maskingName);
        String maskingAddressValue = "DUMMY ADDRESS";
        when(mockedAddressMP.mask(anyString(), eq("ADDRESS"))).thenReturn(maskingAddressValue);

        List<IdentifiedEntity> toMask = Arrays.asList(
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(nameType), Collections.singleton(PartOfSpeechType.UNKNOWN)),
                new IdentifiedEntity("foo", 0, 0, Collections.singleton(addressType), Collections.singleton(PartOfSpeechType.UNKNOWN))
        );

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("NAME", new DataMaskingTarget(ProviderType.NAME, "dummy"));
        toBeMasked.put("ADDRESS", new DataMaskingTarget(ProviderType.ADDRESS, "dummy"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.PLAIN, DataTypeFormat.PLAIN,
                toBeMasked, false, null, null);
        MaskIdentifiedEntities ftMask = new MaskIdentifiedEntities(new ConfigurationManager(), dataMaskingOptions, mockedFactory);

        List<IdentifiedEntity> maskedEntities = ftMask.maskEntities(toMask);

        assertNotNull(maskedEntities);
        assertThat(maskedEntities.size(), is(toMask.size()));

        verify(mockedNameMP, times(1)).mask(anyString(), eq("NAME"));
        verify(mockedAddressMP, times(1)).mask(anyString(), eq("ADDRESS"));
    }
}