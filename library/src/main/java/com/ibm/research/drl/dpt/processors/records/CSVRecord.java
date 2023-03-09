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
package com.ibm.research.drl.dpt.processors.records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;

import java.io.IOException;
import java.util.Map;

public final class CSVRecord extends TabularRecord {
    private final static CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

    private final CSVDatasetOptions csvOptions;

    public CSVRecord(String[] data, Map<String, Integer> fieldNames, CSVDatasetOptions csvOptions, boolean isHeader) {
        this.data = data;
        this.fieldNames = fieldNames;
        this.csvOptions = csvOptions;
        this.isHeader = isHeader;
    }

    @Override
    public void setFieldValue(String fieldReference, byte[] value) {
        super.setFieldValue(fieldReference, (value == null ? "".getBytes() : value));
    }

    @Override
    protected String formatRecord() {
        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(csvOptions.getFieldDelimiter())
                .withQuoteChar(csvOptions.getQuoteChar()).withLineSeparator("");

        try {
            return mapper.writer(schema).writeValueAsString(data);
        } catch (JsonProcessingException ignore) {
        }

        throw new RuntimeException("unreachable");
    }

    public static Record fromString(String input, DatasetOptions datasetOptions, Map<String, Integer> fieldNames, boolean isHeader) throws IOException {
        CSVDatasetOptions csvOptions = (CSVDatasetOptions) datasetOptions;
        CsvSchema schema = CsvSchema.emptySchema().withColumnSeparator(csvOptions.getFieldDelimiter()).withQuoteChar(csvOptions.getQuoteChar());

        CsvMapper mapper = csvOptions.isTrimFields() ? CSVRecord.mapper.enable(CsvParser.Feature.TRIM_SPACES) : CSVRecord.mapper;
        MappingIterator<String[]> reader = mapper.readerFor(String[].class).with(schema).readValues(input);
        return new CSVRecord(reader.next(), fieldNames, csvOptions, isHeader);
    }
}
