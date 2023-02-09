/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ibm.research.drl.dpt.spark.task.SparkTaskToExecute;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DPTSparkDriver {
    private static final Logger logger = LogManager.getLogger(DPTSparkDriver.class);

    private enum CommandLineOptions {
        DisplayHelp("h", "help", "Display help", false, false),
        Configuration("c", "configuration", "Path to the configuration file", true, true),
        Input("i", "input", "Path to the input (file or directory)", true, true),
        Output("o", "output", "Path to the output (file or directory)", true, true),
        TaskArguments("a", "arguments", "List of additional task arguments", false, true),

        TaskName("n", "name", "Optional task name", false, true),
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

    private static CommandLine createCommandLine(Options options, String[] args) throws ParseException {
        return new DefaultParser().parse(options, args);
    }

    private static String extractFileExtension(String configurationFile) {
        return configurationFile.substring(configurationFile.lastIndexOf(".") + 1);
    }

    private static ObjectMapper buildMapper(String configurationFile) {
        final String extension = extractFileExtension(configurationFile);

        if (extension.equalsIgnoreCase("json")) {
            return JsonUtils.MAPPER;
        } else if (extension.equalsIgnoreCase("yaml") || extension.equalsIgnoreCase("yml")) {
            return new ObjectMapper(new YAMLFactory());
        }

        throw new IllegalArgumentException("Unknown extension " + configurationFile);
    }

    public static void main(String[] args) {
        System.setProperty("com.ibm.crypto.provider.DoRSATypeChecking", "false");

        Options options = buildOptions();

        try {
            CommandLine commandLine = createCommandLine(options, args);

            final String configurationFile = commandLine.getOptionValue(CommandLineOptions.Configuration.shortOption);

            final ObjectMapper mapper = buildMapper(configurationFile);

            final SparkSession session = SparkSession.builder().config(new SparkConf(true).setAppName(commandLine.getOptionValue(CommandLineOptions.TaskName.shortOption, "DPTSpark"))).getOrCreate();

            try (InputStream configuration = readConfiguration(configurationFile, session)) {

                final SparkTaskToExecute taskToExecute = mapper.readValue(configuration, SparkTaskToExecute.class);

                Dataset<Row> processedDataset = taskToExecute.process(
                        taskToExecute.readInputDataset(
                                commandLine.getOptionValue(CommandLineOptions.Input.shortOption)
                        )
                );

                taskToExecute.writeProcessedDataset(processedDataset, commandLine.getOptionValue(CommandLineOptions.Output.shortOption));

                System.exit(0);
            }
        } catch (IOException | ParseException | RuntimeException e) {
            logger.error("Execution failed " + e.getMessage());
            logger.debug("Additional information:", e);
        }

        new HelpFormatter().printHelp(System.getProperty("java.class.path"), "", options, "", true);

        System.exit(1);
    }

    private static InputStream readConfiguration(String configurationFile, SparkSession session) {
        return null;
    }
}
