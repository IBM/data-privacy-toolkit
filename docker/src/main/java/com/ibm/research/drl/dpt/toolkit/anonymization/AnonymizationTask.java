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
package com.ibm.research.drl.dpt.toolkit.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithm;
import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.kmap.KMap;
import com.ibm.research.drl.dpt.anonymization.kmap.KMapOptions;
import com.ibm.research.drl.dpt.anonymization.kmeans.KMeansAnonymization;
import com.ibm.research.drl.dpt.anonymization.kmeans.KMeansOptions;
import com.ibm.research.drl.dpt.anonymization.mondrian.Mondrian;
import com.ibm.research.drl.dpt.anonymization.mondrian.MondrianOptions;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.anonymization.sampling.Sampling;
import com.ibm.research.drl.dpt.anonymization.sampling.SamplingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.toolkit.dataset.JSONIPVDataset;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;

public class AnonymizationTask extends TaskToExecute {
    private final AnonymizationTaskOptions taskOptions;

    @JsonCreator
    public AnonymizationTask(
            @JsonProperty("task") String task,
            @JsonProperty("extension") String extension,
            @JsonProperty("inputFormat") DataTypeFormat inputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "inputFormat"
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV"),
                    @JsonSubTypes.Type(value = JSONDatasetOptions.class, name = "JSON")
            })
            @JsonProperty("inputOptions") DatasetOptions inputOptions,

            @JsonProperty("outputFormat") DataTypeFormat outputFormat,
            @JsonTypeInfo(
                    use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "outputFormat"
            )
            @JsonSubTypes({
                    @JsonSubTypes.Type(value = CSVDatasetOptions.class, name = "CSV"),
                    @JsonSubTypes.Type(value = JSONDatasetOptions.class, name = "JSON")
            })
            @JsonProperty("outputOptions") DatasetOptions outputOptions,
            @JsonProperty("taskOptions") AnonymizationTaskOptions anonymizationTaskOptions) {
        super(task, extension, inputFormat, inputOptions, outputFormat, outputOptions);

        this.taskOptions = anonymizationTaskOptions;
    }

    @Override
    public AnonymizationTaskOptions getTaskOptions() {
        return taskOptions;
    }

    @Override
    public void processFile(InputStream input, OutputStream output) throws MisconfigurationException {
        final AnonymizationAlgorithm algorithm = buildAnonymizationAlgorithm();

        final IPVDataset anonymizedDataset = algorithm.initialize(
                readInputDataset(input),
                taskOptions.getColumnInformation(),
                taskOptions.getPrivacyConstraints(),
                buildAnonymizationAlgorithmOptions()
                ).apply();

        writeDataset(output, anonymizedDataset);
    }

    private AnonymizationAlgorithmOptions buildAnonymizationAlgorithmOptions() {
        return switch (taskOptions.getAlgorithm()) {
            case OLA -> new OLAOptions(taskOptions.getSuppressionRate());
            case MONDRIAN -> new MondrianOptions(taskOptions.getCategoricalSplitStrategy());
            case KMAP -> new KMapOptions(taskOptions.getSuppressionRate());
            case KMEANS -> new KMeansOptions(taskOptions.getSuppressionRate(), taskOptions.getStrategyOptions());
            case SAMPLING -> new SamplingOptions(taskOptions.getPercentage());
        };
    }

    private void writeDataset(OutputStream output, IPVDataset anonymizedDataset) {
        try (PrintWriter writer = new PrintWriter(output)) {
            writer.print(anonymizedDataset.toString());
        }
    }

    private IPVDataset readInputDataset(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return switch (getInputFormat()) {
                case CSV -> {
                    var options = (CSVDatasetOptions) getInputOptions();
                    yield IPVDataset.load(reader, options.isHasHeader(), options.getFieldDelimiter(), options.getQuoteChar(), options.isTrimFields());
                }
                case JSON -> JSONIPVDataset.load(reader);
                default -> throw new IllegalArgumentException("Format not supported (at the moment). Please contact support.");
            };
        } catch (IOException e) {
            throw new RuntimeException("Format not supported at the moment", e);
        }
    }

    private AnonymizationAlgorithm buildAnonymizationAlgorithm() {
        return switch (taskOptions.getAlgorithm()) {
            case OLA -> new OLA();
            case MONDRIAN -> new Mondrian();
            case KMAP -> new KMap();
            case KMEANS -> new KMeansAnonymization();
            case SAMPLING -> new Sampling();
        };
    }
}
