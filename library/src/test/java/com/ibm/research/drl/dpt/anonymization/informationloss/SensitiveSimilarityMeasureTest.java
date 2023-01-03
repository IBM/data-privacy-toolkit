/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SensitiveSimilarityMeasureTest {

    private IPVDataset loadDataset() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/random1.txt")) {
            return IPVDataset.load(inputStream, false, ',', '"', false);
        }
    }

    private List<ColumnInformation> buildColumnInformation() {
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation()); // column id
        columnInformation.add(new DefaultColumnInformation()); // name
        columnInformation.add(new DefaultColumnInformation()); // surname
        columnInformation.add(new DefaultColumnInformation()); // email
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI)); // yob
        columnInformation.add(new DefaultColumnInformation()); // zip
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI)); // gender
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI)); // race
        columnInformation.add(new DefaultColumnInformation()); // religion
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI)); // marital status
        columnInformation.add(new SensitiveColumnInformation()); // icd

        return columnInformation;
    }

    @Test
    public void dummyDatasetForDebug() {
        final IPVDataset dataset = new IPVDataset(Arrays.asList(
                List.of("A"),
                List.of("A"),
                List.of("A"),
                List.of("B"),
                List.of("B"),
                List.of("B")
        ), null, false);

        Double report = new SensitiveSimilarityMeasure().initialize(dataset, null, null, Collections.singletonList(new Partition() {
            @Override
            public int size() {
                return dataset.getNumberOfRows();
            }

            @Override
            public double getNormalizedWidth(int qidColumn) {
                return 0;
            }

            @Override
            public IPVDataset getMember() {
                return dataset;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public void setAnonymous(boolean value) {

            }
        }), Collections.singletonList(new SensitiveColumnInformation()), null).report();

        assertNotNull(report);
        System.out.println(report);
    }

    @Test
    public void dummyDatasetForDebug2() throws Exception {
        final IPVDataset dataset = new IPVDataset(Arrays.asList(
                List.of("A"),
                List.of("A"),
                List.of("A"),
                List.of("B"),
                List.of("B"),
                List.of("B")
        ), null, false);

        List<List<String>> lists = new ArrayList<>();
        final IPVDataset anonymized = new IPVDataset(lists, null, false);

        Double report = new SensitiveSimilarityMeasure().initialize(dataset, anonymized, null, Collections.singletonList(new Partition() {
            @Override
            public int size() {
                return anonymized.getNumberOfRows();
            }

            @Override
            public double getNormalizedWidth(int qidColumn) {
                return 0;
            }

            @Override
            public IPVDataset getMember() {
                return anonymized;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public void setAnonymous(boolean value) {

            }
        }), List.of(new SensitiveColumnInformation()), null).report();

        assertNotNull(report);
    }

    @Test
    public void metricOnOriginalNotAnonymizedReturnsZero() throws Exception {
        final IPVDataset anonymized = loadDataset();

        InformationMetric metric = new SensitiveSimilarityMeasure().initialize(loadDataset(), null, null, Collections.singletonList(new Partition() {
            @Override
            public int size() {
                return anonymized.getNumberOfRows();
            }

            @Override
            public double getNormalizedWidth(int qidColumn) {
                return 0;
            }

            @Override
            public IPVDataset getMember() {
                return anonymized;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public void setAnonymous(boolean value) {

            }
        }), buildColumnInformation(), null);

        assertEquals(metric.report(), 0.0, Double.MIN_VALUE);
    }

    @Disabled
    @Test
    public void dummyComplete() throws Exception {
        for (int k = 1; k <= 100; k += 10) {
            AnonymizationAlgorithm ola = new OLA().initialize(loadDataset(), buildColumnInformation(), Collections.singletonList(new KAnonymity(k)), new OLAOptions(5.0));

            IPVDataset anonymized = ola.apply();

            InformationMetric metric = new SensitiveSimilarityMeasure().initialize(loadDataset(), anonymized, ola.getOriginalPartitions(),
                    ola.getAnonymizedPartitions(), ola.getColumnInformationList(), null);

            System.out.println(k + " " + metric.report());
        }
    }
}