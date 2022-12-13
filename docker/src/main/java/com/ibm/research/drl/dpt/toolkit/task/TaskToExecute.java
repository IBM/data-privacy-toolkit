/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.toolkit.anonymization.AnonymizationTask;
//import com.ibm.research.drl.dpt.toolkit.exploration.ExplorationTask;
//import com.ibm.research.drl.dpt.toolkit.freetext.FreeTextDeID;
import com.ibm.research.drl.dpt.toolkit.identification.IdentificationTask;
import com.ibm.research.drl.dpt.toolkit.masking.MaskingTask;
//import com.ibm.research.drl.dpt.toolkit.transaction_uniqueness.TransactionUniquenessTask;
import com.ibm.research.drl.dpt.toolkit.vulnerability.VulnerabilityTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "task")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IdentificationTask.class, name = "Identification"),
        @JsonSubTypes.Type(value = MaskingTask.class, name = "Masking"),
        @JsonSubTypes.Type(value = VulnerabilityTask.class, name = "Vulnerability"),
        @JsonSubTypes.Type(value = AnonymizationTask.class, name = "Anonymization"),
//        @JsonSubTypes.Type(value = TransactionUniquenessTask.class, name = "TransactionUniqueness"),
//        @JsonSubTypes.Type(value = ExplorationTask.class, name = "Exploration"),
//        @JsonSubTypes.Type(value = FreeTextDeID.class, name = "FreeTextDeID"),
})
public abstract class TaskToExecute {
    private static final Logger logger = LogManager.getLogger(TaskToExecute.class);

    private final String task;
    private final String extension;
    private final DataTypeFormat inputFormat;
    private final DatasetOptions inputOptions;
    private final DataTypeFormat outputFormat;
    private final DatasetOptions outputOptions ;

    public String getTask() {
        return task;
    }

    public String getExtension() {
        return extension;
    }

    public DataTypeFormat getInputFormat() {
        return inputFormat;
    }

    public DatasetOptions getInputOptions() {
        return inputOptions;
    }

    public DataTypeFormat getOutputFormat() {
        return outputFormat;
    }

    public DatasetOptions getOutputOptions() {
        return outputOptions;
    }

    public String buildOutputExtension() {
        if (this.getExtension() != null)
            return this.getExtension();

        if (this.getOutputFormat() != null)
            return this.getOutputFormat().name().toLowerCase();

        if (this.getInputFormat() != null)
            return this.getInputFormat().name().toLowerCase();

        return "out";
    }

    public TaskToExecute(
            String task,
            String extension,
            DataTypeFormat inputFormat,
            DatasetOptions inputOptions,
            DataTypeFormat outputFormat,
            DatasetOptions outputOptions
    ) {
        this.task = task;
        this.extension = extension;
        this.inputFormat = inputFormat;
        this.inputOptions = inputOptions;
        this.outputFormat = outputFormat;
        this.outputOptions = outputOptions;
    }

    public void enrichOptions(Map<String, Object> taskOptions) {
        for (Map.Entry<String, Object> option : taskOptions.entrySet()) {
            try {
                this.getTaskOptions().setOption(option.getKey(), option.getValue());
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                throw new IllegalArgumentException("Unable to set option: " + option.getKey(), e);
            }
        }
    }

    public abstract TaskOptions getTaskOptions();

    public abstract void processFile(InputStream input, OutputStream output) throws IOException, MisconfigurationException;
}
