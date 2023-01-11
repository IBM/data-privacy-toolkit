/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.nlp.opennlp.OpenNLPPOSTagger;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ComplexFreeTextAnnotatorTest {
    @Test
    public void testPOSInformationIsUsedCorrectly() throws Exception {
        String text = "I FOO you";

        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream((
                        "{" +
                        "  \"defaultLanguage\": \"ENGLISH\"," +
                        "  \"performLanguageDetection\": false," +
                        "  \"identifiers\": {" +
                        "    \"com.ibm.research.drl.dpt.nlp.PRIMAAnnotator\": {" +
                        "      \"performPOSTagging\": true," +
                        "      \"sentenceDetectorModel\": \"/nlp/en/en-sent.bin\"," +
                        "      \"tokenizerModel\": \"/nlp/en/en-token.bin\"," +
                        "      \"mapping\": {" +
                        "        \"ADDRESS\": \"LOCATION\"," +
                        "        \"STATES_US\": \"LOCATION\"," +
                        "        \"CITY\": \"LOCATION\"," +
                        "        \"DAY\": \"DATETIME\"," +
                        "        \"MONTH\": \"DATETIME\"," +
                        "        \"SSN_US\": \"NATIONAL_ID\"," +
                        "        \"SSN_UK\": \"NATIONAL_ID\"" +
                        "      }," +
                        "      \"splitSentences\": true," +
                        "      \"MIN_SHINGLE_SIZE\": 2," +
                        "      \"MAX_SHINGLE_SIZE\": 10," +
                        "      \"identifiers\": [" +
                        "        \"com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier\"," +
                        "        {" +
                        "          \"type\": \"REGEX\"," +
                        "          \"providerType\": \"FOO\"," +
                        "          \"regex\": [" +
                        "            \"FOO\"" +
                        "          ]," +
                                "\"isPosIndependent\": true" +
                        "        }" +
                        "      ]" +
                        "    }" +
                        "  }," +
                        "  \"blacklist\": {" +
                        "    \"*\": [" +
                        "      \"MAC\"," +
                        "      \"Mac\"," +
                        "      \"mac\"," +
                        "      \"App\"," +
                        "      \"Mac OS\"," +
                        "      \"MAC OS\"," +
                        "      \"mac os\"" +
                        "    ]," +
                        "    \"DATETIME\": [" +
                        "      \"day\"," +
                        "      \"past\"," +
                        "      \"days\"," +
                        "      \"currently\"," +
                        "      \"date\"," +
                        "      \"present\"," +
                        "      \"current\"," +
                        "      \"hours\"," +
                        "      \"weeks\"," +
                        "      \"months\"," +
                        "      \"years\"," +
                        "      \"once\"" +
                        "    ]," +
                        "    \"ORGANIZATION\": [" +
                        "      \"INP\"," +
                        "      \"LTC\"" +
                        "    ]" +
                        "  }," +
                        "  \"connected\": [" +
                        "    {" +
                        "      \"first\": \"ORGANIZATION\"," +
                        "      \"second\": \"LOCATION\"," +
                        "      \"endType\": \"ORGANIZATION\"," +
                        "      \"endSubtype\": \"ORGANIZATION\"," +
                        "      \"particles\": [" +
                        "        \"in\"," +
                        "        \"on\"," +
                        "        \"of\"," +
                        "        \"the\"," +
                        "        \"to\"" +
                        "      ]" +
                        "    }" +
                        "  ]," +
                        "  \"unreliable\": {" +
                        "  }," +
                        "  \"weights\": {" +
                        "    \"PRIMA\": {" +
                        "      \"DOB\": 51," +
                        "      \"DATETIME\": 50," +
                        "      \"EMAIL\": 90," +
                        "      \"NAME\": 50," +
                        "      \"MRN\": 90," +
                        "      \"PHONE\": 89," +
                        "      \"NAME_REGEX\": 99" +
                        "    }" +
                        "  }," +
                        "  \"not_POS\": [" +
                        "    \"DATETIME\"," +
                        "    {" +
                        "      \"MONTH\": [" +
                        "        \"may\"" +
                        "      ]" +
                        "    }," +
                        "    \"DAY\"," +
                        "    \"PHONE\"," +
                        "    \"NUMERIC\"," +
                        "    \"NATIONAL_ID\"," +
                        "    \"EMAIL\"," +
                        "    \"MRN\"," +
                        "    \"URL\"," +
                        "    \"IP_ADDRESS\"," +
                        "    \"MAC_ADDRESS\"," +
                        "    \"CREDIT_CARD\"," +
                        "    \"IMSI\"," +
                        "    \"IMEI\"," +
                        "    \"VIN\"," +
                        "    \"IBAN\"," +
                        "    \"SSN_UK\"," +
                        "    \"SSN_US\"," +
                        "    \"NAME_REGEX\"" +
                        "  ]" +
                        "}").getBytes())
        ) {
            ComplexFreeTextAnnotator annotator = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream));

            List<IdentifiedEntity> entities = annotator.identify(text, Language.ENGLISH);

            assertThat(entities.isEmpty(), is(false));
        }
    }


    @Test
    public void testDoNotReportUnwantedEntities() throws Exception {
        String input = "test trest testaadfasfasdfasdfasf asd fasf asfasdfafdasdfasdfadsfasfdadsfafas";

        NLPAnnotator mockedOpenNLPPos = mock(NLPAnnotator.class);
        when(mockedOpenNLPPos.getName()).thenReturn("OpenNLP_POS");

        NLPAnnotator mockedPrima = mock(NLPAnnotator.class);
        when(mockedPrima.getName()).thenReturn("PRIMA");
        when(mockedPrima.identify(anyString(), any(Language.class))).thenReturn(Arrays.asList(
                new IdentifiedEntity("TEST", 0, "TEST".length(),
                        Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "FOO")),
                        Collections.singleton(PartOfSpeechType.valueOf("NN"))),
                new IdentifiedEntity("TEST", 6, 6 + "TEST".length(),
                        Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "FOO")),
                        Collections.singleton(PartOfSpeechType.valueOf("NN"))),
                new IdentifiedEntity("TEST", 2, 2 + "TEST".length(),
                        Collections.singleton(new IdentifiedEntityType("SNOMED_ENTRY", "SNOMED_ENTRY", "SNOMED_ENTRY")),
                        Collections.singleton(PartOfSpeechType.valueOf("NN")))
        ));

        try (InputStream is = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPosAndIgnore.json")) {
            ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(is), mockedPrima, mockedOpenNLPPos);

            List<IdentifiedEntity> response = identifier.identify(input, Language.UNKNOWN);

            assertNotNull(response);
            assertThat(response.size(), is(2));
        }
    }

    @Test
    public void testDoNotReportDoesNotDropWantedEntities() throws Exception {
        String input = "test trest testaadfasfasdfasdfasf asd fasf asfasdfafdasdfasdfadsfasfdadsfafas";

        NLPAnnotator mockedOpenNLPPos = mock(NLPAnnotator.class);
        when(mockedOpenNLPPos.getName()).thenReturn("OpenNLP_POS");

        NLPAnnotator mockedPrima = mock(NLPAnnotator.class);
        when(mockedPrima.getName()).thenReturn("PRIMA");
        when(mockedPrima.identify(anyString(), any(Language.class))).thenReturn(
                Arrays.asList(
                        new IdentifiedEntity("TEST", 0, "TEST".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "FOO")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))),
                        new IdentifiedEntity("TEST", 6, 6 + "TEST".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "FOO")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN")))
                )
        );

        try (InputStream is = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPosAndIgnore.json")) {
            ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(is), mockedPrima, mockedOpenNLPPos);

            List<IdentifiedEntity> response = identifier.identify(input, Language.UNKNOWN);

            assertNotNull(response);
            assertThat(response.isEmpty(), is(false));
            assertThat(response.size(), is(2));
        }
    }
   
    @Test
    @Disabled("Causes GC Overhead exceptions, needs to be further investigated")
    public void testBlacklistByIdentifier() throws IOException {
        String input = "My email is foo@gmail.com";

        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPos.json")));

        List<IdentifiedEntity> results = identifier.identify(input, Language.ENGLISH);
        assertEquals(1, results.size());


        ComplexFreeTextAnnotator identifierWithIdentifierBlacklist =
                new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPosIdentifierBlacklist.json")));

        results = identifierWithIdentifierBlacklist.identify(input, Language.ENGLISH);
        assertEquals(0, results.size());
    }
    

    @Test
    @Disabled
    public void testHIPAADates() throws IOException {
        String[] inputs = new String[] {
                "Pharmacy express scripts PH# 1-800-123-1234.requesting prior authorization",
                "Spoke with pt who works at BWH and Spaulding in central services.",
                "Daughter (Margaret) owns two horses and leases a third, competes on a regular basis. Son (John) is into hockey"
        };

        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAStanford.json")));

        for(String input: inputs) {
            List<IdentifiedEntity> results = identifier.identify(input, Language.ENGLISH);
            System.out.println(NLPUtils.applyFunction(input, results, NLPUtils.ANNOTATE_FUNCTION));
        }
    }
    
    @Test
    public void testMergeDoesNotMergeDistinctIdentifiedEntities() throws IOException {
        Object[][] inputs = {
                new Object[]{"John Doe MD is a nice guy. He works with the famous Dr. John Smith.",2, new String[]{
                        "John Doe", "John Smith"
                }},
                new Object[]{"John Smith MD works at a nice hospital. His phone number is 555-444-1234.",2, new String[] {
                        "John Smith", "555-444-1234"
                }},
                new Object[]{"John Smith lives at 425 FLOWER BLVD", 1, new String[]{"425 FLOWER BLVD"}}
        };

        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPos.json")));

        for(Object[] input: inputs) {
            List<IdentifiedEntity> results = identifier.identify(input[0].toString(), Language.ENGLISH);

            assertThat(results.toString(), results.size(), is((Integer) input[1]));

            results.sort(Comparator.comparingInt(IdentifiedEntity::getStart));

            for (int i = 0; i < (Integer) input[1]; ++i) {
                IdentifiedEntity entity = results.get(i);
                assertThat(entity.toString(), entity.getText(), is(((String[])input[2])[i]));
            }
        }
    }

    @Test
    @Disabled
    public void findingMisteriousDrops() throws IOException {
        String inputText = "Hello, my name is John and I live in Ireland. My name is John and my friend is George";

        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/complexWithIdentifiers.json")));

        List<IdentifiedEntity> result = identifier.identify(inputText, Language.ENGLISH);

        System.out.println(result);
    }

    @Test
    @Disabled
    public void testWithPrimaAndSystemT() throws IOException {
        String inputText = "Hello, my name is John and I live in Ireland. My name is John and my friend is George.";

        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/complexWithPRIMA&SystemT.json")));

        List<IdentifiedEntity> result = identifier.identify(inputText, Language.ENGLISH);

        System.out.println(JsonUtils.MAPPER.writeValueAsString(
                result.stream().map(
                        entity -> {
                            Map<String, Object> mapped = new HashMap<>();

                            mapped.put("text", entity.getText());
                            mapped.put("partOfSpeech", entity.getPos().iterator().next().toString());
                            mapped.put("type", entity.getType().iterator().next().getType());
                            mapped.put("start", entity.getStart());
                            mapped.put("end", entity.getEnd());

                            return mapped;
                        }
                ).collect(Collectors.toList())
        ));
    }

    @Test
    public void testMergeIsDoneCorrectlyWithPoS() throws Exception {
        String[] inputs = {
                "Sincerely, John Doe MD",
                "John Doe MD\n" + "Cool Office\n" + "Company Rocks Medical Associates",
        };

        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPos.json")));

        for(String input: inputs) {
            List<IdentifiedEntity> results = identifier.identify(input, Language.ENGLISH);

            assertThat(results.toString(), results.size(), is(1));
            assertThat(results.toString(), results.iterator().next().getText(), containsString("John Doe"));
            System.err.println(results);
        }
    }

    @Test
    public void testMergeIsDoneCorrectly() throws Exception {
        String[] inputs = {
                "Sincerely, John Doe MD",
                "John Doe MD\n" + "Cool Office\n" + "Company Rocks Medical Associates",
        };

        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithoutPos.json")) {
            ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream));

            for (String input : inputs) {
                List<IdentifiedEntity> results = identifier.identify(input, Language.ENGLISH);

                assertThat(results.toString(), results.size(), is(1));
                assertThat(results.toString(), results.iterator().next().getText(), containsString("John Doe"));
            }
        }
    }

    @Test
    public void splitIsDoneCorrectlyCase1() throws Exception {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;

        try(InputStream is = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(is), a, b);
        }
        // |---|
        // |---|
        // colliding, merge

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AA",
                                0,
                                "AA".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.UNKNOWN)
                        ),
                        new IdentifiedEntity(
                                "AA",
                                0,
                                "AA".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );

        assertNotNull(result);
        assertThat(result.size(), is(1));
    }

    @Test
    public void splitIsDoneCorrectlyCase2() throws IOException {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), a, b);
        }
        // |---|
        // |-----|

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AA",
                                0,
                                "AA".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.UNKNOWN)
                        ),
                        new IdentifiedEntity(
                                "AABB",
                                0,
                                "AABB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );

        assertNotNull(result);
        assertThat(result.size(), is(2));
    }
    @Test
    public void splitIsDoneCorrectlyCase3() throws IOException {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream =ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), a, b);
        }
        // |-----|
        // |---|

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AABB",
                                0,
                                "AABB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.UNKNOWN)
                        ),
                        new IdentifiedEntity(
                                "AA",
                                0,
                                "AA".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );

        assertNotNull(result);
        assertThat(result.size(), is(2));
    }

    @Test
    public void splitIsDoneCorrectlyCase4() throws IOException {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
             identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), a, b);
        }
        // |-------|
        //    |----|

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AABB",
                                0,
                                "AABB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.UNKNOWN)
                        ),
                        new IdentifiedEntity(
                                "BB",
                                2,
                                2 + "BB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );
        assertNotNull(result);
        assertThat(result.size(), is(2));
    }

    @Test
    public void splitIsDoneCorrectlyCaseMultiple() throws IOException {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), a, b);
        }
        // |-------|
        //      |-------|
        //          |-------|

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AABB",
                                0,
                                "AABB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        ),
                        new IdentifiedEntity(
                                "BBCC",
                                2,
                                2 + "BBCC".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO1", "FOO1", "NLP1")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        ),
                        new IdentifiedEntity(
                                "CCDD",
                                4,
                                4 + "CCDD".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO2", "FOO2", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );

        assertNotNull(result);
        assertThat(result.toString(), result.size(), is(4));

        for (IdentifiedEntity entity : result) {
            Set<String> types = entity.getType().stream().map(IdentifiedEntityType::getType).collect(Collectors.toSet());
            switch (entity.getText()) {
                case "AA":
                    assertTrue(types.contains("FOO"));
                    assertThat(types.toString(), types.size(), is(1));
                    break;
                case "BB":
                    assertTrue(types.contains("FOO"));
                    assertTrue(types.contains("FOO1"));
                    assertThat(types.toString(), types.size(), is(2));
                    break;
                case "CC":
                    assertTrue(types.contains("FOO1"));
                    assertTrue(types.contains("FOO2"));
                    assertThat(types.toString(), types.size(), is(2));
                    break;
                case "DD":
                    assertTrue(types.contains("FOO2"));
                    assertThat(types.toString(), types.size(), is(1));
                    break;
                default:
                    fail("something was very wrong");
            }
        }
    }

    @Test
    public void splitIsDoneCorrectlyCase5() throws IOException {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), a, b);
        }
        // |----|
        //    |----|

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AABB",
                                0,
                                "AABB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.UNKNOWN)
                        ),
                        new IdentifiedEntity(
                                "BBCC",
                                2,
                                2 + "BBCC".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );

        assertNotNull(result);
        assertThat(result.toString(), result.size(), is(3));
    }
    @Test
    public void splitIsDoneCorrectlyCase6() throws IOException {
        NLPAnnotator a = mock(NLPAnnotator.class);
        NLPAnnotator b = mock(NLPAnnotator.class);

        when(a.getName()).thenReturn("NLP1");
        when(b.getName()).thenReturn("NLP2");
        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/ComplexSplitTest.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), a, b);
        }
        // |-------|
        //    |--|

        List<IdentifiedEntity> result = identifier.splitAndMergeOverlapping(
                Arrays.asList(
                        new IdentifiedEntity(
                                "AABBCC",
                                0,
                                "AABBCC".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP1")),
                                Collections.singleton(PartOfSpeechType.UNKNOWN)
                        ),
                        new IdentifiedEntity(
                                "BB",
                                2,
                                2 + "BB".length(),
                                Collections.singleton(new IdentifiedEntityType("FOO", "FOO", "NLP2")),
                                Collections.singleton(PartOfSpeechType.valueOf("NN"))
                        )
                )
        );

        assertNotNull(result);
        assertThat(result.toString(), result.size(), is(3));
    }

    @Test
    public void testUnreliable() throws Exception {
        ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/unreliableTest.json")));
        
        String input = "I was born on 19/12/1992";

        List<IdentifiedEntity> results = identifier.identify(input, Language.ENGLISH);

        assertEquals(1, results.size());
        for (IdentifiedEntity entity : results) {
            System.out.println(entity);
        }
    }
    
    @Test
    public void testHIPAA() throws Exception {
        String[] inputs = {
                "DR JOE WILL CALL PT",
                "2 days and taken off by Marvis because Marvis wanted",
                "IRON 140  39-150 umol/L",
                "Sincerely, John Doe MD",
                "John Doe MD Cool Office Company Rocks Medical Associates"
        };
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/hipaaPRIMAwithPos.json")) {
            ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream));

            for (String input : inputs) {
                System.out.println("--- " + input);
                List<IdentifiedEntity> results = identifier.identify(input, Language.ENGLISH);

                for (IdentifiedEntity entity : results) {
                    System.out.println(entity);
                }
            }
        }
    }
    
    @Test
    public void testInvalidAnnotatorNameAtWeights() {
        assertThrows(RuntimeException.class, () -> {
            try (InputStream primaAnnotatorConfig = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/PRIMA.json");
                 InputStream openNLPPOSTaggerConfig = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/opennlp.json");
                 InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/complexInvalidAnnotatorName.json")) {
                ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(
                        JsonUtils.MAPPER.readTree(inputStream),
                        new OpenNLPPOSTagger(JsonUtils.MAPPER.readTree(openNLPPOSTaggerConfig)),
                        new PRIMAAnnotator(JsonUtils.MAPPER.readTree(primaAnnotatorConfig))
                );

                assertNotNull(identifier);
            }
        });
    }
    
    @Test
    public void testPipelineBasic() throws Exception {
        ComplexFreeTextAnnotator identifier;

        try (
                InputStream primaAnnotatorConfig = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/PRIMA.json");
                InputStream openNLPPOSTaggerConfig = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/opennlp.json");
                InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/openNLPPrima.json")) {
             identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream),
                     new OpenNLPPOSTagger(JsonUtils.MAPPER.readTree(openNLPPOSTaggerConfig)),
                     new PRIMAAnnotator(JsonUtils.MAPPER.readTree(primaAnnotatorConfig))
            );
        }

        String note = "my email is foo@gmail.com";

        List<IdentifiedEntity> result = identifier.identify(note, Language.UNKNOWN);

        assertThat(result.size(), is(1));
    }

    @Test
    public void testComplexGeneratedFromConfiguration() throws Exception {
        try (InputStream is = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/complex-with-ids_precise.json")) {
            JsonNode configuration = JsonUtils.MAPPER.readTree(is);
            ComplexFreeTextAnnotator identifier = new ComplexFreeTextAnnotator(configuration);

            assertNotNull(identifier);
        }
    }


    @Test
    public void correctlyIdentifySSNEvenWhenInGarbageText() throws Exception {
        ComplexFreeTextAnnotator identifier;
        try (
                InputStream primaAnnotatorConfig = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/PRIMA.json");
                InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/testPrimaOnly.json")) {
             identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream),
                    new PRIMAAnnotator(JsonUtils.MAPPER.readTree(primaAnnotatorConfig)) // without pos tagger it should drop everything
            );
        }

        try (Reader reader = new InputStreamReader(new ByteArrayInputStream("This is my SSN: 123-12-1234".getBytes()))) {
            String note = IOUtils.toString(reader);

            List<IdentifiedEntity> result = identifier.identify(note, Language.UNKNOWN);

            assertNotNull(result);
            assertThat(result.toString(), result.size(), is(1));
        }
    }

    @Test
    public void verifiesOrganization_Something_LocationIsMergedToOrganization() throws Exception {
        final String text = "John works for Healthcare Center of Washington, which is a great place of work";
        final NLPAnnotator mockedIdentifier = mock(NLPAnnotator.class);

        when(mockedIdentifier.identify(eq(text), any())).thenReturn(Arrays.asList(
                new IdentifiedEntity("John", 0, "John".length(), Collections.singleton(new IdentifiedEntityType("NAME", "NAME", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.valueOf("NN"))),
                new IdentifiedEntity("Healthcare Center", 15, 15 + "Healthcare Center".length(), 
                        Collections.singleton(new IdentifiedEntityType("ORGANIZATION", "ORGANIZATION", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.valueOf("NN"))),
                new IdentifiedEntity("Washington", 36, 36 + "Washington".length(), Collections.singleton(new IdentifiedEntityType("LOCATION", "LOCATION", IdentifiedEntityType.UNKNOWN_SOURCE)), 
                        Collections.singleton(PartOfSpeechType.valueOf("NN")))
        ));
        when(mockedIdentifier.getName()).thenReturn("MOCKED");

        ComplexFreeTextAnnotator identifier;
        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/mocked.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), mockedIdentifier);
        }

        List<IdentifiedEntity> result = identifier.identify(text, Language.UNKNOWN);

        assertNotNull(result);
        assertThat(result.toString(), result.size(), is(2));
    }

    @Test
    public void repetitionOfEntitiesAreDetected() throws Exception {
        final String text = "John is also known as John";
        final NLPAnnotator mockedIdentifier = mock(NLPAnnotator.class);

        when(mockedIdentifier.identify(eq(text), any())).thenReturn(List.of(new IdentifiedEntity("John", 0, "John".length(),
                Collections.singleton(new IdentifiedEntityType("NAME", "NAME", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.valueOf("NN")))));
        when(mockedIdentifier.getName()).thenReturn("MOCKED");

        ComplexFreeTextAnnotator identifier;

        try (InputStream inputStream = ComplexFreeTextAnnotatorTest.class.getResourceAsStream("/mocked.json")) {
            identifier = new ComplexFreeTextAnnotator(JsonUtils.MAPPER.readTree(inputStream), mockedIdentifier);
        }

        List<IdentifiedEntity> identifiedEntities = identifier.identify(text, Language.UNKNOWN);

        assertNotNull(identifiedEntities);
        assertThat(identifiedEntities.size(), is(2));

        for (IdentifiedEntity entity : identifiedEntities) {
            assertNotNull(entity);
            assertEquals(entity.getText(), "John");

            final int begin = entity.getStart();
            final int end = entity.getEnd();

            assertTrue(begin == 0 && end == ("John".length()) || (begin == "John is also known as ".length() && end == text.length()));
        }
    }
}