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
import com.ibm.research.drl.dpt.IPVAlgorithm;
import com.ibm.research.drl.dpt.anonymization.VerificationUtils;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;
import com.ibm.research.drl.dpt.processors.records.CSVRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.vulnerability.IPVVulnerability;
import com.ibm.research.drl.dpt.vulnerability.WithRowExtractor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CSVFormatProcessor extends FormatProcessor {
    private static final Logger logger = LogManager.getLogger(CSVFormatProcessor.class);

    private static final CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

    @Override
    protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions datasetOptions, int firstN) {
        if (!(datasetOptions instanceof CSVDatasetOptions))
            throw new IllegalArgumentException("Dataset masking options not consistent with the format processor: CSV");

        final CSVDatasetOptions csvOptions = (CSVDatasetOptions) datasetOptions;

        final MappingIterator<String[]> reader;
        try {
            reader = mapper.readerFor(String[].class)
                    .with(
                            CsvSchema.emptySchema()
                                    .withColumnSeparator(csvOptions.getFieldDelimiter())
                                    .withQuoteChar(csvOptions.getQuoteChar())
                    )
                    .readValues(dataset);
        } catch (IOException e) {
            logger.error("Unable to read values from input stream", e);

            throw new RuntimeException(e);
        }

        return () -> new Iterator<>() {
            int seenSoFar;
            int readSoFar;

            Map<String, Integer> fields = null;

            CSVRecord nextRecord = null;

            @Override
            public boolean hasNext() {
                while (null == nextRecord && reader.hasNext()) {
                    if (firstN > 0 && readSoFar >= firstN) {
                        return false;
                    }

                    String[] values = processValues(reader.next(), csvOptions.isTrimFields());

                    seenSoFar += 1;

                    if (0 == values.length) {
                        logger.debug("Skipping record {} because it contains no fields (corrupt row?)", seenSoFar);
                        continue;
                    }
                    if (fields == null && 1 == values.length && values[0].trim().isEmpty()) {
                        logger.debug("Skipping record {} because it contains only one empty field (corrupt row?)", seenSoFar);
                        continue;
                    }
                    if (fields != null && fields.size() != values.length) {
                        logger.debug("Skipping record {} because it contains {} field(s), but schema contains {} header(s) (corrupt row?)", seenSoFar, values.length, fields.size());
                        continue;
                    }

                    readSoFar += 1;

                    if (null == fields) { // is first read?
                        if (csvOptions.isHasHeader()) {
                            fields = buildFieldsMap(values);
                            readSoFar -= 1; // header does not count as record
                        } else {
                            fields = buildFieldsMap(generateColumnNames(values.length));
                        }
                        nextRecord = new CSVRecord(values, fields, csvOptions, csvOptions.isHasHeader());
                    } else {
                        nextRecord = new CSVRecord(values, fields, csvOptions, false);
                    }
                }

                return nextRecord != null;
            }

            private String[] processValues(String[] values, boolean trimFields) {
                if (trimFields) {
                    for (int i = 0; i < values.length; ++i) {
                        values[i] = values[i].trim();
                    }
                }
                return values;
            }

            @Override
            public Record next() {
                if (!hasNext()) throw new NoSuchElementException();

                CSVRecord currentRecord = this.nextRecord;
                this.nextRecord = null;

                return currentRecord;
            }
        };
    }

    private Map<String, Integer> buildFieldsMap(String[] fieldNames) {
        Map<String, Integer> fieldMap = new HashMap<>(fieldNames.length);

        for (String name : fieldNames) {
            fieldMap.put(name, fieldMap.size());
        }

        return fieldMap;
    }

    public static String[] generateColumnNames(int length) {
        String[] names = new String[length];

        for (int i = 0; i < length; ++i) {
            names[i] = "Column " + i;
        }

        return names;
    }

    @Override
    public boolean supportsStreams() {
        return true;
    }

    @Override
    public Map<IPVVulnerability, List<Integer>> identifyVulnerabilitiesStream(InputStream input, IPVAlgorithm algorithm, DataTypeFormat inputFormatType, DatasetOptions datasetOptions, boolean isFullReport, int kValue) throws IOException {
        CSVDatasetOptions csvDatasetOptions = (CSVDatasetOptions) datasetOptions;
        IPVDataset dataset = IPVDataset.load(input, csvDatasetOptions.isHasHeader(), csvDatasetOptions.getFieldDelimiter(), csvDatasetOptions.getQuoteChar(), csvDatasetOptions.isTrimFields());

        Collection<IPVVulnerability> vulnerabilities = algorithm.apply(dataset);

        Map<IPVVulnerability, List<Integer>> results = new HashMap<>();

        if (isFullReport && algorithm instanceof WithRowExtractor) {
            for (IPVVulnerability vulnerability : vulnerabilities) {
                Integer[] rowIDs = extractRowIds(vulnerability.getItemSet(), kValue, csvDatasetOptions.isHasHeader(), dataset);
                results.put(vulnerability, Arrays.asList(rowIDs));
            }
        } else {
            for (IPVVulnerability vulnerability : vulnerabilities) {
                results.put(vulnerability, null);
            }
        }

        return results;
    }

    private static Set<Integer> joinRowSets(Map<String, Set<Integer>> valueMap, int k) {
        Set<Integer> jointRowSets = new LinkedHashSet<>();

        for (Set<Integer> value : valueMap.values()) {
            if (value.size() < k) {
                jointRowSets.addAll(value);
            }
        }

        return jointRowSets;
    }

    private static Integer[] extractRowIds(ItemSet itemSet, int k, boolean hasHeader, IPVDataset dataset) {
        Integer[] rows = joinRowSets(VerificationUtils.buildValueMap(itemSet, dataset), k).toArray(new Integer[0]);
        Arrays.sort(rows);

        if (hasHeader) {
            for (int i = 0; i < rows.length; i++) {
                rows[i]++;
            }
        }

        return rows;
    }
}
