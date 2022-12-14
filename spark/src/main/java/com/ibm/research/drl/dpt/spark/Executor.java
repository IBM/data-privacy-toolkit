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
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class Executor {
    private static final Logger log = LoggerFactory.getLogger(Executor.class);
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

        log.info("input: " + input);
        log.info("output: " + output);
        log.info("conf: " + conf);

        // Read config
        confJson = SparkUtils.readConfigurationFile(conf);
        configurationManager = ConfigurationManager.load(confJson);

        inputRef = DatasetReference.fromFile(input);
        log.info("input configuration: " + inputRef);

        outputRef = DatasetReference.fromFile(output);
        log.info("output configuration: " + outputRef.toString());
    }

    public void run(SparkSession sparkSession) throws IOException {
        Dataset<Row> inputDataset = inputRef.readDataset(sparkSession);
        log.info("input dataset: " + inputDataset.count() + " rows");
    }
}
