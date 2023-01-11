/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.anonymization;

import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.spark.anonymization.mondrian.MondrianExecutor;
import com.ibm.research.drl.dpt.spark.anonymization.mondrian.MondrianSpark;
import com.ibm.research.drl.dpt.spark.anonymization.ola.OLASpark;
import com.ibm.research.drl.dpt.spark.export.Export;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.cli.*;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;

import java.io.IOException;
import java.io.InputStream;

public class AnonymizationExecutor {

    public static void main(String[] args) throws IOException {
        Options options = new Options();

        Option confOption = new Option("c", "conf", true, "configuration file (required)");
        confOption.setRequired(true);

        options.addOption(confOption);

        Option i = new Option("i", "input", true, "input (required)");
        i.setRequired(true);
        options.addOption(i);
        Option o = new Option("o", "output", true, "output folder (required)");
        o.setRequired(true);
        options.addOption(o);

        Option algorithmOption = new Option("a", "algorithm", true, "algorithm (required)");
        algorithmOption.setRequired(true);
        options.addOption(algorithmOption);

        try {

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);


            boolean remoteConfiguration = cmd.hasOption("remoteConf");
            InputStream confStream = SparkUtils.readFile(cmd.getOptionValue("c"), remoteConfiguration);

            DataTypeFormat inputFormat = DataTypeFormat.CSV;
            final DataTypeFormat exportFormat = DataTypeFormat.CSV;

            SparkContext sc = SparkUtils.createSparkContext("Data Anonymization");
            JavaRDD<String> inputRDD = SparkUtils.createTextFileRDD(sc, cmd.getOptionValue("i"));

            String algorithmName = cmd.getOptionValue("a");

            JavaRDD<String> outputRDD;

            switch (algorithmName.toUpperCase()) {
                case "OLA":
                    outputRDD = OLASpark.run(confStream, inputRDD);
                    Export.doExport(outputRDD, cmd.getOptionValue("o"));
                    break;
                case "MONDRIAN":
                    outputRDD = MondrianSpark.run(confStream, inputRDD);
                    Export.doExport(outputRDD, cmd.getOptionValue("o"));
                default:
                    throw new RuntimeException("invalid algorithm name: " + algorithmName);
            }


            confStream.close();
            sc.stop();
        } catch (ParseException e) {
            String header = "Data Anonymization\n\n";
            String footer = "\n";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("AnonymizationExecutor", header, options, footer, true);
            throw new RuntimeException("invalid arguments");
        }
    }
}
