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

        return Stream.concat(
                taskOptions.getIdentifiers().stream().filter(JsonNode::isTextual).map(JsonNode::asText)
                        .map(identifierClassName -> {
                            try {
                                return (Identifier) Class.forName(identifierClassName).getConstructor().newInstance();
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }),
                constructUserDefinedIdentifiers(taskOptions.getIdentifiers().stream().filter(JsonNode::isObject))
        ).collect(Collectors.toList());
    }

    private Stream<Identifier> constructUserDefinedIdentifiers(Stream<JsonNode> userDefinedSpecifications) {
        return userDefinedSpecifications.map(spec -> {
            final PluggableIdentifierType type = PluggableIdentifierType.valueOf(spec.get("type").asText());

            switch (type) {
                case DICTIONARY:
                    return buildDictionaryBasedUserDefinedIdentifier(spec);
                case REGEX:
                    return buildRegexBasedUserDefinedIdentifier(spec);
                default:
                    throw new RuntimeException("Unknown type");
            }
        });
    }

    private Identifier buildRegexBasedUserDefinedIdentifier(JsonNode spec) {
        final List<String> expressions = new ArrayList<>(spec.get("regex").size());
        spec.get("regex").elements().forEachRemaining(e -> {
            if (null != e && e.isTextual()) {
                expressions.add(e.asText().trim());
            }
        });

        boolean isPosIndependent = !spec.has("isPosIndependent") || spec.get("isPosIndependent").asBoolean();

        return new PluggableRegexIdentifier(
                spec.get("providerType").asText(),
                Collections.emptyList(),
                //Stream.generate(expressions::next).map(JsonNode::asText).collect(Collectors.toList()),
                expressions,
                ValueClass.TEXT,
                isPosIndependent
        );
    }

    private Identifier buildDictionaryBasedUserDefinedIdentifier(JsonNode spec) {
        boolean isPosIndependent = !spec.has("isPosIndependent") || spec.get("isPosIndependent").asBoolean();
        boolean ignoreCase = !spec.has("ignoreCase") || spec.get("ignoreCase").asBoolean();

        return new PluggableLookupIdentifier(
                spec.get("providerType").asText(),
                Collections.emptyList(),
                buildTermsList(spec),
                ignoreCase,
                ValueClass.TEXT,
                isPosIndependent
        );
    }

    private Collection<String> buildTermsList(JsonNode spec) {
        Set<String> terms = new HashSet<>();

        if (spec.has("terms")) {
            spec.get("terms").elements().forEachRemaining( term -> terms.add(term.asText().trim()));
        }
        if (spec.has("paths")) {
           spec.get("paths").elements().forEachRemaining(
                    path -> terms.addAll(readFromFile(path.asText()))
           );
        }
        if (terms.isEmpty()) throw new RuntimeException("No terms specified");

        return terms;
    }

    private Collection<? extends String> readFromFile(String path) {
        Set<String> terms = new HashSet<>();
        for (File file : populateFilesToProcess(path)) {
            try {
                terms.addAll(
                        Files.lines(file.toPath()).map(String::strip).collect(Collectors.toList())
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return terms;
    }

    private File[] populateFilesToProcess(String fileName) {
        final File fileToProcess = new File(fileName);

        if (!fileToProcess.exists()) throw new RuntimeException(fileToProcess.getAbsoluteFile() + " does not exist");

        if (fileToProcess.isDirectory()) {
            return Objects.requireNonNull(fileToProcess.listFiles());
        } else {
            return new File[]{ fileToProcess };
        }
    }
}
