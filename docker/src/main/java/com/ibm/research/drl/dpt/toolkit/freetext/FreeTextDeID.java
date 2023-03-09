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
package com.ibm.research.drl.dpt.toolkit.freetext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.nlp.ComplexFreeTextAnnotator;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.processors.FreeTextFormatProcessor;
import com.ibm.research.drl.dpt.processors.FreeTextFormatProcessorFactory;
import com.ibm.research.drl.dpt.providers.masking.FreeTextMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.toolkit.dataset.GenericDatasetOptions;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class FreeTextDeID extends TaskToExecute {
    private static final Logger logger = LogManager.getLogger(FreeTextDeID.class);

    private final FreeTextDeIDOptions taskOptions;
    private final FreeTextFormatProcessor processor;
    private final FreeTextMaskingProvider maskingProvider;
    private final ComplexFreeTextAnnotator annotator;

    @JsonCreator
    public FreeTextDeID(
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
            @JsonProperty("taskOptions") FreeTextDeIDOptions taskOptions) {
        super(task, extension, inputFormat, inputOptions, inputFormat, inputOptions);

        this.taskOptions = taskOptions;
        this.processor = FreeTextFormatProcessorFactory.getProcessor(inputFormat);
        this.annotator = new ComplexFreeTextAnnotator(taskOptions.getNlpAnnotator());
        this.maskingProvider = new FreeTextMaskingProvider(
                new MaskingProviderFactory(ConfigurationManager.load(taskOptions.getMaskingConfiguration().getMaskingProvidersConfig()), taskOptions.getMaskingConfiguration().getToBeMasked()),
                annotator,
                taskOptions.getMaskingConfiguration().getToBeMasked());
    }

    @Override
    public TaskOptions getTaskOptions() {
        return taskOptions;
    }

    @Override
    public void processFile(InputStream input, OutputStream output) throws MisconfigurationException {
        if (taskOptions.getAnnotate()) {
            try (PrintStream printStream = new PrintStream(output)) {
                processor.applyFunction(input, printStream, annotator, null, entity -> "[[" + getEntityType(entity) + "]" + entity.getText() + "]");
            }
        } else {
            processor.maskDocument(input, output, maskingProvider);
        }
    }

    private String getEntityType(IdentifiedEntity entity) {
        return entity.getType().iterator().next().getType();
    }
}
