/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.processors.records.JSONRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.Duration.ofMillis;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class JSONFormatProcessorTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void identificationOfMultipleObjects() throws Exception {
        String json = "" +
                "{\n" +
                "  \"id\": \"1\",\n" +
                "  \"foo\": \"bar\"\n" +
                "}\n" +
                "{\n" +
                "\"id\": \"2\",\n" +
                "\"foo\": \"bar\"\n" +
                "}\n" +
                "{\n" +
                "\"id\": \"3\",\n" +
                "\"foo\": \"bar\"\n" +
                "}";

        JSONFormatProcessor processor = new JSONFormatProcessor();

        try (InputStream input = new ByteArrayInputStream(json.getBytes());) {
            for (Record record : processor.extractRecords(
                    input, new JSONDatasetOptions()
            )) {
                System.out.println(record);
            }
        }
    }

    @Test
    public void verifyNodesAreRemoved() throws Exception {
        String json = "" +
                "{\"a\":{\"b\":123,\"c\":[123]}}" +
                "{\"a\":{\"b\":{\"a\":\"foo\"},\"d\":\"foo\"}}" +
                "{\"a\":{\"b\":[123]}}";

        ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/a/b", new DataMaskingTarget(ProviderType.NULL, "/a/b"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);

        try (
                InputStream input = new ByteArrayInputStream(json.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
        ) {
            new JSONFormatProcessor().maskStream(
                    input, output,
                    factory,
                    dataMaskingOptions,
                    Collections.emptySet(),
                    Collections.emptyMap()
            );

            JsonParser parser = mapper.getFactory().createParser(output.toString());
            MappingIterator<JsonNode> iterator = new ObjectMapper().readerFor(JsonNode.class).readValues(parser);

            assertThat(iterator, is(not(nullValue())));
            assertThat(iterator.hasNext(),is(true));

            while(iterator.hasNext()) {
                JsonNode currentNode = iterator.next();
                System.out.println(currentNode);
                assertThat(currentNode.get("a"), is(not(nullValue())));
                assertThat(currentNode.get("a").get("b"), is(not(nullValue())));
                assertThat(currentNode.get("a").get("b").isNull(), is(true));
            }
        }
    }

    @Test
    public void testOperationOnArrayElements() throws Exception {
        String json = "[{\"a\": \"foo@gmail.com\"}, {\"a\" : \"boo@gmail.com\"}, null, {\"a\": \"goo@gmail.com\"}]";

        JSONFormatProcessor processor = new JSONFormatProcessor();

        try (InputStream input = new ByteArrayInputStream(json.getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream();
  ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put("/*/a", new DataMaskingTarget(ProviderType.REDACT, "/*/a"));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                    DataTypeFormat.JSON, DataTypeFormat.JSON,
                    toBeMasked,
                    false,
                    Collections.emptyMap(),
                    new JSONDatasetOptions()
            );
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);

            processor.maskStream(
                    input, output,
                    factory,
                    dataMaskingOptions,
                    Collections.emptySet(),
                    Collections.emptyMap()
            );

            //System.out.println(baos.toString());
        }
    }

    @Test
    public void testMaskJSONFile() throws Exception {
        try (
                InputStream inputStream = this.getClass().getResourceAsStream("/input.json");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(baos)
        ) {
            JSONFormatProcessor formatProcessor = new JSONFormatProcessor();


            JsonNode configurationTree = mapper.readTree("{\n" +
                    "\"toBeMasked\":{\n" +
                    "\"/dialogs/0/dialogContent/dialog/0/user\": \"HASH\",\n" +
                    "\"/dialogs/0/dialogContent/dialog/0/message\": \"REDACT\"\n" +
                    "},\n" +
                    "\"inputFormat\":\"JSON\",\n" +
                    "\"outputFormat\":\"JSON\",\n" +
                    "\"_fields\":{},\n" +
                    "\"_defaults\":{}\n" +
                    "}");

            final ConfigurationManager configurationManager = ConfigurationManager.load(configurationTree);
            final DataMaskingOptions maskingOptions = mapper.treeToValue(configurationTree, DataMaskingOptions.class);
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, Collections.emptyMap());
            formatProcessor.maskStream(inputStream, output, factory, maskingOptions, new HashSet<>(), null);

            try (InputStream originalStream = this.getClass().getResourceAsStream("/input.json")) {
                JsonNode original = mapper.readTree(originalStream);
                JsonNode masked = mapper.readTree(baos.toByteArray());

                String path1 = "/dialogs/0/dialogContent/dialog/0/user";
                String path2 = "/dialogs/0/dialogContent/dialog/0/message";

                assertThat(original.at(path1).asText(), not(equalTo(masked.at(path1).asText())));
                assertThat(original.at(path2).asText(), not(equalTo(masked.at(path2).asText())));
            }
        }
    }

    @Test
    public void testIdentifyJSONElementsInnerObjects() throws Exception {
        String jsonS = "[{\"a\": \"foo@gmail.com\"}, {\"a\" : \"boo@gmail.com\"}, null, {\"a\": \"goo@gmail.com\"}]";

        IdentificationReport allResults =
                new JSONFormatProcessor().identifyTypesStream(new ByteArrayInputStream(jsonS.getBytes()), DataTypeFormat.JSON,
                        new JSONDatasetOptions(), IdentifierFactory.defaultIdentifiers(), -1);
        
        Map<String, IdentifiedType> results = allResults.getBestTypes();

        results = results.entrySet().stream().filter(
                entry -> !entry.getValue().getTypeName().equals(ProviderType.UNKNOWN.name())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(1, results.size(), results.toString());
        assertEquals(ProviderType.EMAIL.name(), results.get("/*/a").getTypeName());
    }

    @Test
    public void testIdentifyMultipleObjectSingleFile() throws Exception {
        String jsonS = "{\"a\": \"foo@gmail.com\"}\n{\"a\" : \"boo@gmail.com\"}\n{\"a\": \"goo@gmail.com\"}";

        IdentificationReport allResults =
                new JSONFormatProcessor().identifyTypesStream(new ByteArrayInputStream(jsonS.getBytes()), DataTypeFormat.JSON,
                        new JSONDatasetOptions(), IdentifierFactory.defaultIdentifiers(), -1);

        Map<String, IdentifiedType> results = allResults.getBestTypes();

        results = results.entrySet().stream().filter(
                entry -> !entry.getValue().getTypeName().equals(ProviderType.UNKNOWN.name())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(1, results.size(), results.toString());
        assertEquals(ProviderType.EMAIL.name(), results.get("/a").getTypeName());
        assertEquals(3L, results.get("/a").getCount());
    }

    @Test
    public void testIdentifyJSONElementsInnerObjects2() throws Exception {
        String jsonS = "{\"a\" : [{\"a\": \"foo@gmail.com\"}, {\"a\" : \"boo@gmail.com\"}, null, {\"a\": \"goo@gmail.com\"}]}";

        IdentificationReport allResults =
                new JSONFormatProcessor().identifyTypesStream(new ByteArrayInputStream(jsonS.getBytes()), DataTypeFormat.JSON, new JSONDatasetOptions(), IdentifierFactory.defaultIdentifiers(), -1);
        
        Map<String, IdentifiedType> results = allResults.getBestTypes();

        results = results.entrySet().stream().filter(
                entry -> !entry.getValue().getTypeName().equals(ProviderType.UNKNOWN.name())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(1, results.size(), results.toString());
        assertEquals(ProviderType.EMAIL.name(), results.get("/a/*/a").getTypeName());
        assertThat(
                allResults.getBestTypes().entrySet().stream().filter(
                        entry -> entry.getValue().getTypeName().equals(ProviderType.UNKNOWN.name())
                ).count(), is(1L));
    }

    @Test
    public void testIdentifyJSONElementsWithProcessor() throws Exception {
        String jsonS = "{\"a\": [\"foo@gmail.com\"], \"b\": null, \"c\": \"John\", \"d\": 123}";

        IdentificationReport allResults =
                new JSONFormatProcessor().identifyTypesStream(new ByteArrayInputStream(jsonS.getBytes()), DataTypeFormat.JSON, new JSONDatasetOptions(), IdentifierFactory.defaultIdentifiers(), -1);
        
        Map<String, IdentifiedType> results = allResults.getBestTypes();

        results = results.entrySet().stream().filter(
                entry -> !entry.getValue().getTypeName().equals(ProviderType.UNKNOWN.name())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(3, results.size(), results.toString());
        assertEquals(ProviderType.NAME.name(), results.get("/c").getTypeName());
        assertEquals(ProviderType.NUMERIC.name(), results.get("/d").getTypeName());
        assertEquals(ProviderType.EMAIL.name(), results.get("/a/*").getTypeName());
    }
    @Test
    public void testIdentifyJSONElements() throws Exception {
        String jsonS = "{\"a\": [\"foo@gmail.com\"], \"b\": null, \"c\": \"John\", \"d\": 123}";

        IdentificationReport allResults = new JSONFormatProcessor()
                .identifyTypesStream(
                        new ByteArrayInputStream(jsonS.getBytes()),
                        DataTypeFormat.JSON,
                        new JSONDatasetOptions(),
                        IdentifierFactory.defaultIdentifiers(),
                        -1);

        Map<String, IdentifiedType> results = allResults.getBestTypes();
        
        results = results.entrySet().stream().filter(
                entry -> !entry.getValue().getTypeName().equals(ProviderType.UNKNOWN.name())
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(3, results.size());
        assertEquals(ProviderType.EMAIL.name(), results.get("/a/*").getTypeName());
        assertEquals(ProviderType.NAME.name(), results.get("/c").getTypeName());
        assertEquals(ProviderType.NUMERIC.name(), results.get("/d").getTypeName());
    }

    @Test
    public void testIdentifyStreamJSON() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/fhir/deviceExampleOneLine.json");

        IdentificationReport results
                = new JSONFormatProcessor().identifyTypesStream(inputStream, DataTypeFormat.JSON, new JSONDatasetOptions(), IdentifierFactory.defaultIdentifiers(), -1);

        assertThat(results.getRawResults().size(), not(0));
    }

    @Test
    public void testMultipleReads() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/fhir/deviceExampleOneLine.json") ) {
            JSONFormatProcessor processor = new JSONFormatProcessor();

            List<Record> records = new ArrayList<>();

            processor.extractRecords(inputStream, new JSONDatasetOptions()).iterator().forEachRemaining(records::add);

            assertFalse(records.isEmpty());
            assertThat(records.size(), is(2));
        }
    }


    @Test
    public void testTraverseSingleObject() throws Exception {
        String jsonS = "{\"a\": 2}";

        List<String> references = new ArrayList<>();
        new JSONRecord(mapper.readTree(jsonS)).getFieldReferences().iterator().forEachRemaining(references::add);


        assertEquals(1, references.size());
        assertEquals(1, references.size());
    }

    @Test
    public void testTraverseArray() throws Exception {
        String jsonS = "[\"a\", 2]";

        List<String> references = new ArrayList<>();
        new JSONRecord(mapper.readTree(jsonS)).getFieldReferences().iterator().forEachRemaining(references::add);

        assertEquals(2, references.size());
        assertTrue(references.contains("/0"));
        assertTrue(references.contains("/1"));
    }

    @Test
    public void testTraverseArrayInnerObject() throws Exception {
        String jsonS = "[{\"c\": 2}, {\"c\": 3} , {\"d\": 3}]";


        List<String> references = new ArrayList<>();
        new JSONRecord(mapper.readTree(jsonS)).getFieldReferences().iterator().forEachRemaining(references::add);

        assertEquals(3, references.size());
        assertTrue(references.contains("/2/d"));
        assertEquals(2, references.stream().filter( v -> v.endsWith("/c")).count());
    }

    @Test
    public void testTraverseArrayInnerObject2() throws Exception {
        String jsonS = "{\"b\": [{\"c\": 2, \"d\": 3}]}";

        List<String> references = new ArrayList<>();
        new JSONRecord(mapper.readTree(jsonS)).getFieldReferences().iterator().forEachRemaining(references::add);

        assertEquals(2, references.size());
        assertTrue(references.contains("/b/0/c"));
        assertTrue(references.contains("/b/0/d"));
    }

    @Test
    public void testSimpleRelationship() throws Exception {
        String jsonString = "{" +
                "\"id\": \"fooo\"," +
                "\"name\": \"bar\"" +
                "}";


        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/name", new DataMaskingTarget(ProviderType.HASH, "/name"));

        Map<String, FieldRelationship> predifinedRelationships = new HashMap<>();
        predifinedRelationships.put("/name", new FieldRelationship(
                ValueClass.TEXT,
                RelationshipType.KEY,
                "/name",
                new RelationshipOperand[]{
                        new RelationshipOperand("/id")
                }
        ));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked, false,
                predifinedRelationships,
                new JSONDatasetOptions()
                );

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(baos)
        ) {
            try (
                    ByteArrayInputStream bais = new ByteArrayInputStream(jsonString.getBytes())
            ) {

                new JSONFormatProcessor().maskStream(
                        bais,
                        output,
                        new MaskingProviderFactory(new ConfigurationManager(new DefaultMaskingConfiguration()), Collections.emptyMap()),
                        dataMaskingOptions,
                        Collections.emptySet(),
                        Collections.emptyMap()
                );
            }
        }
    }

    @Test
    public void testSimpleRelationshipOnArray() throws Exception {
        String jsonString = "{" +
                "\"id\": \"fooo\"," +
                "\"names\": [\"bar\"]" +
                "}";


        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/names/*", new DataMaskingTarget(ProviderType.HASH, "/names/*"));

        Map<String, FieldRelationship> predifinedRelationships = new HashMap<>();
        predifinedRelationships.put("/names/*", new FieldRelationship(
                ValueClass.TEXT,
                RelationshipType.KEY,
                "/names/*",
                new RelationshipOperand[]{
                        new RelationshipOperand("/id")
                }
        ));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked, false,
                predifinedRelationships,
                new JSONDatasetOptions()
        );

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(baos)
        ) {
            try (
                    ByteArrayInputStream bais = new ByteArrayInputStream(jsonString.getBytes())
            ) {

                new JSONFormatProcessor().maskStream(
                        bais,
                        output,
                        new MaskingProviderFactory(new ConfigurationManager(new DefaultMaskingConfiguration()), Collections.emptyMap()),
                        dataMaskingOptions,
                        Collections.emptySet(),
                        Collections.emptyMap()
                );
            }

            System.out.println(baos.toString());
        }
    }

    @Test
    public void testSimpleRelationshipOnArrayWithRelative() throws Exception {
        String jsonString = "{" +
                "\"names\": [" +
                    "{" +
                "\"id\":\"bar\"," +
                "\"obj\":\"bar\"}" +
                "]" +
                "}";


        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/names/*/obj", new DataMaskingTarget(ProviderType.HASH, "/names/*/obj"));

        Map<String, FieldRelationship> predifinedRelationships = new HashMap<>();
        predifinedRelationships.put("/names/*/obj", new FieldRelationship(
                ValueClass.TEXT,
                RelationshipType.KEY,
                "/names/*/obj",
                new RelationshipOperand[]{
                        new RelationshipOperand("id")
                }
        ));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked, false,
                predifinedRelationships,
                new JSONDatasetOptions()
        );

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(baos)
        ) {
            try (
                    ByteArrayInputStream bais = new ByteArrayInputStream(jsonString.getBytes())
            ) {

                new JSONFormatProcessor().maskStream(
                        bais,
                        output,
                        new MaskingProviderFactory(new ConfigurationManager(new DefaultMaskingConfiguration()), Collections.emptyMap()),
                        dataMaskingOptions,
                        Collections.emptySet(),
                        Collections.emptyMap()
                );
            }

            System.out.println(baos.toString());
        }
    }

    @Test
    public void testCompoundOperandNotInMaskedList() {
        assertTimeout(ofMillis(2000L), () -> {

            JSONFormatProcessor formatProcessor = new JSONFormatProcessor();

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put("/date", new DataMaskingTarget(ProviderType.DATETIME, "/date"));

            Map<String, FieldRelationship> relationships = new HashMap<>();
            relationships.put("/date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "/date", Arrays.asList(new RelationshipOperand("userid"))));

            MaskingProviderFactory mpf = new MaskingProviderFactory();
            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                    DataTypeFormat.JSON,
                    DataTypeFormat.JSON,
                    toBeMasked,
                    false,
                    relationships,
                    null
            );

            String originalDate = "28-11-2017";

            String user1_date = null;
            ObjectMapper mapper = new ObjectMapper();

            for (int i = 0; i < 100; i++) {
                //maskRecord returns a reference to the same object of the first argument so we need to create a new record each time
                Record record = new JSONRecord(mapper.readTree("{\"userid\": \"user1\", \"date\" : \"" + originalDate + "\"}"));
                Record masked = formatProcessor.maskRecord(record, mpf, new HashSet<>(), dataMaskingOptions);
                String maskedDate = new String(masked.getFieldValue("/date"));

                assertNotEquals(maskedDate, originalDate);

                if (user1_date != null) {
                    assertEquals(user1_date, maskedDate);
                }

                user1_date = maskedDate;
            }


            String user2_date = null;

            for (int i = 0; i < 100; i++) {
                Record record = new JSONRecord(mapper.readTree("{\"userid\": \"user2\", \"date\" : \"" + originalDate + "\"}"));
                Record masked = formatProcessor.maskRecord(record, mpf, new HashSet<>(), dataMaskingOptions);
                String maskedDate = new String(masked.getFieldValue("/date"));

                assertNotEquals(maskedDate, originalDate);

                if (user2_date != null) {
                    assertEquals(user2_date, maskedDate);
                }

                user2_date = maskedDate;
            }

            assertNotEquals(user1_date, user2_date);

        });
    }

    @Test
    public void testCompoundOperandIsNull() throws Exception {

        JSONFormatProcessor formatProcessor = new JSONFormatProcessor();

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/operand", new DataMaskingTarget(ProviderType.DATETIME, "/operand"));
        toBeMasked.put("/date", new DataMaskingTarget(ProviderType.DATETIME, "/date"));

        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("/date",
                new FieldRelationship(ValueClass.DATE, RelationshipType.DISTANCE, "" +
                        "/date", Arrays.asList(new RelationshipOperand("/operand"))));

        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory();

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON,
                DataTypeFormat.JSON,
                toBeMasked,
                false,
                relationships,
                null
        );


        String originalDate = "28-11-2017";

        JSONRecord record = new JSONRecord(new ObjectMapper().readTree("{\"operand\": null, \"date\": \"" + originalDate +"\"}"));

        //maskRecord returns a reference to the same object of the first argument so we need to create a new record each time
        Record masked = formatProcessor.maskRecord(record, maskingProviderFactory, new HashSet<>(), dataMaskingOptions);
        String maskedDate = new String(masked.getFieldValue("/date"));

        assertNotEquals(maskedDate, originalDate);

    }

    @Test
    public void testRelationshipRelative() throws  Exception {

        DataMaskingOptions dataMaskingOptions  = mapper.readValue(this.getClass().getResourceAsStream("/relative_rel_masking.json"), DataMaskingOptions.class);
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), dataMaskingOptions.getToBeMasked());

        JSONFormatProcessor formatProcessor = new JSONFormatProcessor();

        Record record = new JSONRecord(mapper.readTree(this.getClass().getResourceAsStream("/relative_rel_input.json")));

        Record masked = formatProcessor.maskRecord(record, maskingProviderFactory, Collections.emptySet(), dataMaskingOptions);

        assertEquals("xyz", new String(masked.getFieldValue("/a/b/0/name")));
        assertEquals("abc", new String(masked.getFieldValue("/a/b/1/name")));
    }

    @Test
    public void testRelationshipAbsolute() throws  Exception {

        DataMaskingOptions dataMaskingOptions  = mapper.readValue(this.getClass().getResourceAsStream("/relative_abs_masking.json"), DataMaskingOptions.class);
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), dataMaskingOptions.getToBeMasked());

        JSONFormatProcessor formatProcessor = new JSONFormatProcessor();

        Record record = new JSONRecord(mapper.readTree(this.getClass().getResourceAsStream("/relative_rel_input.json")));

        Record masked = formatProcessor.maskRecord(record, maskingProviderFactory, Collections.emptySet(), dataMaskingOptions);

        assertEquals("123", new String(masked.getFieldValue("/a/b/0/name")));
        assertEquals("123", new String(masked.getFieldValue("/a/b/1/name")));
    }

    @Test
    public void testRelationshipAbsoluteOperandMissing() throws  Exception {

        DataMaskingOptions dataMaskingOptions  = mapper.readValue(this.getClass().getResourceAsStream("/relative_abs_masking.json"), DataMaskingOptions.class);
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), dataMaskingOptions.getToBeMasked());

        JSONFormatProcessor formatProcessor = new JSONFormatProcessor();

        Record record = new JSONRecord(mapper.readTree(this.getClass().getResourceAsStream("/global_rel_missing.json")));

        Record masked = formatProcessor.maskRecord(record, maskingProviderFactory, Collections.emptySet(), dataMaskingOptions);

        assertEquals("", new String(masked.getFieldValue("/a/b/0/name")));
        assertEquals("", new String(masked.getFieldValue("/a/b/1/name")));
    }

    @Test
    public void testRelationshipRelativeOperandMissing() throws  Exception {

        DataMaskingOptions dataMaskingOptions  = mapper.readValue(this.getClass().getResourceAsStream("/relative_rel_masking.json"), DataMaskingOptions.class);
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), dataMaskingOptions.getToBeMasked());

        JSONFormatProcessor formatProcessor = new JSONFormatProcessor();

        Record record = new JSONRecord(mapper.readTree(this.getClass().getResourceAsStream("/global_rel_missing.json")));

        Record masked = formatProcessor.maskRecord(record, maskingProviderFactory, Collections.emptySet(), dataMaskingOptions);

        assertEquals("", new String(masked.getFieldValue("/a/b/0/name")));
        assertEquals("abc", new String(masked.getFieldValue("/a/b/1/name")));
    }

    @Test
    public void testMaskMultipleObjectSingleFile() throws Exception {
        String jsonS = "{\"a\": \"foo@gmail.com\"}\n" +
                "{\"a\" : \"boo@gmail.com\"}\n" +
                "{\"a\": \"goo@gmail.com\"}\n";

        JSONFormatProcessor processor = new JSONFormatProcessor();
        Map<String, DataMaskingTarget> toBeMasked = new HashMap<String, DataMaskingTarget>() {{
            put("/a", new DataMaskingTarget(ProviderType.REDACT, "/a"));
        }};
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        MaskingProviderFactory factory = new MaskingProviderFactory(
            new ConfigurationManager(),
                toBeMasked
        );
        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON,
                DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );


        String output;
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
                ) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonS.getBytes())) {

                processor.maskStream(
                        inputStream,
                        outputStream,
                        factory,
                        dataMaskingOptions,
                        Collections.emptySet(),
                        Collections.emptyMap()
                );
            }

            output = outputStream.toString();
        }

        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertEquals(output.indexOf('@'), -1);
    }

    @Test
    public void testMaskMultipleObjectSingleFileWithArrays() throws Exception {
        String jsonS = "{\"a\": [{\"a\":\"foo@gmail.com\"}]}\n" +
                "{\"a\": [{\"a\":\"foo@gmail.com\"}]}\n";

        JSONFormatProcessor processor = new JSONFormatProcessor();
        Map<String, DataMaskingTarget> toBeMasked = new HashMap<String, DataMaskingTarget>() {{
            put("/a/*/a", new DataMaskingTarget(ProviderType.REDACT, "/a/*/a"));
        }};
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(),
                toBeMasked
        );
        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON,
                DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );


        String output;
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonS.getBytes())) {

                processor.maskStream(
                        inputStream,
                        outputStream,
                        factory,
                        dataMaskingOptions,
                        Collections.emptySet(),
                        Collections.emptyMap()
                );
            }

            output = outputStream.toString();
        }

        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertEquals(output.indexOf('@'), -1);
    }

    @Test
    public void testMaskWithDifferentTarget() throws Exception {
        String jsonS = "{\"a\" :\"foo@gmail.com\"}\n" +
                "{\"a\": \"foo@gmail.com\"}\n";

        JSONFormatProcessor processor = new JSONFormatProcessor();
        Map<String, DataMaskingTarget> toBeMasked = new HashMap<String, DataMaskingTarget>() {{
            put("/a", new DataMaskingTarget(ProviderType.REDACT, "/b"));
        }};

        MaskingProviderFactory factory = new MaskingProviderFactory(
                new ConfigurationManager(),
                toBeMasked
        );
        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON,
                DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );

        String output;
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonS.getBytes())) {

                processor.maskStream(
                        inputStream,
                        outputStream,
                        factory,
                        dataMaskingOptions,
                        Collections.emptySet(),
                        Collections.emptyMap()
                );
            }

            output = outputStream.toString();
        }

        assertNotNull(output);
        assertFalse(output.isEmpty());
        assertNotEquals(output.indexOf("\"b\":"), -1);
    }

    @Test
    public void testSuppressShouldDropField() throws Exception {
        String json = "" +
                "{\"a\":{\"b\":123,\"c\":[123]}}";

        ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/a/b", new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, "/a/b"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);

        try (
                InputStream input = new ByteArrayInputStream(json.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
        ) {
            new JSONFormatProcessor().maskStream(
                    input, output,
                    factory,
                    dataMaskingOptions,
                    Collections.emptySet(),
                    Collections.emptyMap()
            );

            JsonNode masked = mapper.readTree(output.toString());

            assertThat(masked.get("a"), is(not(nullValue())));
            assertThat(masked.get("a").get("b"), is(nullValue()));
            assertThat(masked.get("a").get("c"), is(not(nullValue())));

        }
    }

    @Test
    public void testI390SuppressOnArraysShouldDropField() throws Exception {
        String json = "" +
                "[{\"a\":{\"b\":123,\"c\":[123]}}," +
                "{\"a\":{\"b\":123,\"c\":[123]}}]";

        ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/*/a/b", new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, "/*/a/b"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);

        try (
                InputStream input = new ByteArrayInputStream(json.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
        ) {
            new JSONFormatProcessor().maskStream(
                    input, output,
                    factory,
                    dataMaskingOptions,
                    Collections.emptySet(),
                    Collections.emptyMap()
            );

            JsonNode masked = mapper.readTree(output.toString());

            assertThat(masked.get(0).get("a"), is(not(nullValue())));
            assertThat(masked.get(0).get("a").get("b"), is(nullValue()));
            assertThat(masked.get(0).get("a").get("c"), is(not(nullValue())));

            assertThat(masked.get(1).get("a"), is(not(nullValue())));
            assertThat(masked.get(1).get("a").get("b"), is(nullValue()));
            assertThat(masked.get(1).get("a").get("c"), is(not(nullValue())));

        }
    }


    @Test
    public void testI382NullMaskingProviderShouldReplaceWithNull() throws Exception {
        String json = "" +
                "{\"a\":{\"b\":123,\"c\":[123]}}";

        DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();
        defaultMaskingConfiguration.setValue("null.mask.returnNull", true);

        ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/a/c", new DataMaskingTarget(ProviderType.NULL, "/a/c"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);

        try (
                InputStream input = new ByteArrayInputStream(json.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
        ) {
            new JSONFormatProcessor().maskStream(
                    input, output,
                    factory,
                    dataMaskingOptions,
                    Collections.emptySet(),
                    Collections.emptyMap()
            );

            JsonNode masked = mapper.readTree(output.toString());

            assertThat(masked.get("a"), is(not(nullValue())));
            assertThat(masked.get("a").get("b"), is(not(nullValue())));
            assertThat(masked.get("a").get("c").isNull(), is(true));
        }
    }

    @Test
    public void testI382NullMaskingProviderShouldReplaceWithEmptyString() throws Exception {
        String json = "" +
                "{\"a\":{\"b\":123,\"c\":[123]}}";

        DefaultMaskingConfiguration defaultMaskingConfiguration = new DefaultMaskingConfiguration();
        defaultMaskingConfiguration.setValue("null.mask.returnNull", false);

        ConfigurationManager configurationManager = new ConfigurationManager(defaultMaskingConfiguration);

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/a/c", new DataMaskingTarget(ProviderType.NULL, "/a/c"));

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON, DataTypeFormat.JSON,
                toBeMasked,
                false,
                Collections.emptyMap(),
                new JSONDatasetOptions()
        );
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);

        try (
                InputStream input = new ByteArrayInputStream(json.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
        ) {
            new JSONFormatProcessor().maskStream(
                    input, output,
                    factory,
                    dataMaskingOptions,
                    Collections.emptySet(),
                    Collections.emptyMap()
            );

            JsonNode masked = mapper.readTree(output.toString());

            assertThat(masked.get("a"), is(not(nullValue())));
            assertThat(masked.get("a").get("b"), is(not(nullValue())));
            assertThat(masked.get("a").get("c"), is(not(nullValue())));
            assertThat(masked.get("a").get("c").asText(), is(""));

        }
    }
}
