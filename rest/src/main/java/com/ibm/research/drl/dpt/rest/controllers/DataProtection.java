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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.configuration.*;
import com.ibm.research.drl.dpt.processors.CSVFormatProcessor;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.rest.exceptions.InvalidRequestException;
import com.ibm.research.drl.dpt.rest.models.MaskingProviderDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

@RestController
@RequestMapping(value = "/api/feature/mask")
public class DataProtection {
    private static final Logger logger = LogManager.getLogger(DataProtection.class);
    private final ObjectMapper mapper;
    private final CSVFormatProcessor csvProcessor;

    private final CsvMapper csvMapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);
    private final CsvSchema schema = CsvSchema.emptySchema();

    public DataProtection(@Autowired ObjectMapper mapper, @Autowired CSVFormatProcessor csvProcessor) {
        this.mapper = mapper;
        this.csvProcessor = csvProcessor;
    }

    @PostMapping(value = "/{hasHeader}/{useCompound}", produces = "text/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String maskFileWithSpecification(
            @PathVariable("hasHeader") boolean hasHeader,
            @PathVariable("useCompound") boolean useCompound,
            @RequestParam("configuration") String confS,
            @RequestParam("fields") String fieldsS,
            @RequestParam("dataset") String inputDataset) throws IOException {

        if (null == inputDataset) {
            throw new InvalidRequestException("Missing input dataset");
        }

        if (null == fieldsS) {
            throw new InvalidRequestException("Missing parameter field");
        }
        Map<String, MaskingProviderDescription> fieldDescriptions = mapper.readValue(fieldsS, new TypeReference<>() {});

        logger.info("Masking providers before: " + fieldDescriptions);

        final Map<String, DataMaskingTarget> targets = new HashMap<>();

        fieldDescriptions.forEach((fieldName, value) -> {
            final String providerTypeName = value.getName();
            final ProviderType providerType;
            if (providerTypeName.isEmpty()) {
                providerType = null;
            } else {
                providerType = ProviderType.valueOf(providerTypeName);
            }

            targets.put(fieldName, new DataMaskingTarget(providerType, fieldName));
        });

        logger.info("Masking providers: {}", targets);

        final DefaultMaskingConfiguration configuration = null == confS ? new DefaultMaskingConfiguration() : mapper.readValue(confS, DefaultMaskingConfiguration.class);

        final ConfigurationManager configurationManager = new ConfigurationManager(configuration);

        if (userRequestedDifferentialPrivacy(targets)) {
            updateConfiguration(configurationManager, inputDataset, hasHeader, targets);
        }

        MaskingProviderFactory factory = new MaskingProviderFactory(configurationManager, targets);
        try (Reader reader = new StringReader(inputDataset);
             StringWriter writer = new StringWriter(inputDataset.length());
             MappingIterator<String[]> it = csvMapper.readerFor(String[].class).with(schema).readValues(reader)) {
            SequenceWriter seqWriter = csvMapper.writerFor(String[].class).with(schema).writeValues(writer);

            String[] headers = null;

            while (it.hasNext()) {
                String[] record = it.next();

                if (null == headers) {
                    if (hasHeader) {
                        headers = record;
                        seqWriter.write(headers);
                        continue;
                    } else {
                        headers = new String[record.length];
                        for (int i = 0; i < record.length; ++i) {
                            headers[i] = "Column " + i;
                        }
                    }
                }

                for (int i = 0; i < headers.length; ++i) {
                    final String header = headers[i];
                    if (targets.containsKey(header)) {
                        MaskingProvider provider = factory.get(header);

                        record[i] = provider.mask(record[i]);
                    }
                }

                seqWriter.write(record);
            }

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidRequestException(e.getMessage());
        }
    }

    private boolean userRequestedDifferentialPrivacy(Map<String, DataMaskingTarget> targets) {
        return targets.entrySet().parallelStream().
                map(Map.Entry::getValue).
                map(DataMaskingTarget::getProviderType).
                anyMatch( type -> Objects.equals(type, ProviderType.DIFFERENTIAL_PRIVACY));
    }

    private void updateConfiguration(ConfigurationManager configuration, String inputDataset, boolean hasHeader, Map<String, DataMaskingTarget> fieldsToMask) throws IOException {
        try (Reader reader = new StringReader(inputDataset)) {
            String[] headers = null;
            final MappingIterator<String[]> it = csvMapper.readerFor(String[].class).with(schema).readValues(reader);

            double[] min = null;
            double[] max = null;

            while (it.hasNext()) {
                String[] record = it.next();
                if (null == headers) {
                    min = new double[record.length];
                    max = new double[record.length];

                    for (int i = 0; i < min.length; ++i) {
                        min[i] = Double.POSITIVE_INFINITY;
                        max[i] = Double.NEGATIVE_INFINITY;
                    }

                    if (hasHeader) {
                        headers = record;
                        continue;
                    } else {
                        headers = new String[record.length];
                        for (int i = 0; i < record.length; ++i) {
                            headers[i] = "Column " + i;
                        }
                    }
                }

                for (int i = 0; i < headers.length; ++i) {
                    String header = headers[i];
                    DataMaskingTarget type = fieldsToMask.get(header);
                    if (null != type && type.getProviderType() == ProviderType.DIFFERENTIAL_PRIVACY) {
                        double value = Double.parseDouble(record[i]);
                        min[i] = Math.min(min[i], value);
                        max[i] = Math.max(max[i], value);
                    }
                }
            }

            List<String> newHeaders = Arrays.asList(headers != null ? headers : new String[0]);

            double[] finalMax = max;
            double[] finalMin = min;

//            fieldsToMask.values().stream().
//                    filter(entry -> Objects.equals(entry.getProviderType(), ProviderType.DIFFERENTIAL_PRIVACY)).
//                    forEach(target -> {
//                String fieldName = target.getTargetPath();
//                int i = newHeaders.indexOf(fieldName);
//
//                Map<String, ConfigurationOption> fieldSpecificOptions = new HashMap<>();
//
//                assert finalMin != null;
//
//                double diameter = finalMax[i] - finalMin[i];
//                fieldSpecificOptions.put("differentialPrivacy.parameter.diameter", new ConfigurationOption(diameter, "", ""));
//                fieldSpecificOptions.put("differentialPrivacy.range.max", new ConfigurationOption(finalMax[i], "", ""));
//                fieldSpecificOptions.put("differentialPrivacy.range.min", new ConfigurationOption(finalMin[i], "", ""));
//
//                FieldMaskingConfiguration fieldMaskingConfiguration = new FieldMaskingConfiguration(configuration.getDefaultConfiguration(), fieldSpecificOptions);
//
//                configuration.setFieldConfiguration(fieldName, fieldMaskingConfiguration);
//            });

        }
    }

}
