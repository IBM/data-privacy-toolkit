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
package com.ibm.research.drl.dpt.nlp;


import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.nlp.opennlp.OpenNLPAnnotator;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NLPUtilsTest {

    @Test
    @Disabled("Change of assumptions")
    public void testAnnotateOverlappingEntities() {

        String inputText = "John lives at Paris";

        List<IdentifiedEntity> identifiedEntities = new ArrayList<>();

        Set<IdentifiedEntityType> types = new HashSet<>(List.of(new IdentifiedEntityType("FOO", "FOO", "FOOBAR")));
        Set<IdentifiedEntityType> types2 = new HashSet<>(List.of(new IdentifiedEntityType("FOO2", "FOO2", "FOOBAR")));
        IdentifiedEntity first = new IdentifiedEntity("John lives", 0, "John lives".length(), types, Collections.emptySet());
        IdentifiedEntity second = new IdentifiedEntity("lives at", 5, 5 + "lives at".length(), types2, Collections.emptySet());

        identifiedEntities.add(first);
        identifiedEntities.add(second);

        String annotated = NLPUtils.applyFunction(inputText, identifiedEntities, NLPUtils.ANNOTATE_FUNCTION);
        assertEquals("<ProviderType:FOO>John lives</ProviderType> at Paris", annotated);
    }

    @Test
    @Disabled("Change of assumptions")
    public void testAnnotateOverlappingEntitiesCheckActualAnnotations() {

        String inputText = "John lives alone at Paris";

        List<IdentifiedEntity> identifiedEntities = new ArrayList<>();

        Set<IdentifiedEntityType> types = new HashSet<>(List.of(new IdentifiedEntityType("FOO", "FOO", "FOOBAR")));
        Set<IdentifiedEntityType> types2 = new HashSet<>(List.of(new IdentifiedEntityType("FOO2", "FOO2", "FOOBAR")));
        Set<IdentifiedEntityType> types3 = new HashSet<>(List.of(new IdentifiedEntityType("FOO3", "FOO3", "FOOBAR")));
        
        IdentifiedEntity first = new IdentifiedEntity("John lives", 0, "John lives".length(), types, Collections.emptySet());
        IdentifiedEntity second = new IdentifiedEntity("lives alone at", 5, 5 + "lives alone at".length(), types2, Collections.emptySet());
        IdentifiedEntity third = new IdentifiedEntity("alone at Paris", 11, 11 + "alone at Paris".length(), types3, Collections.emptySet());

        identifiedEntities.add(first);
        identifiedEntities.add(second);
        identifiedEntities.add(third);

        String annotated = NLPUtils.applyFunction(inputText, identifiedEntities, NLPUtils.ANNOTATE_FUNCTION);
        assertEquals("<ProviderType:FOO>John lives</ProviderType> <ProviderType:FOO3>alone at Paris</ProviderType>", annotated);
    }
    
    @Test
    public void testMaskingWithIdentifiedEntityList() {
        final String PI = "John";
        final String restOfTheText = " is a nice guy";
        final String allText = PI + restOfTheText;
        final List<IdentifiedEntity> identifiedEntities = Collections.singletonList(new IdentifiedEntity(PI, 0, PI.length(), 
                Collections.singleton(new IdentifiedEntityType("NAME", "NAME", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.emptySet()));

        MaskingProviderFactory mockedFactory = new MaskingProviderFactory(new ConfigurationManager(new DefaultMaskingConfiguration()), Collections.emptyMap());

        final String maskedText = NLPUtils.maskAnnotatedText(allText, identifiedEntities, mockedFactory);

        assertNotSame(maskedText, allText);
        assertNotEquals(maskedText, allText);

        assertTrue(maskedText.endsWith(restOfTheText));
        final String firstPartOfMaskedText = maskedText.substring(0, maskedText.indexOf(restOfTheText));

        assertNotEquals(firstPartOfMaskedText, PI);
    }

    @Test
    public void testMasking() throws Exception {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        try (InputStream inputStream = NLPUtilsTest.class.getResourceAsStream("/opennlp.json")) {
            NLPAnnotator identifier = new OpenNLPAnnotator(JsonUtils.MAPPER.readTree(inputStream));

            String note1 = "Peter was not home at present. His wife picked up the phone and took a message for him to return my call so that I could talk to him about his healthcare management. I will try to reach out to Peter again next week if no call returned  prior. Peter appears to work so I might have to try him at a later hour";
            List<IdentifiedEntity> identifiedEntities = identifier.identify(note1, Language.UNKNOWN);

            int[] maskOK = new int[identifiedEntities.size()];

            for (int j = 0; j < 100; j++) {
                //System.out.println("--- masked -----");
                List<IdentifiedEntity> maskedTokens = NLPUtils.maskAnnotatedTokens(identifiedEntities, maskingProviderFactory);

                for (int i = 0; i < identifiedEntities.size(); i++) {
                    IdentifiedEntity original = identifiedEntities.get(i);
                    IdentifiedEntity masked = maskedTokens.get(i);

                    if (original.getType() == null) {
                        assertEquals(original.getText(), masked.getText());
                    } else {
                        if (!original.getText().equals(masked.getText())) {
                            maskOK[i]++;
                        }
                    }
                }
            }

            for (int value : maskOK) {
                assertTrue(value > 0);
            }
        }
    }

    @Test
    public void testMaskingAnnotatedTextNoAnnotations() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        String annotatedText = "Nobody was home at present";
        String maskedText = NLPUtils.maskAnnotatedText(annotatedText, true, maskingProviderFactory);

        assertEquals(annotatedText, maskedText);
    }

    @Test
    public void testMaskingAnnotatedTextMissingClosingPattern() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        String annotatedText = "<ProviderType:NAME>Peter Nobody was home at present";
        String maskedText = NLPUtils.maskAnnotatedText(annotatedText, true, maskingProviderFactory);

        assertEquals(annotatedText, maskedText);
    }

    @Test
    public void testMaskingAnnotatedTextMissingClosingBracket () {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        String annotatedText = "<ProviderType:NAME Peter Nobody was home at present";
        String maskedText = NLPUtils.maskAnnotatedText(annotatedText, true, maskingProviderFactory);

        assertEquals(annotatedText, maskedText);
    }

    @Test
    public void testMaskingAnnotatedText() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        String annotatedText = "<ProviderType:NAME>Peter</ProviderType> was not home at present. " +
                "He was at <ProviderType:COUNTRY>Italy</ProviderType> with his friend <ProviderType:NAME>Jack</ProviderType>";
        String maskedText = NLPUtils.maskAnnotatedText(annotatedText, true, maskingProviderFactory);

        int index = maskedText.indexOf("<ProviderType:NAME>");
        assertNotEquals(-1, index);

        System.out.println(maskedText);

        assertNotEquals(annotatedText, maskedText);
    }

    @Test
    public void testMaskingAnnotatedTextNoTags() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        String annotatedText = "<ProviderType:NAME>Peter</ProviderType> was not home at present. " +
                "He was at <ProviderType:COUNTRY>Italy</ProviderType> with his friend <ProviderType:NAME>Jack</ProviderType>";
        String maskedText = NLPUtils.maskAnnotatedText(annotatedText, false, maskingProviderFactory);

        int index = maskedText.indexOf("<ProviderType:NAME>");
        assertEquals(-1, index);

        System.out.println(maskedText);

        assertNotEquals(annotatedText, maskedText);
    }

    @Test
    public void testMaskingAnnotatedText2() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(maskingConfiguration), null);

        String annotatedText = "<ProviderType:NAME>Peter</ProviderType>";
        String maskedText = NLPUtils.maskAnnotatedText(annotatedText, true, maskingProviderFactory);

        int index = maskedText.indexOf("<ProviderType:NAME>");
        assertNotEquals(-1, index);

        System.out.println(maskedText);

        assertNotEquals(annotatedText, maskedText);
    }
    
    @Test
    public void testApplyFunctionRedact() throws IOException {
        try (InputStream inputSteam = NLPUtilsTest.class.getResourceAsStream("/complexWithIdentifiers.json")) {
            NLPAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputSteam));

            String inputText = "Hello, my name is John and I live in Ireland";
            String outputText = NLPUtils.applyFunction(inputText, identifier.identify(inputText, Language.UNKNOWN), NLPUtils.REDACT_FUNCTION);

            assertEquals(-1, outputText.indexOf("John"));
            assertEquals(-1, outputText.indexOf("Ireland"));
        }
    }

    @Test
    public void testApplyFunctionTag() throws IOException {
        try (InputStream inputSteam = NLPUtilsTest.class.getResourceAsStream("/complexWithIdentifiers.json")) {
            NLPAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputSteam));

            String inputText = "Hello, my name is John and I live in Ireland. My name is John and my friend is George";
            String outputText = NLPUtils.applyFunction(inputText, identifier.identify(inputText, Language.ENGLISH), NLPUtils.TAG_FUNCTION_WITH_CACHE);

            assertEquals(-1, outputText.indexOf("John"));
            assertEquals(-1, outputText.indexOf("George"));
            assertEquals(-1, outputText.indexOf("Ireland"));
        }
    }
}
