/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.anonymization.ola;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class SyntheticDatasetGenerator {

    private List<String> getLeaves(List<List<String>> values) {
        List<String> terms = new ArrayList<>();
        for(List<String> l: values) {
            terms.add(l.get(0));
        }
        return terms;
    }

    private void generateRandomDataset(Appendable out, int numberOfRows, List<ColumnInformation> columnInformationList) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.RFC4180);) {
            List<List<String>> leaves = new ArrayList<>();

            for (ColumnInformation columnInformation : columnInformationList) {
                ColumnType columnType = columnInformation.getColumnType();
                if (columnType == ColumnType.QUASI) {
                    CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
                    leaves.add(getLeaves(((MaterializedHierarchy) categoricalInformation.getHierarchy()).getTerms()));
                } else {
                    List<String> dummy = new ArrayList<>();
                    dummy.add("foo");
                    leaves.add(dummy);
                }
            }

            SecureRandom random = new SecureRandom();

            for (int i = 0; i < numberOfRows; i++) {
                List<String> row = new ArrayList<>();

                for (List<String> leavesList : leaves) {
                    int index = random.nextInt(leavesList.size());
                    row.add(leavesList.get(index));
                }

                printer.printRecord(row);
            }
        }
    }

    public static void main(String[] args) throws IOException, MisconfigurationException {
        String configurationFilename = validate(args[0]);
        String outputFilename = validate(args[1]);
        int numberOfRows = Integer.parseInt(args[2]);

        JsonNode configurationJSON = new ObjectMapper().readTree(new FileInputStream(configurationFilename));
        List<ColumnInformation> columnInformationList = AnonymizationOptions.columnInformationFromJSON(configurationJSON.get("columnInformation"), null);

        SyntheticDatasetGenerator syntheticDatasetGenerator = new SyntheticDatasetGenerator();

        FileWriter fileWriter = new FileWriter(outputFilename);
        syntheticDatasetGenerator.generateRandomDataset(fileWriter, numberOfRows, columnInformationList);
        fileWriter.close();
    }

    private static String validate(String filename) {
        if ("/etc/passwd".equals(filename) ||
                "/etc/shadow".equals(filename)) throw new IllegalArgumentException(filename + " is not a valid file");

        return filename;
    }

}
