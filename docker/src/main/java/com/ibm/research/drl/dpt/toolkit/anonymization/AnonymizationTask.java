/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
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

import java.io.*;

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
        switch (taskOptions.getAlgorithm()) {
            case OLA:
                return new OLAOptions(taskOptions.getSuppressionRate());
            case MONDRIAN:
                return new MondrianOptions(taskOptions.getCategoricalSplitStrategy());
            case KMAP:
                return new KMapOptions(taskOptions.getSuppressionRate());
            case KMEANS:
                return new KMeansOptions(taskOptions.getSuppressionRate(), taskOptions.getStrategyOptions());
            case SAMPLING:
                return new SamplingOptions(taskOptions.getPercentage());
        }

        throw new IllegalArgumentException("Unsupported algorithmType: " + taskOptions.getAlgorithm());
    }

    private void writeDataset(OutputStream output, IPVDataset anonymizedDataset) {
        try (PrintWriter writer = new PrintWriter(output)) {
            writer.print(anonymizedDataset.toString());
        }
    }

    private IPVDataset readInputDataset(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            switch (getInputFormat()) {
                case CSV:
                    CSVDatasetOptions options = (CSVDatasetOptions) getInputOptions();

                    return IPVDataset.load(reader, options.isHasHeader(), options.getFieldDelimiter(), options.getQuoteChar(), options.isTrimFields());
                case JSON:
                    return JSONIPVDataset.load(reader);

                case DICOM:
                case XLS:
                case XLSX:
                case XML:
                case PDF:
                case DOC:
                case DOCX:
                case PLAIN:
                case FHIR_JSON:
                case HL7:
                case PARQUET:
                case VCF:
                case JDBC:
                default:
                    throw new IllegalArgumentException("Format not supported (at the moment). Please contact support.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Format not supported at the moment", e);
        }
    }

    private AnonymizationAlgorithm buildAnonymizationAlgorithm() {
        switch (taskOptions.getAlgorithm()) {
            case OLA:
                return new OLA();
            case MONDRIAN:
                return new Mondrian();
            case KMAP:
                return new KMap();
            case KMEANS:
                return new KMeansAnonymization();
            case SAMPLING:
                return new Sampling();
        }

        throw new IllegalArgumentException("Unsupported algorithmType: " + taskOptions.getAlgorithm());
    }
}
