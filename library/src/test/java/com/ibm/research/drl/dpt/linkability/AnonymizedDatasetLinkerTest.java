/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.linkability;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnonymizedDatasetLinkerTest {
    @Test
    public void testAnonymizedMixCategoricalNumericalNoRanges() throws Exception {
        try (
                InputStream target = AnonymizedDatasetLinkerTest.class.getResourceAsStream("/datasetLinkerNumericalTest.csv");
                InputStream source = AnonymizedDatasetLinkerTest.class.getResourceAsStream("/datasetLinkerNumericalTest.csv")
        ) {

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, true));
            linkInformation.add(new LinkInfo(1, 1));

            List<ColumnInformation> columnInformation = new ArrayList<>();

            IPVDataset dataset = IPVDataset.load(source, false, ',', '"', false);
            columnInformation.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
            columnInformation.add(ColumnInformationGenerator.generateCategoricalFromData(dataset, 1, ColumnType.QUASI));

            AnonymizedDatasetLinker anonymizedDatasetLinker = new AnonymizedDatasetLinker(target, linkInformation);

            List<String> anonymizedRow = Arrays.asList("10", "e");
            Integer matches = anonymizedDatasetLinker.matchAnonymizedRow(anonymizedRow, linkInformation, columnInformation);
            assertEquals(1, matches.intValue());

            anonymizedRow = Arrays.asList("10", "*");
            matches = anonymizedDatasetLinker.matchAnonymizedRow(anonymizedRow, linkInformation, columnInformation);
            assertEquals(2, matches.intValue());

            anonymizedRow = Arrays.asList("10-12", "*");
            matches = anonymizedDatasetLinker.matchAnonymizedRow(anonymizedRow, linkInformation, columnInformation);
            assertEquals(3, matches.intValue());

            anonymizedRow = Arrays.asList("10-12", "w");
            matches = anonymizedDatasetLinker.matchAnonymizedRow(anonymizedRow, linkInformation, columnInformation);
            assertEquals(0, matches.intValue());

            anonymizedRow = Arrays.asList("10-12", "a");
            matches = anonymizedDatasetLinker.matchAnonymizedRow(anonymizedRow, linkInformation, columnInformation);
            assertEquals(1, matches.intValue());
        }
    }

    @Test
    public void testAnonymizedDataset() throws Exception {
        try (
                InputStream sourceInputStream = AnonymizedDatasetLinkerTest.class.getResourceAsStream("/link_source_anon.csv");
                InputStream targetInputStream = AnonymizedDatasetLinkerTest.class.getResourceAsStream("/link_target_anon.csv")) {

            IPVDataset source = IPVDataset.load(sourceInputStream, false, ',', '"', false);

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, "*", false, -1));
            linkInformation.add(new LinkInfo(1, 1, "*", false, -1));

            AnonymizedDatasetLinker linker = new AnonymizedDatasetLinker(targetInputStream, linkInformation);
            List<Integer> linkResults = linker.matchesPerRecord(source, linkInformation, columnInformation);

            assertEquals(7, linkResults.size());

            int counter3 = 0;
            int counter2 = 0;
            int counter0 = 0;

            for (Integer counter : linkResults) {
                if (counter == 3) {
                    counter3++;
                }

                if (counter == 2) {
                    counter2++;
                }

                if (counter == 0) {
                    counter0++;
                }
            }

            assertEquals(2, counter3);
            assertEquals(2, counter2);
            assertEquals(3, counter0);
        }
    }

    @Test
    public void testAnonymizedDataset2() throws Exception {
        try (
                InputStream sourceInputStream = AnonymizedDatasetLinkerTest.class.getResourceAsStream("/link_source_anon2.csv");
                InputStream targetInputStream = AnonymizedDatasetLinkerTest.class.getResourceAsStream("/link_target_anon2.csv")) {
            IPVDataset source = IPVDataset.load(sourceInputStream, false, ',', '"', false);

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, "*", false, -1));
            linkInformation.add(new LinkInfo(1, 1, "*", false, -1));

            AnonymizedDatasetLinker linker = new AnonymizedDatasetLinker(targetInputStream, linkInformation);
            List<Integer> linkResults = linker.matchesPerRecord(source, linkInformation, columnInformation);

            assertEquals(7, linkResults.size());

            int counter3 = 0;
            int counter2 = 0;
            int counter0 = 0;

            for (Integer counter : linkResults) {
                if (counter == 3) {
                    counter3++;
                }

                if (counter == 2) {
                    counter2++;
                }

                if (counter == 0) {
                    counter0++;
                }
            }

            assertEquals(2, counter3);
            assertEquals(2, counter2);
            assertEquals(3, counter0);
        }
    }
}
