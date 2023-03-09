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
package com.ibm.research.drl.dpt.toolkit.masking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.processors.FormatProcessor;
import com.ibm.research.drl.dpt.processors.FormatProcessorFactory;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.toolkit.dataset.GenericDatasetOptions;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.util.*;

public class MaskingTask extends TaskToExecute {
    private static final Logger logger = LogManager.getLogger(MaskingTask.class);

    private final MaskingOptions taskOptions;
    private final MaskingProviderFactory maskingProviderFactory;
    private final DataMaskingOptions dataMaskingOptions;

    @JsonCreator
    public MaskingTask(
            @JsonProperty("task") String task,
            @JsonProperty("extension") String extension,
            @JsonProperty("inputFormat") DataTypeFormat inputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "inputFormat",
                    defaultImpl = GenericDatasetOptions.class
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV"),
                    @JsonSubTypes.Type(value = JSONDatasetOptions.class, name = "JSON")
            })
            @JsonProperty("inputOptions") DatasetOptions inputOptions,
            @JsonProperty("taskOptions") MaskingOptions dataMaskingOptions,
            @JsonProperty("outputFormat") DataTypeFormat outputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "outputFormat",
                    defaultImpl = GenericDatasetOptions.class
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV"),
                    @JsonSubTypes.Type(value = JSONDatasetOptions.class, name = "JSON")
            })
            @JsonProperty("outputOptions") DatasetOptions outputOptions
     ) {
        super(task, extension, inputFormat, inputOptions, outputFormat, outputOptions);
        this.taskOptions = dataMaskingOptions;

        this.dataMaskingOptions = new DataMaskingOptions(
                getInputFormat(),
                getOutputFormat(),
                taskOptions.getToBeMasked(),
                false,
                taskOptions.getPredefinedRelationships(),
                getInputOptions()
        );

        this.maskingProviderFactory = buildMaskingProviderFactory(
                taskOptions.getToBeMasked(),
                taskOptions.getMaskingProvidersConfig()
        );
    }

    private MaskingProviderFactory buildMaskingProviderFactory(Map<String, DataMaskingTarget> toBeMasked, JsonNode maskingProvidersConfig) {
        final ConfigurationManager configurationManager = ConfigurationManager.load(maskingProvidersConfig);
        return new MaskingProviderFactory(configurationManager, toBeMasked);
    }

    @Override
    public void processFile(InputStream input, OutputStream output) throws IOException, MisconfigurationException {
        maskDataset(input, output);
    }

    @Override
    public MaskingOptions getTaskOptions() {
        return this.taskOptions;
    }

    private static Map<ProviderType, Class<? extends MaskingProvider>> loadUserDefinedMaskingProviders(String filename) throws IOException {
        final Map<ProviderType, Class<? extends MaskingProvider>> userDefinedMaskingProviders = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                final String[] parts = line.split(":");
                final ProviderType providerType = ProviderType.valueOf(parts[0].trim());

                final Class<? extends MaskingProvider> maskingProvider = (Class<? extends MaskingProvider>) Class.forName(parts[1].trim());

                userDefinedMaskingProviders.put(providerType, maskingProvider);
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(filename + " not found", e);
        } catch (IOException e) {
            throw new IOException("Error reading user defined masking providers from " + filename, e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found " + e.getMessage(), e);
        }
        return userDefinedMaskingProviders;
    }

    private void maskDataset(InputStream dataset, OutputStream output) throws IOException {
        final Set<String> alreadyMaskedFields = new HashSet<>();

        final Map<ProviderType, Class<? extends MaskingProvider>> userDefinedTypes;
        if (null != this.getTaskOptions().getMaskingProviders() && !this.getTaskOptions().getMaskingProviders().isEmpty()) {
            userDefinedTypes = loadUserDefinedMaskingProviders(this.getTaskOptions().getMaskingProviders());
        } else {
            userDefinedTypes = Collections.emptyMap();
        }

        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(dataMaskingOptions.getInputFormat());
        formatProcessor.maskStream(dataset, output, this.maskingProviderFactory, dataMaskingOptions, alreadyMaskedFields, userDefinedTypes);
    }

}
