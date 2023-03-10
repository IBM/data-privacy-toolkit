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

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.nlp.ComplexFreeTextAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntityType;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.HashMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.RedactMaskingProvider;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class FreeTextMaskingProviderTest {
    @Test
    public void testBasicBehavior() throws IOException {
        ComplexFreeTextAnnotator annotator = mock(ComplexFreeTextAnnotator.class);
        MaskingProviderFactory factory = mock(MaskingProviderFactory.class);

        String test = "THIS IS MY TEST";

        when(annotator.identify(anyString(), any())).thenReturn(List.of(new IdentifiedEntity(
                "TEST",
                test.indexOf("TEST"),
                test.indexOf("TEST") + "TEST".length(),
                Collections.singleton(new IdentifiedEntityType("TEST", "", "")),
                Collections.emptySet()
        )));
        Map<String, DataMaskingTarget> toBeMasked = Map.of("TEST", new DataMaskingTarget(ProviderType.REDACT, "TEST"));

        when(factory.get(anyString(), any())).thenReturn(new RedactMaskingProvider(new DefaultMaskingConfiguration()));
        when(factory.getConfigurationForField(anyString())).thenReturn(new DefaultMaskingConfiguration());
        when(factory.getToBeMasked()).thenReturn(toBeMasked);

        FreeTextMaskingProvider provider = new FreeTextMaskingProvider(
                factory,
                annotator
        );

        String masked = provider.mask(test);
        assertThat(masked, not(test));
    }

    @Test
    public void testBasicBehaviorWithPadding() throws IOException {
        ComplexFreeTextAnnotator annotator = mock(ComplexFreeTextAnnotator.class);
        MaskingProviderFactory factory = mock(MaskingProviderFactory.class);
        MaskingProvider mp = mock(MaskingProvider.class);

        String test = "THIS IS MY TEST";

        when(annotator.identify(anyString(), any())).thenReturn(List.of(new IdentifiedEntity(
                "TEST",
                test.indexOf("TEST"),
                test.indexOf("TEST") + "TEST".length(),
                Collections.singleton(new IdentifiedEntityType("TEST", "", "")),
                Collections.emptySet()
        )));
        when(factory.get(anyString(), any())).thenReturn(mp);
        when(factory.getConfigurationForField(anyString())).thenReturn(new DefaultMaskingConfiguration());
        when(mp.mask(anyString())).thenReturn("X");
        when(factory.getToBeMasked()).thenReturn(Map.of("TEST", new DataMaskingTarget(ProviderType.REDACT, "TEST")));

        FreeTextMaskingProvider provider = new FreeTextMaskingProvider(
                factory,
                annotator
        );

        String masked = provider.mask(test);
        assertThat(masked, not(test));
        assertThat(masked.length(), is(test.length()));
    }

    @Test
    public void testBasicBehaviorWithInternalConfig() throws IOException {
        MaskingProviderFactory factory = mock(MaskingProviderFactory.class);
        MaskingProvider mp = mock(MaskingProvider.class);
        MaskingConfiguration configuration = mock(MaskingConfiguration.class);

        String test = "THIS IS MY TEST test@gmail.com and this is another email foo@gmail.com";

        when(factory.get(anyString(), any())).thenReturn(mp);
        when(factory.getConfigurationForField(anyString())).thenReturn(new DefaultMaskingConfiguration());
        when(mp.mask(anyString())).thenReturn("X");

        JsonNode nlpConfig;

        try (InputStream inputStream = FreeTextMaskingProviderTest.class.getResourceAsStream("/complexWithIdentifiersPRIMAOnlyEmailOnly.json")) {
            nlpConfig = JsonUtils.MAPPER.readTree(inputStream);
        }

        when(configuration.getJsonNodeValue(anyString())).thenReturn(nlpConfig);
        when(factory.getToBeMasked()).thenReturn(Map.of("EMAIL", new DataMaskingTarget(ProviderType.REDACT, "EMAIL")));

        FreeTextMaskingProvider provider = new FreeTextMaskingProvider(
                factory,
                configuration
        );

        String masked = provider.mask(test);

        assertThat(masked, not(test));
        assertThat(masked.length(), is(test.length()));
    }

    @Test
    public void testGrepAndMaskAnywhere() {
        ComplexFreeTextAnnotator annotator = mock(ComplexFreeTextAnnotator.class);
        MaskingProviderFactory factory = mock(MaskingProviderFactory.class);
        when(factory.getConfigurationForField(anyString())).thenReturn(new DefaultMaskingConfiguration());
        when(factory.getToBeMasked()).thenReturn(Map.of("TEST", new DataMaskingTarget(ProviderType.REDACT, "TEST")));

        FreeTextMaskingProvider freeTextMaskingProvider = new FreeTextMaskingProvider(
                factory,
                annotator
        );

        String value = "john went to work. Mr. smith is a professor.";
        List<IdentifiedEntity> entities = freeTextMaskingProvider.grep("smith", value, " ", false, true, "NAME");

        assertEquals(1, entities.size());
        assertThat(entities.get(0).getText(), is("smith"));
    }

    @Test
    public void testCompoundGrepAndMask() throws IOException {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        try (InputStream inputStream = FreeTextMaskingProviderTest.class.getResourceAsStream("/complexWithIdentifiersPRIMAOnlyEmailOnly.json")) {
            maskingConfiguration.setValue("freetext.mask.nlp.config", JsonUtils.MAPPER.readTree(inputStream));
        }

        maskingConfiguration.setValue("generic.lookupTokensType", "NAME");
        maskingConfiguration.setValue("generic.lookupTokensSeparator", " ");
        maskingConfiguration.setValue("generic.lookupTokensIgnoreCase", false);
        maskingConfiguration.setValue("generic.lookupTokensFindAnywhere", false);

        Map<String, DataMaskingTarget> toBeMasked = Map.of("NAME", new DataMaskingTarget(ProviderType.REDACT, "NAME"));

        FreeTextMaskingProvider freeTextMaskingProvider = new FreeTextMaskingProvider(new MaskingProviderFactory(
                new ConfigurationManager(maskingConfiguration),
                toBeMasked
        ), maskingConfiguration);

        String value = "XYZ went to work. Mr. QWE is a professor.";

        String masked = freeTextMaskingProvider.maskGrepAndMask(value, List.of("XYZ QWE"));

        assertEquals(-1, masked.indexOf("XYZ"));
        assertEquals(-1, masked.indexOf("QWE"));
    }

    @Test
    @Disabled("STILL BROKEN")
    public void testCompoundGrepAndMaskNoDoubleMasking() throws IOException {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        try (InputStream inputStream = FreeTextMaskingProviderTest.class.getResourceAsStream("/complexWithIdentifiersPRIMAOnlyEmailOnly.json")) {
            maskingConfiguration.setValue("freetext.mask.nlp.config", JsonUtils.MAPPER.readTree(inputStream));
        }

        maskingConfiguration.setValue("generic.lookupTokensType", "NAME");
        maskingConfiguration.setValue("generic.lookupTokensSeparator", " ");
        maskingConfiguration.setValue("generic.lookupTokensIgnoreCase", false);
        maskingConfiguration.setValue("generic.lookupTokensFindAnywhere", false);

        FreeTextMaskingProvider freeTextMaskingProvider = new FreeTextMaskingProvider(new MaskingProviderFactory(
                new ConfigurationManager(maskingConfiguration),
                Map.of("EMAIL", new DataMaskingTarget(ProviderType.HASH, "EMAIL"))
        ), maskingConfiguration);

        String emailValue = "xyz@ie.ibm.com";
        String value = "XYZ went to work. His e-mail is " + emailValue;

        String masked = freeTextMaskingProvider.maskGrepAndMask(value, List.of(emailValue));

        assertEquals(-1, masked.indexOf(emailValue));

        // we need to make sure it does not double-mask
        String hashedEmail = (new HashMaskingProvider()).mask(emailValue);
        assertTrue(masked.contains(hashedEmail));
    }

    @Test
    public void testGrepMatchCase() throws IOException {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        try (InputStream inputStream = FreeTextMaskingProviderTest.class.getResourceAsStream("/complexWithIdentifiersPRIMAOnly.json")) {
            maskingConfiguration.setValue("freetext.mask.nlp.config", JsonUtils.MAPPER.readTree(inputStream));
        }

        FreeTextMaskingProvider freeTextMaskingProvider = new FreeTextMaskingProvider(new MaskingProviderFactory(
                new ConfigurationManager(maskingConfiguration),
                Collections.emptyMap()
        ), maskingConfiguration);

        boolean ignoreCase = false;

        String value = "John went to work. Mr. Smith is a professor.";
        List<IdentifiedEntity> entities = freeTextMaskingProvider.grep("John Smith", value, "\\s+", ignoreCase, false, "NAME");
        assertEquals(2, entities.size());
        assertEquals(0, entities.get(0).getStart());
        assertEquals(value.indexOf("Smith"), entities.get(1).getStart());
    }

    @Test
    public void testGrepMatchCaseEmptySource() throws IOException {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        try (InputStream inputStream = FreeTextMaskingProviderTest.class.getResourceAsStream("/complexWithIdentifiersPRIMAOnly.json")) {
            maskingConfiguration.setValue("freetext.mask.nlp.config", JsonUtils.MAPPER.readTree(inputStream));
        }

        FreeTextMaskingProvider freeTextMaskingProvider = new FreeTextMaskingProvider(new MaskingProviderFactory(
                new ConfigurationManager(maskingConfiguration),
                Collections.emptyMap()
        ), maskingConfiguration);

        boolean ignoreCase = false;

        String value = "John went to work. Mr. Smith is a professor.";
        List<IdentifiedEntity> entities = freeTextMaskingProvider.grep("", value, " ", ignoreCase, false, "NAME");
        assertEquals(0, entities.size());
    }

    @Test
    public void testGrepAndMaskIgnoreCase() throws IOException {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        try (InputStream inputStream = FreeTextMaskingProviderTest.class.getResourceAsStream("/complexWithIdentifiersPRIMAOnly.json")) {
            maskingConfiguration.setValue("freetext.mask.nlp.config", JsonUtils.MAPPER.readTree(inputStream));
        }
        FreeTextMaskingProvider freeTextMaskingProvider = new FreeTextMaskingProvider(new MaskingProviderFactory(
                new ConfigurationManager(maskingConfiguration),
                Collections.emptyMap()
        ), maskingConfiguration);

        //ignore case
        boolean ignoreCase = true;

        String value = "John went to work. Mr. Smith is a professor.";
        List<IdentifiedEntity> entities = freeTextMaskingProvider.grep("john smith", value, " ", ignoreCase, false, "NAME");
        assertEquals(2, entities.size());
        assertEquals(0, entities.get(0).getStart());
        assertEquals(value.indexOf("Smith"), entities.get(1).getStart());
    }

    @Test
    @Disabled
    public void testJSONLookupTokens() throws Exception {
/*
TODO: MOVE TO PROCESSORS MODULE
        String msg = "{ \"a\": \"foo\", \"b\": \"Mr. foo went to a bar\" }";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        ConfigurationManager configurationManager = ConfigurationManager.load(mapper.readTree(this.getClass().getResourceAsStream("/jsonlookup.json")));
        DataMaskingOptions maskingOptions = (new ObjectMapper()).readValue(this.getClass().getResourceAsStream("/jsonlookup.json"), DataMaskingOptions.class);

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.JSON);
        formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

        String masked = outputStream.toString();

        JsonNode node = mapper.readTree(masked);
        String maskedB = node.get("b").asText();

        assertTrue(maskedB.startsWith("Mr. "));
        assertTrue(maskedB.endsWith(" went to a bar"));
        assertFalse(maskedB.contains("foo"));
        */

    }

    @Test
    @Disabled
    public void testJSONLookupTokensNested() throws Exception {
/*
TODO: MOVE TO PROCESSORS

        String msg = "{ \"root\" : { \"a\": \"foo\", \"b\": \"Mr. foo went to a bar\" } }";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        ConfigurationManager configurationManager = ConfigurationManager.load(mapper.readTree(this.getClass().getResourceAsStream("/jsonlookupnested.json")));
        DataMaskingOptions maskingOptions = (new ObjectMapper()).readValue(this.getClass().getResourceAsStream("/jsonlookupnested.json"), DataMaskingOptions.class);

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.JSON);
        formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

        String masked = outputStream.toString();

        JsonNode node = mapper.readTree(masked);
        System.out.println(node.get("root").get("a"));
        System.out.println(node.get("root").get("b"));

        String maskedB = node.get("root").get("b").asText();
        assertTrue(maskedB.startsWith("Mr. "));
        assertTrue(maskedB.endsWith(" went to a bar"));
        assertFalse(maskedB.contains("foo"));
        */
    }

    @Test
    @Disabled
    public void testJSONLookupTokensArray() throws Exception {
/*
TODO: MOVE TO PROCESSORS

        String msg = "{ \"root\" : [ " +
                "{ \"a\": \"foo\", \"b\": \"Mr. foo went to a bar\" },  " +
                "{ \"a\": \"goo\", \"b\": \"Mr. goo went to a bar\" }  " +
                "] }";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        ConfigurationManager configurationManager = ConfigurationManager.load(mapper.readTree(this.getClass().getResourceAsStream("/jsonlookuparray.json")));
        DataMaskingOptions maskingOptions = (new ObjectMapper()).readValue(this.getClass().getResourceAsStream("/jsonlookuparray.json"), DataMaskingOptions.class);

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.JSON);
        formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

        String masked = outputStream.toString();

        JsonNode node = mapper.readTree(masked);
        System.out.println(node.get("root").get(0).get("a"));
        System.out.println(node.get("root").get(0).get("b"));

        for(int i = 0; i < 2; i++) {
            String maskedB = node.get("root").get(i).get("b").asText();
            assertTrue(maskedB.startsWith("Mr. "));
            assertTrue(maskedB.endsWith(" went to a bar"));

            String a = node.get("root").get(i).get("a").asText();
            assertFalse(maskedB.contains(a));
        }

 */
    }

    @Test
    @Disabled
    public void processWithRelationship() throws Exception {
        /*
TODO: MOVE TO PROCESSORS

        String msg = "{ \"a\": \"foo\", \"b\": \"Mr. foo went to a bar\" }";

        InputStream inputStream = new ByteArrayInputStream(msg.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        ConfigurationManager configurationManager = ConfigurationManager.load(mapper.readTree(this.getClass().getResourceAsStream("/jsonlookup.json")));
        DataMaskingOptions maskingOptions = mapper.readValue(this.getClass().getResourceAsStream("/jsonlookup.json"), DataMaskingOptions.class);

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(DataTypeFormat.JSON);
        formatProcessor.maskStream(inputStream, printStream, new MaskingProviderFactory(configurationManager, Collections.emptyMap()), maskingOptions, new HashSet<>(), null);

        String masked = outputStream.toString();

        JsonNode node = mapper.readTree(masked);
        String maskedB = node.get("b").asText();

        assertTrue(maskedB.startsWith("Mr. "));
        assertTrue(maskedB.endsWith(" went to a bar"));
        assertFalse(maskedB.contains("foo"));

         */
    }

//    @Test
//    public void testOnNotIntelligibleText() {
//        String text = "THIS IS NOT TEXT CONTAINING ANY PHI";
//
//        String maskedText = new OldFreeTextMaskingProvider(new DefaultMaskingConfiguration(), new MaskingProviderFactory()).mask(text);
//
//        assertEquals(text, maskedText);
//    }
//
//    @Test
//    public void testTextWithPHI() {
//        String phi = "John Smith";
//
//        assertTrue(new NameIdentifier().isOfThisType(phi));
//
//        String text = phi + " went for a walk";
//
//        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
//        maskingConfiguration.setValue("freetext.mask.maskingConfigurationFilename", "/testFreetextMaskName.json");
//
//        OldFreeTextMaskingProvider freeTextMaskingProvider = new OldFreeTextMaskingProvider(maskingConfiguration, new MaskingProviderFactory());
//        String maskedText = freeTextMaskingProvider.mask(text);
//        assertNotEquals(text, maskedText);
//        assertTrue(maskedText.endsWith(" went for a walk"));
//    }
//
//    @Test
//    public void testTextWithPHIMaskOnlyThese() {
//        String phi = "John Smith went for a trip to Paris";
//
//        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
//        maskingConfiguration.setValue("freetext.mask.maskingConfigurationFilename", "/testFreetextMaskName.json");
//
//        String maskedText = (new OldFreeTextMaskingProvider(maskingConfiguration, new MaskingProviderFactory()).mask(phi));
//
//        assertFalse(maskedText.contains("John Smith"));
//        assertTrue(maskedText.endsWith("Paris"));
//    }
//
//    @Test
//    public void testTextWithPHIMaskOnlyThese2() {
//        String phi = "John Smith went for a trip to Paris";
//        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
//        maskingConfiguration.setValue("freetext.mask.maskingConfigurationFilename", "/testFreetextMaskEmail.json");
//
//        String maskedText = (new OldFreeTextMaskingProvider(maskingConfiguration, new MaskingProviderFactory()).mask(phi));
//        assertTrue(maskedText.contains("John Smith"));
//        assertTrue(maskedText.endsWith("Paris"));
//    }
//
//    @Test
//    public void testTextWithEmail() {
//        String phi = "A colleague of mine (johndoe@gr.ibm.com) in Finland is not able to connect to SSO, and it seems this is the case for all of Finland";
//        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
//        maskingConfiguration.setValue("freetext.mask.maskingConfigurationFilename", "/testFreetextMaskEmail.json");
//
//        String maskedText = (new OldFreeTextMaskingProvider(maskingConfiguration, new MaskingProviderFactory()).mask(phi));
//
//        assertFalse(maskedText.contains("gr.ibm.com"));
//        assertTrue(maskedText.endsWith("Finland"));
//    }
}