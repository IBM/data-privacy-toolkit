/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.linkability;

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
    @Disabled("Testing required on dataset too large to execute in Travis/Workflow")
    public void dummy() throws Exception {
        long start = System.currentTimeMillis();

        Map<String, Set<Integer>>[] columns = new HashMap[4];

        for (int i = 0; i < columns.length; ++i) {
            columns[i] = new HashMap<>();
        }

        try (InputStream source = getClass().getResourceAsStream("/florida_link.txt")) {
            try (CSVParser parser = new CSVParser(new InputStreamReader(source), CSVFormat.RFC4180)) {
                int i = 0;
                for (CSVRecord record : parser) {
                    String[] values = {
                            record.get(2) // zip
                            , record.get(3) // gender
                            , record.get(4) //yob
                            , record.get(5) //race
                    };

                    for (int j = 0; j < values.length; ++j) {

                        String value = values[j];

                        Set<Integer> records = columns[j].get(value);

                        if (null == records) {
                            records = new HashSet<>();
                            columns[0].put(value, records);
                        }
                        records.add(i);
                    }


                    ++i;
                }
            }
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void testAnonymizedMixCategoricalNumericalNoRanges() throws Exception {
        
        InputStream target = this.getClass().getResourceAsStream("/datasetLinkerNumericalTest.csv");
        
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, true));
        linkInformation.add(new LinkInfo(1, 1));
        
        List<ColumnInformation> columnInformation = new ArrayList<>();
        
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/datasetLinkerNumericalTest.csv"), false, ',', '"', false);
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

    @Test
    public void testAnonymizedDataset() throws Exception {
        InputStream sourceInputStream = this.getClass().getResourceAsStream("/link_source_anon.csv");
        InputStream targetInputStream = this.getClass().getResourceAsStream("/link_target_anon.csv");

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

        for(Integer counter: linkResults) {
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

    @Test
    public void testAnonymizedDataset2() throws Exception {
        InputStream sourceInputStream = this.getClass().getResourceAsStream("/link_source_anon2.csv");
        InputStream targetInputStream = this.getClass().getResourceAsStream("/link_target_anon2.csv");

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

        for(Integer counter: linkResults) {
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

    @Test
    @Disabled
    public void testAnonymizedDatasetFullScaleWithMasked() throws Exception {
        InputStream sourceInputStream = this.getClass().getResourceAsStream("/masked_dataset.csv");
        InputStream targetInputStream = this.getClass().getResourceAsStream("/florida_link.txt");

        IPVDataset source = IPVDataset.load(sourceInputStream, false, ',', '"', false);

        Collection<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(4, 4, "*", false, -1)); //YOB
        linkInformation.add(new LinkInfo(5, 2, "*", true, 3)); //ZIP
        linkInformation.add(new LinkInfo(6, 3, "*", false, -1)); //Gender
        linkInformation.add(new LinkInfo(7, 5, "*", false, -1)); //Race

        AnonymizedDatasetLinker linker = new AnonymizedDatasetLinker(targetInputStream, linkInformation);
        List<Integer> linkResults = linker.matchesPerRecord(source, linkInformation);

        for(Integer result: linkResults) {
            System.out.println(result);
        }
    }

    @Test
    @Disabled
    public void saveResults() throws Exception {
        InputStream sourceInputStream = this.getClass().getResourceAsStream("/masked_dataset.csv");
        InputStream targetInputStream = this.getClass().getResourceAsStream("/florida_link.txt");

        IPVDataset source = IPVDataset.load(sourceInputStream, false, ',', '"', false);

        Collection<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(4, 4, "*", false, -1)); //YOB
        linkInformation.add(new LinkInfo(5, 2, "*", true, 3)); //ZIP
        linkInformation.add(new LinkInfo(6, 3, "*", false, -1)); //Gender
        linkInformation.add(new LinkInfo(7, 5, "*", false, -1)); //Race

        AnonymizedDatasetLinker linker = new AnonymizedDatasetLinker(targetInputStream, linkInformation);

        System.out.println("Indexing done");

        List<Integer> linkResults = null;// linker.matchesPerRecord(source, linkInformation);

        System.out.println("Linking against masked done");

//        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("/tmp/masked_linked.csv")))) {
//            for (Integer result : linkResults) {
//                pw.println(result);
//            }
//        }

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ICDv9), ColumnType.QUASI));

//        sourceInputStream = this.getClass().getResourceAsStream("/anonymised_dataset.csv");
//        sourceInputStream = this.getClass().getResourceAsStream("/new_anonymised.csv");
        sourceInputStream = new FileInputStream("/tmp/received_datasettrue.csv"); //this.getClass().getResourceAsStream("/received_datasettrue.csv");
        source = IPVDataset.load(sourceInputStream, false, ',', '"', false);

        linkResults = linker.matchesPerRecord(source, linkInformation, columnInformation);

        System.out.println("Linking against anonymised done");

        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("/tmp/anonymised_linked.csv")))) {
            for (Integer result : linkResults) {
                pw.println(result);
            }
        }
    }

    @Test
    @Disabled
    public void testAnonymizedDatasetFullScale() throws Exception {
        InputStream sourceInputStream = this.getClass().getResourceAsStream("/anonymised_dataset.csv");
        InputStream targetInputStream = this.getClass().getResourceAsStream("/florida_link.txt");

        IPVDataset source = IPVDataset.load(sourceInputStream, false, ',', '"', false);

        Collection<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(4, 4)); //YOB
        linkInformation.add(new LinkInfo(5, 2, "*", true, 3)); //ZIP
        linkInformation.add(new LinkInfo(6, 3)); //Gender
        linkInformation.add(new LinkInfo(7, 5)); //Race

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ICDv9), ColumnType.QUASI));

        long start = System.currentTimeMillis();
        AnonymizedDatasetLinker linker = new AnonymizedDatasetLinker(targetInputStream, linkInformation);

        System.out.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();

        List<Integer> linkResults = linker.matchesPerRecord(source, linkInformation, columnInformation);

        System.out.println(System.currentTimeMillis() - start);

        System.out.println(linkResults.size());
    }
}
