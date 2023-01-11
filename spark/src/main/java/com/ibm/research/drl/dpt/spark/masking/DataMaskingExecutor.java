/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.spark.export.Export;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DataMaskingExecutor {
    private static final Logger logger = LogManager.getLogger(DataMaskingExecutor.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
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
        options.addOption("p", true, "Partition by");
        options.addOption("a", false, "Append writing mode");
        options.addOption("basePath", true, "Base path for reading partitioned data");
        
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            boolean remoteConfiguration = cmd.hasOption("remoteConf");

            JsonNode confFile = SparkUtils.readConfigurationFile(cmd.getOptionValue("c"), remoteConfiguration);
           
            logger.info("input: " + cmd.getOptionValue("i"));
            logger.info("output: " + cmd.getOptionValue("o"));
            logger.info("configuration file: " + confFile.toString());
            logger.info("append: " + cmd.hasOption("a"));
            
            ConfigurationManager configurationManager = ConfigurationManager.load(confFile);

            final DataMaskingOptions maskingOptions = SparkUtils.deserializeConfiguration(cmd.getOptionValue("c"), cmd.hasOption("remoteConf"), DataMaskingOptions.class);

            final DataTypeFormat exportFormat = maskingOptions.getOutputFormat();

            SparkSession sparkSession = SparkUtils.createSparkSession("Data Masking");
            
            String basePath = cmd.hasOption("basePath") ? cmd.getOptionValue("basePath") : null;
            logger.info("basePath " + basePath);
            
            Dataset<Row> dataset = SparkUtils.createDataset(sparkSession, cmd.getOptionValue("i"), 
                    maskingOptions.getInputFormat(), maskingOptions.getDatasetOptions(), basePath);

            checkFieldsAgainstSchema(maskingOptions.getToBeMasked().keySet(), Arrays.asList(dataset.columns()));
            checkFieldsAgainstSchema(extractFieldsUsedInCompound(maskingOptions), Arrays.asList(dataset.columns()));

            Dataset<Row> outputRDD = DataMasking.run(configurationManager, maskingOptions, dataset);

            validateSchemaDidNotChange(dataset.schema(), outputRDD.schema());

            List<String> partitions;
            if (cmd.hasOption("p")) {
                partitions = Arrays.asList(cmd.getOptionValue("p").split(","));
            } else {
                partitions = Collections.emptyList();
            }
            
            logger.info("partitions: " + StringUtils.join(partitions, ','));
            
            Export.doExport(outputRDD, exportFormat, cmd.getOptionValue("o"), partitions, cmd.hasOption("a"));

            sparkSession.stop();
        } catch (ParseException e) {
            String header = "Data Masking\n\n";
            String footer = "\n";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DataMaskingExecutor", header, options, footer, true);
            throw new RuntimeException("invalid arguments");
        }
    }

    private static Set<String> extractFieldsUsedInCompound(DataMaskingOptions maskingOptions) {
        Map<String, FieldRelationship> fieldRelationshipMap = maskingOptions.getPredefinedRelationships();
        if (fieldRelationshipMap == null || fieldRelationshipMap.isEmpty()) {
            return Collections.emptySet();
        }

        return fieldRelationshipMap.values().stream().flatMap(
                fieldRelationship -> Arrays.stream(fieldRelationship.getOperands()).map(RelationshipOperand::getName)
        ).collect(Collectors.toSet());
    }

    private static void validateSchemaDidNotChange(StructType before, StructType after) {
        StructField[] afterFields = after.fields();

        for (StructField beforeField : before.fields()) {
            int index = after.fieldIndex(beforeField.name());

            StructField afterField = afterFields[index];

            if (!beforeField.dataType().sameType(afterField.dataType())) throw new RuntimeException("Type changed for field " + beforeField.name());
            if (beforeField.nullable() != afterField.nullable()) throw new RuntimeException("Nullability requirement changed for field " + beforeField.name());
        }
    }

    private static void checkFieldsAgainstSchema(Set<String> fieldsToBeMasked, Collection<String> schemaFields) {
        if (!schemaFields.containsAll(fieldsToBeMasked)) {
            throw new RuntimeException("Missing fields " +
                    fieldsToBeMasked.parallelStream().filter(((Predicate<String>)schemaFields::contains).negate()).collect(Collectors.joining(",")) +
                    " configured for masking but do not exist in the schema");
        }
        
    }
}
