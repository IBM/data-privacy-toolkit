/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.datasets;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.schema.IPVSchema;
import com.ibm.research.drl.schema.IPVSchemaField;
import com.ibm.research.drl.schema.IPVSchemaFieldType;
import com.ibm.research.drl.schema.impl.SimpleSchema;
import com.ibm.research.drl.schema.impl.SimpleSchemaField;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class IPVDataset implements Iterable<List<String>> {
    private static final Logger logger = LogManager.getLogger(IPVDataset.class);

    protected String id;

    protected final List<List<String>> values;
    protected IPVSchema schema;
    private final boolean hasSchema;

    public List<List<String>> getValues() {
        return this.values;
    }

    @Deprecated
    public IPVDataset(int numberOfColumns) {
        this(new ArrayList<>(), generateSchemaWithoutColumnNames(numberOfColumns), false);
    }

    public IPVDataset(List<List<String>> values, IPVSchema schema, boolean hasSchema) {
        this.values = values;
        this.schema = schema;
        this.hasSchema = hasSchema;
    }

    public int getNumberOfColumns() {
        if (null != schema) return schema.getFields().size();
        if (0 == values.size()) return -1;

        return values.get(0).size();
    }

    public void addRow(List<String> row) {
        this.values.add(row);
    }

    public void append(List<List<String>> v) {
        this.values.addAll(v);
    }

    public void set(int row, int column, String value) {
        this.values.get(row).set(column, value);
    }

    public int getNumberOfRows() {
        return values.size();
    }

    public String get(int row, int column) {
        return values.get(row).get(column);
    }

    public List<String> getRow(int row) {
        return values.get(row);
    }

    public int hash(int row, int column) {
        return get(row, column).hashCode();
    }

    public IPVSchema getSchema() {
        return schema;
    }

    public boolean hasColumnNames() {
        return hasSchema;
    }

    private String sanitize(String stringValue) {
        if (stringValue.contains("\"")) stringValue = stringValue.replace("\"", "\"\"");
        if (stringValue.contains(",") || stringValue.contains("\n")) stringValue = "\"" + stringValue + "\"";

        return stringValue;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        if (hasSchema && null != schema && schema.getFields().size() > 0) {
            // add header -> schema
            builder.append(buildHeader(schema, ','));
            builder.append('\n');
        }

        for (final List<String> record : values) {
            builder.append(record.get(0));
            for (int i = 1; i < record.size(); ++i) {
                builder.append(',');
                builder.append(sanitize(record.get(i)));
            }
            builder.append('\n');
        }

        return builder.toString();
    }

    public String buildHeader(IPVSchema schema, Character fieldDelimiter) {
        final List<? extends IPVSchemaField> fields = schema.getFields();

        StringBuilder builder = new StringBuilder(fields.get(0).getName());
        for (int i = 1; i < fields.size(); ++i) {
            builder.append(fieldDelimiter);
            builder.append(fields.get(i).getName());
        }

        return builder.toString();
    }

    public static IPVDataset load(InputStream inputStream, boolean skipFirst, Character fieldDelimiter, Character quoteCharacter, boolean trimFields) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return load(reader, skipFirst, fieldDelimiter, quoteCharacter, trimFields);
        }
    }

    public static IPVDataset load(Reader reader, final boolean hasHeader, Character fieldDelimiter, Character quoteCharacter, boolean trimFields) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(fieldDelimiter).withQuoteChar(quoteCharacter).withSkipFirstDataRow(false);
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        if (trimFields) {
            mapper.enable(CsvParser.Feature.TRIM_SPACES);
        }

        MappingIterator<String[]> it = mapper.readerFor(String[].class).with(schema).readValues(reader);

        List<String> header = null;
        List<List<String>> values = new ArrayList<>();
        int numberOfFields = -1;
        boolean skipFirst = hasHeader;

        while (it.hasNext()) {
            List<String> csvRecord = Arrays.asList(it.next());

            if (skipFirst) {
                header = csvRecord;
                skipFirst = false;
                continue;
            }
            if (csvRecord.isEmpty()) {
                continue;
            }
            if (numberOfFields == -1) {
                numberOfFields = csvRecord.size();
            }
            if (csvRecord.size() == numberOfFields) {
                values.add(csvRecord);
            } else {
                logger.warn("Record has a different size than what expected: {} instead of {}", csvRecord.size(), numberOfFields);
            }
        }

        return new IPVDataset(
                values,
                hasHeader ? generateSchemaWithColumnNames(header) : generateSchemaWithoutColumnNames(numberOfFields),
                null != header
        );
    }

    public static IPVSchema generateSchemaWithoutColumnNames(int numberOfFields) {
        logger.debug("Generating schema without column name knowledge");

        final List<SimpleSchemaField> fields = new ArrayList<>(numberOfFields);

        for (int i = 0; i < numberOfFields; ++i) {
            fields.add(new SimpleSchemaField("Column " + i, IPVSchemaFieldType.STRING));
        }

        return new SimpleSchema(generateRandomSchemaID(), fields);
    }

    private static IPVSchema generateSchemaWithColumnNames(Iterable<String> header) {
        logger.debug("Generating schema with column name knowledge");

        final List<SimpleSchemaField> fields = new ArrayList<>();

        for (String entry : header) {
            fields.add(new SimpleSchemaField(entry, IPVSchemaFieldType.STRING));
        }

        return new SimpleSchema(generateRandomSchemaID(), fields);
    }

    private static String generateRandomSchemaID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Iterator<List<String>> iterator() {
        return new Iterator<>() {
            private volatile int index = 0;

            @Override
            public synchronized boolean hasNext() {
                return getNumberOfRows() > index;
            }

            @Override
            public synchronized List<String> next() {
                if (!hasNext()) throw new NoSuchElementException();

                return getRow(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void toCSV(CSVDatasetOptions options, Appendable writer) {
        CSVFormat format = CSVFormat.DEFAULT
                .withRecordSeparator('\n')
                .withDelimiter(options.getFieldDelimiter())
                .withQuote(options.getQuoteChar())
                .withTrim(options.isTrimFields())
                .withHeader().withSkipHeaderRecord(!options.isHasHeader());

        if (options.isHasHeader()) {
            format = format.withHeader(schema.getFields().stream().map(IPVSchemaField::getName).toArray(String[]::new));
        }

        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            printer.printRecords(this);
        } catch (IOException e) {
            logger.error("Error creating writer", e);
            throw new RuntimeException(e);
        }
    }

    public void toJSON(JSONDatasetOptions datasetOptions, Writer writer) {
        try {
            new IPVDatasetJSONSerializer().serialize(this, datasetOptions, writer);
        } catch (IOException e) {
            logger.error("Error creating writer", e);

            throw new RuntimeException(e);
        }
    }
}
