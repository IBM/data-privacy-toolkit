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
package com.ibm.research.drl.dpt.rest.controllers;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.processors.CSVFormatProcessor;
import com.ibm.research.drl.dpt.processors.IdentificationReport;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.rest.exceptions.InvalidRequestException;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Identify {
    private static final Logger logger = LogManager.getLogger(Identify.class);

    private final CSVFormatProcessor csvFormatProcessor;

    private final static CsvMapper csvMapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

    public Identify(@Autowired CSVFormatProcessor csvFormatProcessor) {
        this.csvFormatProcessor = csvFormatProcessor;
    }

    @PostMapping(value = "/api/feature/identify/{hasColumnNames}", consumes = {MediaType.TEXT_PLAIN_VALUE, "text/csv"})
    public Map<String, ProviderType> identifyCSV(
            @PathVariable("hasColumnNames") boolean hasColumnNames,
            @RequestParam(value = "delimiter", defaultValue = ",") char delimiter,
            @RequestParam(value = "quoteChar", defaultValue = "\"") char quoteChar,
            @RequestParam(value = "sampleSize", defaultValue = "-1") Long sampleSize,
            @RequestBody String datasetContent) {
        logger.info("Executing identify on CSV dataset with hasColumnNames={} delimiter={} quoteChar={} sampleSize={}", hasColumnNames, delimiter, quoteChar, sampleSize);
        CSVDatasetOptions options = new CSVDatasetOptions(hasColumnNames, delimiter, quoteChar, false);
        try (InputStream input = new ByteArrayInputStream(limitContentLength(datasetContent, options, sampleSize).getBytes())) {
            IdentificationReport identificationReport = csvFormatProcessor.identifyTypesStream(
                    input,
                    DataTypeFormat.CSV,
                    options,
                    IdentifierFactory.defaultIdentifiers(),
                    -1
            );

            return identificationReport.getBestTypes().entrySet().stream().map(
                    e -> new Tuple<>(e.getKey(), ProviderType.valueOf(e.getValue().getTypeName()))
            ).collect(Collectors.toMap(
                    Tuple::getFirst,
                    Tuple::getSecond
            ));
        } catch (Exception e) {
            logger.error(e);
            throw new InvalidRequestException(e.getMessage());
        }
    }

    private String limitContentLength(String datasetContent, CSVDatasetOptions options, long sampleSize) {
        if (-1L == sampleSize) return datasetContent;
        if (options.isHasHeader()) sampleSize += 1L;

        try (StringWriter output = new StringWriter()) {
            CsvSchema schema = CsvSchema.emptySchema().withSkipFirstDataRow(false).withQuoteChar(options.getQuoteChar()).withColumnSeparator(options.getFieldDelimiter());
            try (
                    MappingIterator<String[]> reader = csvMapper.readerFor(String[].class).with(schema).with(CsvParser.Feature.WRAP_AS_ARRAY).readValues(datasetContent);
                    CSVPrinter printer = new CSVPrinter(output, CSVFormat.Builder.create().setDelimiter(options.getFieldDelimiter()).setQuote(options.getQuoteChar()).build())
                ) {

                for (int i = 0; i < sampleSize; ++i) {
                    if (reader.hasNext()) {
                        String[] record = reader.next();
                        printer.printRecords(Arrays.asList(record));
                    }
                }
            }

            return output.toString();
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
