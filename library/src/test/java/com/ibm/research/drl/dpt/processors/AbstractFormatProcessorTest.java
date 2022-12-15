/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.*;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.processors.records.CSVRecord;
import com.ibm.research.drl.dpt.processors.records.JSONRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import org.beer30.jdefault.JDefaultBusiness;
import org.beer30.jdefault.JDefaultIdentity;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static java.time.Duration.ofMillis;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AbstractFormatProcessorTest {

    @Test
    public void maskEmptyStreamIsProcessedCorrectlyStream() throws Exception {
        FormatProcessor processor = new AbstractFormatProcessor() {
            @Override
            public boolean supportsStreams() {
                return true;
            }

            @Override
            protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions datasetOptions, int firstN) {
                return Collections.emptyList();
            }
        };

        try (
                InputStream input = new ByteArrayInputStream( new byte[0] );
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                PrintStream print = new PrintStream(output)
        ) {
            MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());
            processor.maskStream( input, print, factory, new DataMaskingOptions(
                    DataTypeFormat.CSV,
                    DataTypeFormat.CSV,
                    Collections.emptyMap(),
                    false,
                    Collections.emptyMap(),
                    new CSVDatasetOptions(false, ',', '"', false)
            ), Collections.emptySet(), Collections.emptyMap());

            String result = output.toString();
            assertTrue(result.isEmpty());
        }
    }

    @Test
    public void maskStreamRegisterTypes() throws Exception {
        Map<String, Integer> fieldNames = new HashMap<>();
        fieldNames.put("c0", 0);

        Record csvRecord = new CSVRecord(new String[] {"foo"}, fieldNames,
                new CSVDatasetOptions(false, ',', '"', false), false);

        FormatProcessor processor = new AbstractFormatProcessor() {
            @Override
            public boolean supportsStreams() {
                return true;
            }

            @Override
            protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions datasetOptions, int firstN) {
                return Collections.singleton(csvRecord);
            }
        };

        try (
                InputStream input = new ByteArrayInputStream( new byte[0] );
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                PrintStream print = new PrintStream(output)
        ) {

            MaskingProviderFactory factory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

            Map<ProviderType, Class<? extends MaskingProvider>> registerTypes = new HashMap<>();
            registerTypes.put(ProviderType.valueOf("FIXED"), FixedMaskingProvider.class);

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put("c0", new DataMaskingTarget(ProviderType.valueOf("FIXED"), "c0"));

            processor.maskStream( input, print, factory, new DataMaskingOptions(
                    DataTypeFormat.CSV,
                    DataTypeFormat.CSV,
                    toBeMasked,
                    false,
                    Collections.emptyMap(),
                    new CSVDatasetOptions(false, ',', '"', false)
            ), Collections.emptySet(), registerTypes);

            String result = output.toString();
            assertEquals("FIXED", result.trim());
        }
    }

    @Test
    public void testProtectRecordAlreadyMasked() throws Exception {
        String ssn = JDefaultIdentity.ssn(true);
        String creditCard = JDefaultBusiness.creditCardNumber(JDefaultBusiness.creditCardType());

        Record record = mock(Record.class);
        when(record.getFieldValue("field0")).thenReturn(creditCard.getBytes());
        when(record.getFieldValue("field1")).thenReturn(ssn.getBytes());
        when(record.toString()).thenReturn("0,1");

        FormatProcessor processor =  new AbstractFormatProcessor() {
            @Override
            public boolean supportsStreams() {
                return true;
            }

            @Override
            protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions datasetOptions, int firstN) {
                return Collections.singletonList(
                        record
                );
            }
        };

        try (
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                PrintStream print = new PrintStream(output)
        ) {
            Map<String, DataMaskingTarget> identifiedFields = new HashMap<>();
            identifiedFields.put("field0", new DataMaskingTarget(ProviderType.CREDIT_CARD, "field0"));
            identifiedFields.put("field1", new DataMaskingTarget(ProviderType.NATIONAL_ID, "field1"));

            Set<String> alreadyMasked = new HashSet<>();
            alreadyMasked.add("field1");

            processor.maskStream(null, print, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()), new DataMaskingOptions(
                    DataTypeFormat.CSV,
                    DataTypeFormat.CSV,
                    identifiedFields,
                    false,
                    Collections.emptyMap(),
                    new CSVDatasetOptions(false, ',', '"', false)
            ), alreadyMasked, Collections.emptyMap());

            assertThat(output.toString().trim(), is("0,1"));

            verify(record, never()).setFieldValue(eq("field1"), any(byte[].class));
        }
    }

    @Test 
    public void testCompoundDataMaskingDoesNotCrash() throws Exception {
        FormatProcessor processor = mock(AbstractFormatProcessor.class);

        String ssn = JDefaultIdentity.ssn(true);
        String testData = String.format("{\"field0\":\"%s\", \"field1\":\"%s\"}",
                JDefaultBusiness.creditCardNumber(JDefaultBusiness.creditCardType()),
                ssn);
        try (
                InputStream input = new ByteArrayInputStream(testData.getBytes());
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                PrintStream print = new PrintStream(output)
        ) {
            Map<String, DataMaskingTarget> identifiedFields = new HashMap<>();
            identifiedFields.put("field0", new DataMaskingTarget(ProviderType.CREDIT_CARD, "field0"));
            identifiedFields.put("field1", new DataMaskingTarget(ProviderType.NATIONAL_ID, "field1"));
            
            Set<String> alreadyMasked = new HashSet<>();
            alreadyMasked.add("field1");

            processor.maskStream(input, print, new MaskingProviderFactory(new ConfigurationManager(new DefaultMaskingConfiguration()), Collections.emptyMap()), new DataMaskingOptions(
                    DataTypeFormat.CSV,
                    DataTypeFormat.CSV,
                    identifiedFields,
                    false,
                    Collections.emptyMap(),
                    new CSVDatasetOptions(false, ',', '"', false)
            ), alreadyMasked, Collections.emptyMap());
        }
    }


    
    @Test
    public void testCompoundOperandNotInMaskedList() {

        assertTimeout(ofMillis(2000L), () -> {
            AbstractFormatProcessor formatProcessor = new AbstractFormatProcessor() {
                @Override
                public boolean supportsStreams() {
                    return true;
                }

                @Override
                protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions, int firstN) throws IOException {
                    return null;
                }
            };

            CSVDatasetOptions csvOptions = new CSVDatasetOptions(false, ',', '"', false);

            Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
            toBeMasked.put("date", new DataMaskingTarget(ProviderType.DATETIME, "date"));

            Map<String, FieldRelationship> relationships = new HashMap<>();
            relationships.put("date", new FieldRelationship(ValueClass.DATE, RelationshipType.KEY, "date", Arrays.asList(new RelationshipOperand("userid"))));

            MaskingProviderFactory mpf = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());
            DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                    DataTypeFormat.CSV,
                    DataTypeFormat.CSV,
                    toBeMasked,
                    false,
                    relationships,
                    csvOptions
            );

            Map<String, Integer> fieldNames = new HashMap<>();
            fieldNames.put("userid", 0);
            fieldNames.put("date", 1);

            String originalDate = "28-11-2017";


            String user1_date = null;

            for(int i = 0; i < 100; i++) {
                //maskRecord returns a reference to the same object of the first argument so we need to create a new record each time
                Record record = new CSVRecord(new String[]{"user1", originalDate}, fieldNames, csvOptions , false);
                Record masked = formatProcessor.maskRecord(record, mpf, new HashSet<>(), dataMaskingOptions);
                String maskedDate = new String(masked.getFieldValue("date"));

                assertNotEquals(maskedDate, originalDate);

                if (user1_date != null) {
                    assertEquals(user1_date, maskedDate);
                }

                user1_date = maskedDate;
            }


            String user2_date = null;

            for(int i = 0; i < 100; i++) {
                Record record = new CSVRecord(new String[]{"user2", originalDate}, fieldNames, csvOptions, false);
                Record masked = formatProcessor.maskRecord(record, mpf, new HashSet<>(), dataMaskingOptions);
                String maskedDate = new String(masked.getFieldValue("date"));

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

        AbstractFormatProcessor formatProcessor = new AbstractFormatProcessor() {
            @Override
            public boolean supportsStreams() {
                return false;
            }

            @Override
            protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions dataOptions, int firstN) throws IOException {
                return null;
            }
        };

        Map<String, DataMaskingTarget> toBeMasked = new HashMap<>();
        toBeMasked.put("/operand", new DataMaskingTarget(ProviderType.DATETIME, "/date"));
        toBeMasked.put("/date", new DataMaskingTarget(ProviderType.DATETIME, "/date"));

        Map<String, FieldRelationship> relationships = new HashMap<>();
        relationships.put("/date",
                new FieldRelationship(ValueClass.DATE, RelationshipType.DISTANCE, "" +
                        "/date", Arrays.asList(new RelationshipOperand("/operand"))));

        MaskingProviderFactory mpf = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(
                DataTypeFormat.JSON,
                DataTypeFormat.JSON,
                toBeMasked,
                false,
                relationships,
                null
        );


        String originalDate = "28-11-2017";

        Record record = new JSONRecord(new ObjectMapper().readTree("{\"operand\": null, \"date\": \"" + originalDate +"\"}"));

        //maskRecord returns a reference to the same object of the first argument so we need to create a new record each time
        Record masked = formatProcessor.maskRecord(record, mpf, new HashSet<>(), dataMaskingOptions);
        String maskedDate = new String(masked.getFieldValue("/date"));

        assertNotEquals(maskedDate, originalDate);

    }

}