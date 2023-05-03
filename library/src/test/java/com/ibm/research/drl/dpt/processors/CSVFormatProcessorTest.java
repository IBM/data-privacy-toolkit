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
package com.ibm.research.drl.dpt.processors;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.configuration.*;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.providers.identifiers.YOBIdentifier;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVFormatProcessorTest {
    @Test
    public void testStreamSupport() {
        assertTrue(new CSVFormatProcessor().supportsStreams());
    }

    @Test
    public void testWithMaskingAndFreetext() throws Exception {
        try (
                InputStream inputStream = CSVFormatProcessorTest.class.getResourceAsStream("/input_masking_freetext.csv");
                InputStream configuration = CSVFormatProcessorTest.class.getResourceAsStream("/configuration_masking_freetext.json");
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            ConfigurationManager configurationManager = ConfigurationManager.load(JsonUtils.MAPPER.readTree(configuration));

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();

            toBeMasked.put("Email", new DataMaskingTarget(ProviderType.RANDOM, "Email"));
            toBeMasked.put("Description", new DataMaskingTarget(ProviderType.FREE_TEXT, "Description"));
            toBeMasked.put("Description_EMAIL", new DataMaskingTarget(ProviderType.RANDOM, "Description_EMAIL"));
            toBeMasked.put("Description_ACCOUNT_ID", new DataMaskingTarget(ProviderType.HASH, "Description_ACCOUNT_ID"));

            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);


            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    toBeMasked, false, null, new CSVDatasetOptions(true, ',', '"', false));


            new CSVFormatProcessor().maskStream(inputStream, baos, factory, dataMaskingOptions, Collections.emptySet(), Collections.emptyMap());


            String text = baos.toString();

            assertThat(text.indexOf("foo@bar.com"), is(-1));
            assertThat(text.indexOf("ID234567"), is(-1));
        }

    }

    @Test
    public void testWithoutHeader() throws Exception {
        try (
                InputStream inputStream = new ByteArrayInputStream((
                                "A,B,18\n" +
                                "B,C,20\n" +
                                "A,D,40\n" +
                                "A,E,50"
                ).getBytes());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(outputStream)
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, Collections.emptyMap());
            String path = "Column 2";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));

            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions, Collections.emptySet(),  Collections.emptyMap());

            String maskedDataset = outputStream.toString();
            String header = "Column 0,Column 1,Column 2";

            assertFalse(maskedDataset.startsWith(header), maskedDataset);
        }
    }

    @Test
    public void testIdentification() throws Exception {
        try (InputStream inputStream = CSVFormatProcessorTest.class.getResourceAsStream("/100.csv")) {
            IdentificationReport identifiedTypes = new CSVFormatProcessor().identifyTypesStream(
                    inputStream,
                    DataTypeFormat.CSV,
                    new CSVDatasetOptions(false, ',', '"', false),
                    List.of(new YOBIdentifier()),
                    -1
            );

            assertNotNull(identifiedTypes);
        }
    }

    @Test
    @Disabled
    public void testPerformance() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());
        String path = "Column 2";


        Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
        identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));
        
        CSVFormatProcessor formatProcessor = new CSVFormatProcessor();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(outputStream);
        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
        
        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));

        long start = System.currentTimeMillis();
        
        for(int i = 0; i < 1000000; i++) {
            InputStream inputStream = new ByteArrayInputStream((
                    "A,B,18\n"
            ).getBytes()
            );

            formatProcessor.maskStream(inputStream, output, factory, dataMaskingOptions, Collections.emptySet(), Collections.emptyMap());
        }
        
        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }


    @Test
    public void testMaskTargetOtherField() throws Exception {
        try (
                InputStream inputStream = new ByteArrayInputStream((
                        "A,B,20\n" +
                        "A,C,20\n" +
                        "A,D,20\n" +
                        "A,E,20"
                ).getBytes()
                );
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "Column 0";
            String target = "Column 1";
            
            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put(path, new DataMaskingTarget(ProviderType.HASH, target));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV, toBeMasked, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);
            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions, Collections.emptySet(),  Collections.emptyMap());

            try (StringReader reader = new StringReader(output.toString())) {
                IPVDataset maskedDataset = IPVDataset.load(reader, false, ',', '"', false);

                for (int i = 0; i < maskedDataset.getNumberOfRows(); i++) {
                    assertEquals("A", maskedDataset.get(i, 0));
                    assertEquals("559AEAD08264D5795D3909718CDD05ABD49572E84FE55590EEF31A88A08FDFFD", maskedDataset.get(i, 1));
                    assertEquals("20", maskedDataset.get(i, 2));
                }
            }
        }
    }

    @Test
    public void testWithHeader() throws Exception {
        String header = "name,surname,age";
        try (
                InputStream inputStream = new ByteArrayInputStream((
                        header + "\n" +
                                "A,B,18\n" +
                                "B,C,20\n" +
                                "A,D,40\n" +
                                "A,E,50"
                ).getBytes()
                );
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(outputStream)
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "age";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, new CSVDatasetOptions(true, ',', '"', false));

            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(),  Collections.emptyMap());

            String maskedDataset = outputStream.toString();

            assertTrue(maskedDataset.startsWith(header), maskedDataset);
        }
    }

    @Test
    public void testFirstN() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/random1.txt")) {

            CSVFormatProcessor formatProcessor = new CSVFormatProcessor();
            Iterable<Record> records = formatProcessor.extractRecords(inputStream, new CSVDatasetOptions(false, ',', '"', false), 2);
           
            Iterator<Record> iterator = records.iterator();
            int counter = 0;
            
            while(iterator.hasNext()) {
                Record record = iterator.next();

                if (record.isHeader()) continue;
                counter++;
            }

            assertEquals(2, counter);
        }

    }
    
    @Test
    public void testIdentifyStreamCSV() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/random1.txt")) {

            IdentificationReport results = new CSVFormatProcessor().identifyTypesStream(inputStream, DataTypeFormat.CSV,
                    new CSVDatasetOptions(false, ',', '"', false), IdentifierFactory.defaultIdentifiers(),  -1);

            assertNotNull(results);
            assertThat(results.getBestTypes(), notNullValue());
            assertThat(results.getRawResults(), notNullValue());
        }
    }
    
    @Test
    public void testMaskStream() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/demo.csv");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream output = new PrintStream(outputStream)) {

            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "Column 0";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(), Collections.emptyMap());

            assertFalse(outputStream.toString().contains("/"));
        }
    }

    @Test
    public void testCompoundMasking() throws Exception {
        try (InputStream inputStream = CSVFormatProcessorTest.class.getResourceAsStream("/test_compound.csv");
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream output = new PrintStream(outputStream)) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();

            identifiedTypes.put("Column 1", new DataMaskingTarget(ProviderType.CITY, "Column 1"));
            identifiedTypes.put("Column 2", new DataMaskingTarget(ProviderType.COUNTRY, "Column 2"));

            Map<String, FieldRelationship> predefinedRelationships = new HashMap<>();
            predefinedRelationships.put("Column 2", new FieldRelationship(
                    ValueClass.LOCATION,
                    RelationshipType.LINKED,
                    "Column 1",
                    new RelationshipOperand[]{
                            new RelationshipOperand("Column 1", ProviderType.CITY)
                    }
            ));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false,
                    predefinedRelationships, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions, Collections.emptySet(), Collections.emptyMap());

            String result = outputStream.toString();

            System.out.println(result);
        }
    }

    @Test
    public void testIssue369CSVShouldCorrectlyHandleNulls() throws Exception {
        try (
                InputStream inputStream = new ByteArrayInputStream((
                        "One,Two,Three\n" +
                        "A,B,18\n" +
                        "B,C,20\n" +
                        "A,D,40\n" +
                        "A,E,50"
                ).getBytes());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(outputStream)
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, Collections.emptyMap());

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put("Two", new DataMaskingTarget(ProviderType.NULL, "Two"));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, new CSVDatasetOptions(true, ',', '"', false));

            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions, Collections.emptySet(),  Collections.emptyMap());

            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            try (MappingIterator<String[]> reader = mapper.readerFor(String[].class)
                    .with(
                            CsvSchema.emptySchema()
                                    .withColumnSeparator(',')
                                    .withQuoteChar('"')
                    ).readValues(outputStream.toString());) {

                assertThat(reader.next(), is(new String[]{"One", "Two", "Three"}));
                assertThat(reader.next(), is(new String[]{"A", "", "18"}));
                assertThat(reader.next(), is(new String[]{"B", "", "20"}));
                assertThat(reader.next(), is(new String[]{"A", "", "40"}));
                assertThat(reader.next(), is(new String[]{"A", "", "50"}));
            }
        }
    }

    @Test
    public void testSuppressShouldDropFieldWithoutHeaders() throws Exception {
        try (InputStream inputStream = new ByteArrayInputStream((
                "B,C,20\n" +
                "A,D,40\n" +
                "A,E,50"
        ).getBytes());

             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream output = new PrintStream(outputStream)) {

            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "Column 1";

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put(path, new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    toBeMasked, false, null, new CSVDatasetOptions(false, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);
            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(), Collections.emptyMap());

            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            try (MappingIterator<String[]> reader = mapper.readerFor(String[].class)
                    .with(
                            CsvSchema.emptySchema()
                                    .withColumnSeparator(',')
                                    .withQuoteChar('"')
                    )
                    .readValues(outputStream.toString());) {
                assertThat(reader.next(), is(new String[]{"B", "20"}));
                assertThat(reader.next(), is(new String[]{"A", "40"}));
                assertThat(reader.next(), is(new String[]{"A", "50"}));
            }
        }

    }

    @Test
    public void testSuppressShouldDropFieldWithHeaders() throws Exception {
        try (InputStream inputStream = new ByteArrayInputStream((
                "One,Two,Three\n" +
                "B,C,20\n" +
                "A,D,40\n" +
                "A,E,50"
        ).getBytes());

             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream output = new PrintStream(outputStream)) {

            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "Two";

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put(path, new DataMaskingTarget(ProviderType.SUPPRESS_FIELD, path));

            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    toBeMasked, false, null, new CSVDatasetOptions(true, ',', '"', false));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, toBeMasked);
            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(), Collections.emptyMap());

            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            try (MappingIterator<String[]> reader = mapper.readerFor(String[].class)
                    .with(
                            CsvSchema.emptySchema()
                                    .withColumnSeparator(',')
                                    .withQuoteChar('"')
                    )
                    .readValues(outputStream.toString());) {

                assertThat(reader.next(), is(new String[]{"One", "Three"}));
                assertThat(reader.next(), is(new String[]{"B", "20"}));
                assertThat(reader.next(), is(new String[]{"A", "40"}));
                assertThat(reader.next(), is(new String[]{"A", "50"}));
            }
        }
    }

    @Test
    public void testWithInvalidHeader_i410() throws IOException {
        String header = "";
        try (
                InputStream inputStream = new ByteArrayInputStream((
                        header + "\n" +
                                "A,B,18\n" +
                                "\n" +
                                "B,C,20\n" +
                                "\n" +
                                "A,D,40\n" +
                                "\n" +
                                ""
                ).getBytes()
                );
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(outputStream)
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "18";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, new CSVDatasetOptions(true, ',', '"', false));

            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions,  Collections.emptySet(),  Collections.emptyMap());

            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            try (MappingIterator<String[]> reader = mapper.readerFor(String[].class)
                    .with(
                            CsvSchema.emptySchema()
                                    .withColumnSeparator(',')
                                    .withQuoteChar('"')
                    )
                    .readValues(outputStream.toString());) {

                assertThat(reader.next(), is(new String[]{"A", "B", "18"}));
                assertThat(reader.next(), is(new String[]{"B", "C", "F5CA38F748A1D6EAF726B8A42FB575C3C71F1864A8143301782DE13DA2D9202B"}));
                assertThat(reader.next(), is(new String[]{"A", "D", "D59ECED1DED07F84C145592F65BDF854358E009C5CD705F5215BF18697FED103"}));
            }
        }
    }

    @Test
    public void testWithInvalidRows_i410() throws IOException {
        String header = "name,surname,age";
        try (
                InputStream inputStream = new ByteArrayInputStream((
                        header + "\n" +
                                "A,B,18\n" +
                                "\n" +
                                "B,C,20\n" +
                                "\n" +
                                "A,D,40\n" +
                                "\n" +
                                ""
                ).getBytes()
                );
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream output = new PrintStream(outputStream)
        ) {
            ConfigurationManager configurationManager = new ConfigurationManager(new DefaultMaskingConfiguration());

            String path = "age";

            Map<String, DataMaskingTarget> identifiedTypes = new HashMap<>();
            identifiedTypes.put(path, new DataMaskingTarget(ProviderType.HASH, path));
            MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, identifiedTypes);
            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV,
                    identifiedTypes, false, null, new CSVDatasetOptions(true, ',', '"', false));

            new CSVFormatProcessor().maskStream(inputStream, output, factory, dataMaskingOptions, Collections.emptySet(), Collections.emptyMap());

            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            try (MappingIterator<String[]> reader = mapper.readerFor(String[].class)
                    .with(
                            CsvSchema.emptySchema()
                                    .withColumnSeparator(',')
                                    .withQuoteChar('"')
                    )
                    .readValues(outputStream.toString());) {

                assertThat(reader.next(), is(new String[]{"name", "surname", "age"}));
                assertThat(reader.next(), is(new String[]{"A", "B", "4EC9599FC203D176A301536C2E091A19BC852759B255BD6818810A42C5FED14A"}));
                assertThat(reader.next(), is(new String[]{"B", "C", "F5CA38F748A1D6EAF726B8A42FB575C3C71F1864A8143301782DE13DA2D9202B"}));
                assertThat(reader.next(), is(new String[]{"A", "D", "D59ECED1DED07F84C145592F65BDF854358E009C5CD705F5215BF18697FED103"}));
            }
        }
    }

    @Test
    public void validateTrimOption() throws Exception {
        CSVFormatProcessor processor = new CSVFormatProcessor();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("   field1,  field2   ,field3".getBytes())) {
            Record record = processor.extractRecords(inputStream, new CSVDatasetOptions(
                    false,
                    ',',
                    '"',
                    false
            )).iterator().next();

            assertThat(new String(record.getFieldValue("Column 0")), is("   field1"));
            assertThat(new String(record.getFieldValue("Column 1")), is("  field2   "));
            assertThat(new String(record.getFieldValue("Column 2")), is("field3"));
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("   field1,  field2   ,field3".getBytes())) {
            Record record = processor.extractRecords(inputStream, new CSVDatasetOptions(
                    false,
                    ',',
                    '"',
                    true
            )).iterator().next();

            assertThat(new String(record.getFieldValue("Column 0")), is("field1"));
            assertThat(new String(record.getFieldValue("Column 1")), is("field2"));
            assertThat(new String(record.getFieldValue("Column 2")), is("field3"));
        }
    }
}
