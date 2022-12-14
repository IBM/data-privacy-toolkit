/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import com.ibm.research.drl.dpt.spark.Executor;
import com.ibm.research.drl.dpt.util.IdentifierUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;


public class IdentificationExecutor extends Executor {
    private static final Logger log = LoggerFactory.getLogger(IdentificationExecutor.class);

    private IdentificationOptions identificationOptions;
    private  IdentificationConfiguration identificationConfiguration;

    @Override
    protected void init(String[] args) throws IOException, ParseException {
        super.init(args);

        this.identificationOptions = IdentificationOptions.fromJson(confJson);
        this.identificationConfiguration = new IdentificationConfiguration(confJson);
    }

    @Override
    public void run(SparkSession sparkSession) throws IOException {
        super.run(sparkSession);

        Dataset<Row> inputDataset = inputRef.readDataset(sparkSession);

        if (this.identificationOptions.getSpecificField() != null && !this.identificationOptions.getSpecificField().isEmpty()) {
            inputDataset = doSelecting(inputDataset, this.identificationOptions);
        }

        if (this.identificationOptions.isGetFirstN()) {
            inputDataset = doLimiting(inputDataset, this.identificationOptions);
        }

        if (this.identificationOptions.isDoSampling()) {
            inputDataset = doSampling(inputDataset, this.identificationOptions);
        }

        try (OutputStream os = this.outputRef.asOutputStream();
             PrintStream printStream = new PrintStream(os)) {

            IdentificationResults report = identifyDataset(inputDataset, this.confJson, this.identificationOptions, this.identificationConfiguration);

            final ObjectMapper objectMapper = new ObjectMapper();
            log.info("Writing to the report file");
            printStream.print(
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report)
            );
        }

        sparkSession.stop();
    }

    public static void main(String[] args) throws IOException {
        IdentificationExecutor executor = new IdentificationExecutor();

        try {
            executor.init(args);
        } catch (ParseException e) {
            String header = "Data type identification\n\n";
            String footer = "\n";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("IdentificationExecutor", header, executor.options, footer, true);
            throw new RuntimeException("invalid arguments");
        }

        SparkSession sparkSession = SparkSession.builder().sparkContext(new SparkContext(new SparkConf(true).
                setAppName("Data type identification"))).getOrCreate();

        executor.run(sparkSession);
    }

    List<String> getIdentifiersList(Collection<Identifier> identifiers) {
        List<String> debugList = new ArrayList<>();
        
        for(Identifier identifier: identifiers) {
            debugList.add(identifier.getClass().getCanonicalName());
        }
        
        return debugList;
    }

    Dataset<Row> doSelecting(Dataset<Row> originalDataset, IdentificationOptions identificationOptions) {
        final String specificField = identificationOptions.getSpecificField();
        log.info("Specific field: " + specificField);

        return originalDataset.select(specificField);
    }

    Dataset<Row> doLimiting(Dataset<Row> originalDataset, IdentificationOptions identificationOptions) {
        final int firstN = identificationOptions.getN();
        log.info("Limit: " + firstN);

        return originalDataset.limit(firstN);
    }

    Dataset<Row> doSampling(Dataset<Row> originalDataset, IdentificationOptions identificationOptions) {
        final double fraction = identificationOptions.getSampleFraction();
        log.info("Fraction : " + fraction);

        if (fraction < 1.0) {
            final Dataset<Row> sampledDataset = originalDataset.sample(false, fraction);

            if (identificationOptions.isDumpSample()) {
                String outputFolder = UUID.randomUUID().toString();
                log.info("Saving sample to : " + outputFolder);
                sampledDataset.write().parquet(outputFolder);
            }

            return sampledDataset;
        } else {
            return originalDataset;
        }
    }

    IdentificationResults identifyDataset(Dataset<Row> inputDataset, JsonNode confJson, IdentificationOptions identificationOptions, IdentificationConfiguration identificationConfiguration) {
        IdentificationViaFreeText identification = new IdentificationViaFreeText();

        log.info("Processing started");

        long processed = inputDataset.count();

        final Map<String, List<IdentifiedType>> identifiedTypes =
                identification.identifyDataset(inputDataset, confJson, identificationOptions.isSplitByCharacterRequirements(), processed);

        log.info("Processing finished");

        final Map<String, List<IdentifiedType>> bestTypes = new HashMap<>();


        for (Map.Entry<String, List<IdentifiedType>> entry : identifiedTypes.entrySet()) {
            String fieldName = entry.getKey();
            List<IdentifiedType> typesForFields = entry.getValue();

            IdentifiedType bestType = IdentifierUtils.findBestType(typesForFields, processed, identificationConfiguration);

            if (bestType != null) {
                bestTypes.put(fieldName, Arrays.asList(bestType));
            } else {
                bestTypes.put(fieldName, Collections.emptyList());
            }
        }

        List<String> identifiersList = null;
        if (identificationOptions.isDebug()) {
            identifiersList = getIdentifiersList(identificationOptions.getIdentifierFactory().availableIdentifiers());
        }

        long totalCount = inputDataset.count();
        IdentificationResults report = new IdentificationResults(identifiedTypes, bestTypes, identifiersList, totalCount);

        return report;
    }
}
