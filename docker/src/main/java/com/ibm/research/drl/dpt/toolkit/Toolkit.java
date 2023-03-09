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
package com.ibm.research.drl.dpt.toolkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ibm.research.drl.dpt.toolkit.task.TaskToExecute;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Toolkit {
    private static final Logger logger = LogManager.getLogger(Toolkit.class);

    private enum CommandLineOptions {
        DisplayHelp("h", "help", "Display help", false, false),
        Configuration("c", "configuration", "Path to the configuration file", true, true),
        Input("i", "input", "Path to the input (file or directory)", true, true),
        Output("o", "output", "Path to the output (file or directory)", true, true),
        TaskArguments("a", "arguments", "List of additional task arguments", false, true),
        ;

        private final String shortOption;
        private final String longOption;
        private final String desc;
        private final boolean required;
        private final boolean hasArgs;

        CommandLineOptions(String shortOption, String longOption, String desc, boolean required, boolean hasArgs) {
            this.shortOption = shortOption;
            this.longOption = longOption;
            this.desc = desc;
            this.required = required;
            this.hasArgs = hasArgs;
        }
    }

    public static void main(String[] args) {
        System.setProperty("com.ibm.crypto.provider.DoRSATypeChecking", "false");

        Options options = buildOptions();

        try {
            CommandLine commandLine = createCommandLine(options, args);

            final String configurationFile = commandLine.getOptionValue(CommandLineOptions.Configuration.shortOption);

            final ObjectMapper mapper = buildMapper(configurationFile);

            try (InputStream configuration = new FileInputStream(configurationFile)) {

                final TaskToExecute taskToExecute = mapper.readValue(configuration, TaskToExecute.class);

                taskToExecute.enrichOptions(buildTaskOptions(commandLine.getOptionValue(CommandLineOptions.TaskArguments.shortOption)));

                File input = createFile(commandLine.getOptionValue(CommandLineOptions.Input.shortOption));
                File output = createFile(commandLine.getOptionValue(CommandLineOptions.Output.shortOption));

                if (input.getAbsolutePath().equals(output.getAbsolutePath())) {
                    logger.error("Path to the input (file or directory) and path to the output (file or directory) must be different");
                    throw new IllegalArgumentException("Path to the input (file or directory) and path to the output (file or directory) must be different");
                }

                processTasks(input, output, taskToExecute);

                return;
            }
        } catch (IOException | ParseException | RuntimeException e) {
            logger.error("Execution failed: {}", e.getMessage());
            logger.debug("Additional information:", e);
        }
        new HelpFormatter().printHelp(System.getProperty("java.class.path"), "", options, "", true);

        System.exit(1);
    }

    protected static void processTasks(File input, File output, TaskToExecute taskToExecute) {
        if (!input.exists()) {
            logger.error("File not found: {}", input);
            throw new RuntimeException("File not found: "+ input);
        }

        if (input.isDirectory()) {
            if (!output.isDirectory()) {
                logger.error("Input is a directory, output must be a directory as well");
                throw new RuntimeException("Input is a directory, output must be a directory as well");
            }

            processDirectory(input, output, taskToExecute);
        } else {
            if (output.isDirectory()) {
                output = buildOutputFile(input, output, taskToExecute);
            }
            checkParentOfOutputExists(output.getAbsoluteFile().getParentFile());
            processFile(input, output, taskToExecute);
        }
    }

    private static void checkParentOfOutputExists(File parentFile) {
        logger.info("Processing single file to single file, validating output path");
        if (!parentFile.exists()) {
            logger.info("Directory path {} does not exists", parentFile);

            if (!parentFile.mkdirs()) {
                logger.error("Error creating {}", parentFile);
                throw new RuntimeException("Unable to create output directory");
            }
        }
    }

    private static void processDirectory(File inputDirectory, File outputDirectory, TaskToExecute taskToExecute) {
        Iterable<File> listOfFiles = listValidFiles(inputDirectory, taskToExecute.getExtension());

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new RuntimeException("Unable to create output directory");
            }
        } else {
            if (!outputDirectory.isDirectory()) {
                throw new IllegalArgumentException("Output destination is not a directory");
            }
        }

        for (File inputFile : listOfFiles) {
            if (!inputFile.isHidden()) {
                if (inputFile.isFile()) {
                    File outputFile = buildOutputFile(inputFile, outputDirectory, taskToExecute);

                    logger.trace("Processing {} with {} as output", inputFile.getName(), outputFile.getName());

                    processFile(inputFile, outputFile, taskToExecute);
                } else {
                    processDirectory(inputFile, new File(outputDirectory, inputFile.getName()), taskToExecute);
                }
            } else {
                logger.trace("Skipping hidden file {}", inputFile.getName());
            }
        }
    }

    private static File buildOutputFile(File inputFile, File outputDirectory, TaskToExecute taskToExecute) {
        String outputFileName =
                FilenameUtils.getBaseName(inputFile.getName()) +
                        "." +
                        taskToExecute.buildOutputExtension();
        return new File(outputDirectory, outputFileName);
    }

    private static Iterable<File> listValidFiles(File directory, String extension) {
        if (null != extension) {
            return Arrays.asList(
                    Objects.requireNonNull(directory.listFiles(
                            pathname -> pathname.isDirectory() ||
                                    (pathname.isFile() && pathname.getName().endsWith(extension))
                    ))
            );
        }
        return Arrays.asList(Objects.requireNonNull(directory.listFiles()));
    }

    private static void processFile(File inputFile, File outputFile, TaskToExecute taskToExecute) {
        try (
                InputStream input = new FileInputStream(inputFile);
                OutputStream output = new FileOutputStream(outputFile)
        ) {
            taskToExecute.processFile(input, output);
        } catch (IOException e) {
            logger.error("Error processing {} and writing to {}", inputFile, outputFile);
            throw new RuntimeException(e);
        }
    }

    private static File createFile(String filePath) {
        return TypeHandler.createFile(filePath);
    }

    private static ObjectMapper buildMapper(String configurationFile) {
        final String extension = extractFileExtension(configurationFile);

        if (extension.equalsIgnoreCase("json")) {
            return new ObjectMapper();
        } else if (extension.equalsIgnoreCase("yaml") || extension.equalsIgnoreCase("yml")) {
            return new ObjectMapper(new YAMLFactory());
        }

        throw new IllegalArgumentException("Unknown extension " + configurationFile);
    }

    private static String extractFileExtension(String configurationFile) {
        return configurationFile.substring(configurationFile.lastIndexOf(".") + 1);
    }

    private static Map<String, Object> buildTaskOptions(String additionalArguments) {
        if (null == additionalArguments || additionalArguments.isEmpty()) return Collections.emptyMap();

        return Arrays.stream(additionalArguments.split(";"))
                .map(String::trim)
                .filter(((Predicate<String>)String::isEmpty).negate())
                .filter(((Predicate<String>) line -> line.startsWith("#")).negate())
                .map( option -> option.split("\\s*=\\s*"))
                .filter( optionArgument -> 2 == optionArgument.length)
                .collect(
                        Collectors.toMap(
                                optionArgument -> optionArgument[0],
                                optionArgument -> optionArgument[1]
                        )
                );
    }

    private static CommandLine createCommandLine(Options options, String[] args) throws ParseException {
        return new DefaultParser().parse(options, args);
    }

    private static Options buildOptions() {
        Options options = new Options();

        Arrays.stream(CommandLineOptions.values()).map(
                commandLineOption -> Option.builder(commandLineOption.shortOption)
                        .longOpt(commandLineOption.longOption)
                        .required(commandLineOption.required)
                        .desc(commandLineOption.desc)
                        .hasArg(commandLineOption.hasArgs)
                        .build()
        ).forEach(options::addOption);

        return options;
    }
}

