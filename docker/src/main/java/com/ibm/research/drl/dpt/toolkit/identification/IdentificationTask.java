/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.identification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.processors.FormatProcessor;
import com.ibm.research.drl.dpt.processors.FormatProcessorFactory;
import com.ibm.research.drl.dpt.processors.IdentificationReport;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.providers.identifiers.PluggableIdentifierType;
import com.ibm.research.drl.dpt.providers.identifiers.PluggableLookupIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.PluggableRegexIdentifier;
import com.ibm.research.drl.dpt.toolkit.dataset.GenericDatasetOptions;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdentificationTask extends TaskToExecute {
    private static final Logger logger = LogManager.getLogger(IdentificationTask.class);

    private final IdentificationOptions taskOptions;
    private final ObjectMapper mapper;
    private final Collection<Identifier> identifiers;

    @JsonCreator
    public IdentificationTask(
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
            @JsonProperty("taskOptions") IdentificationOptions identificationOptions
    ) {
        super(task, extension, inputFormat, inputOptions, null, null );
        this.taskOptions = identificationOptions;
        mapper = new ObjectMapper();
        identifiers = taskSpecificSetOfIdentifiers(taskOptions);
    }

    @Override
    public IdentificationOptions getTaskOptions() {
        return this.taskOptions;
    }

    @Override
    public String buildOutputExtension() {
        return "json";
    }

    @Override
    public void processFile(InputStream input, OutputStream output) throws IOException, MisconfigurationException {
        FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(this.getInputFormat());

        IdentificationReport results = formatProcessor.identifyTypesStream(input, this.getInputFormat(), this.getInputOptions(), identifiers, this.getTaskOptions().getFirstN());

        output.write(mapper.writeValueAsBytes(results));
    }

    private Collection<Identifier> taskSpecificSetOfIdentifiers(IdentificationOptions taskOptions) {
        if (null == taskOptions.getIdentifiers() || taskOptions.getIdentifiers().isEmpty()) {
            logger.warn("Using default identifiers");
            return IdentifierFactory.defaultIdentifiers();
        }

        return IdentifierFactory.initializeIdentifiers(taskOptions.getIdentifiers()).availableIdentifiers();
    }
}
