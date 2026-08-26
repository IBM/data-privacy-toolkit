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
package com.ibm.research.drl.dpt.datasets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchema;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchemaField;

/** An in-memory tabular dataset used by the IPV privacy-preservation algorithms. */
public class IPVDataset implements Iterable<List<String>> {
    private static final Logger logger = LogManager.getLogger(IPVDataset.class);

    /** Dataset identifier. */
    protected String id;

    /** Row data. */
    protected final List<List<String>> values;
    /** Schema describing the columns. */
    protected IPVSchema schema;
    private final boolean hasSchema;

    /**
     * Returns the underlying row data.
     *
     * @return list of rows
     */
    public List<List<String>> getValues() {
        return this.values;
    }

    /**
     * Constructs an empty dataset with the given number of columns.
     *
     * @param numberOfColumns number of columns
     * @deprecated use {@link #IPVDataset(List, IPVSchema, boolean)} instead
     */
    @Deprecated
    public IPVDataset(int numberOfColumns) {
        this(new ArrayList<>(), generateSchemaWithoutColumnNames(numberOfColumns), false);
    }

    /**
     * Constructs an IPVDataset with the given values and schema.
     *
     * @param values    the row data
     * @param schema    the column schema (may be {@code null})
     * @param hasSchema whether the dataset has a named-column schema
     */
    public IPVDataset(List<List<String>> values, IPVSchema schema, boolean hasSchema) {
        this.values = values;
        this.schema = schema;
        this.hasSchema = hasSchema;
    }

    /**
     * Returns the number of columns in this dataset.
     *
     * @return column count, or -1 if unknown
     */
    public int getNumberOfColumns() {
        if (schema != null) return schema.getFields().size();
        if (values.isEmpty()) return -1;

        return values.get(0).size();
    }

    /**
     * Appends a single row to this dataset.
     *
     * @param row the row to add
     */
    public void addRow(List<String> row) {
        this.values.add(row);
    }

    /**
     * Appends multiple rows to this dataset.
     *
     * @param v the rows to add
     */
    public void append(List<List<String>> v) {
        this.values.addAll(v);
    }

    /**
     * Sets the value at the given row and column.
     *
     * @param row    the row index
     * @param column the column index
     * @param value  the value to set
     */
    public void set(int row, int column, String value) {
        this.values.get(row).set(column, value);
    }

    /**
     * Returns the number of rows in this dataset.
     *
     * @return row count
     */
    public int getNumberOfRows() {
        return values.size();
    }

    /**
     * Returns the value at the given row and column.
     *
     * @param row    the row index
     * @param column the column index
     * @return the cell value
     */
    public String get(int row, int column) {
        return values.get(row).get(column);
    }

    /**
     * Returns the row at the given index.
     *
     * @param row the row index
     * @return the row as a list of strings
     */
    public List<String> getRow(int row) {
        return values.get(row);
    }

    /**
     * Returns the hash code of the value at the given position.
     *
     * @param row    the row index
     * @param column the column index
     * @return hash code of the cell value
     */
    public int hash(int row, int column) {
        return get(row, column).hashCode();
    }

    /**
     * Returns the schema of this dataset.
     *
     * @return the schema, or {@code null} if not set
     */
    public IPVSchema getSchema() {
        return schema;
    }

    /**
     * Returns whether this dataset has named columns.
     *
     * @return true if column names are available
     */
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

        if (hasSchema && schema != null && !schema.getFields().isEmpty()) {
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

    /**
     * Builds a delimited header string from the schema.
     *
     * @param schema         the schema to read field names from
     * @param fieldDelimiter the delimiter to use between field names
     * @return the header string
     */
    public String buildHeader(IPVSchema schema, Character fieldDelimiter) {
        final List<? extends IPVSchemaField> fields = schema.getFields();

        StringBuilder builder = new StringBuilder(fields.get(0).getName());
        for (int i = 1; i < fields.size(); ++i) {
            builder.append(fieldDelimiter);
            builder.append(fields.get(i).getName());
        }

        return builder.toString();
    }

    /**
     * Loads an {@link IPVDataset} from an {@link InputStream}.
     *
     * @param inputStream    the input stream
     * @param skipFirst      whether to skip the first (header) row
     * @param fieldDelimiter the field delimiter character
     * @param quoteCharacter the quote character
     * @param trimFields     whether to trim whitespace from field values
     * @return the loaded dataset
     * @throws IOException if an I/O error occurs
     */
    public static IPVDataset load(InputStream inputStream, boolean skipFirst, Character fieldDelimiter, Character quoteCharacter, boolean trimFields) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return load(reader, skipFirst, fieldDelimiter, quoteCharacter, trimFields);
        }
    }

    /**
     * Loads an {@link IPVDataset} from a {@link Reader}.
     *
     * @param reader         the reader
     * @param hasHeader      whether the first row is a header row
     * @param fieldDelimiter the field delimiter character
     * @param quoteCharacter the quote character
     * @param trimFields     whether to trim whitespace from field values
     * @return the loaded dataset
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Generates a schema with auto-generated column names ({@code "Column 0"}, {@code "Column 1"}, …).
     *
     * @param numberOfFields number of columns
     * @return the generated schema
     */
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
        return values.iterator();
    }

    /**
     * Writes this dataset as CSV to the given appendable.
     *
     * @param options the CSV formatting options
     * @param writer  the output appendable
     */
    public void toCSV(CSVDatasetOptions options, Appendable writer) {
        Builder formatBuilder = CSVFormat.DEFAULT.builder()
                .setRecordSeparator('\n')
                .setDelimiter(options.getFieldDelimiter())
                .setQuote(options.getQuoteChar())
                .setTrim(options.isTrimFields())
                .setHeader().setSkipHeaderRecord(!options.isHasHeader());

        if (options.isHasHeader()) {
            formatBuilder.setHeader(schema.getFields().stream().map(IPVSchemaField::getName).toArray(String[]::new));
        }

        

        try (CSVPrinter printer = new CSVPrinter(writer, formatBuilder.build())) {
            printer.printRecords(this);
        } catch (IOException e) {
            logger.error("Error creating writer", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes this dataset as JSON to the given writer.
     *
     * @param datasetOptions the JSON formatting options
     * @param writer         the output writer
     */
    public void toJSON(JSONDatasetOptions datasetOptions, Writer writer) {
        try {
            IPVDatasetJSONSerializer.serialize(this, datasetOptions, writer);
        } catch (IOException e) {
            logger.error("Error creating writer", e);

            throw new RuntimeException(e);
        }
    }
}
