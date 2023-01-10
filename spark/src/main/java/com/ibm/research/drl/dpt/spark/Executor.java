/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.spark.dataset.reference.DatasetReference;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

public abstract class Executor {
    private static final Logger logger = LogManager.getLogger(Executor.class);

    protected final Options options =  SparkUtils.buildCommonCommandLineOptions();

    protected String input;
    protected String output;
    protected String conf;

    protected JsonNode confJson;
    protected ConfigurationManager configurationManager;

    protected DatasetReference inputRef;

    protected DatasetReference outputRef;

    protected void init(String[] args) throws IOException, ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        input = cmd.getOptionValue("input");
        output = cmd.getOptionValue("output");
        conf = cmd.getOptionValue("conf");

        logger.info("input: " + input);
        logger.info("output: " + output);
        logger.info("conf: " + conf);

        // Read config
        confJson = SparkUtils.readConfigurationFile(conf);
        configurationManager = ConfigurationManager.load(confJson);

        inputRef = DatasetReference.fromFile(input);
        logger.info("input configuration: " + inputRef);

        outputRef = DatasetReference.fromFile(output);
        logger.info("output configuration: " + outputRef.toString());
    }

    public void run(SparkSession sparkSession) throws IOException {
        Dataset<Row> inputDataset = inputRef.readDataset(sparkSession);
        logger.info("input dataset: " + inputDataset.count() + " rows");
    }
}
