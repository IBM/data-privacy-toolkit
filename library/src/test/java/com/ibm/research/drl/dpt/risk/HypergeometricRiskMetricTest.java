/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.GenderHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class HypergeometricRiskMetricTest {
    private static final int POPULATION = 13_511_855;

    @Test
    public void generationOfPartitionsForLinkingWithNoColumnInformationThrows() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(
                    Collections.emptyList(),
                    null,
                    false
            );

            new HypergeometricRiskMetric().initialize(dataset, dataset, Collections.emptyList(), 10, Collections.singletonMap(HypergeometricRiskMetric.N, Integer.toString(POPULATION)));
        });
    }

    @Test
    public void generationOfPartitionsForLinkingWithLinkColumnInformationThrows() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(Collections.emptyList(), null, false);
            List<ColumnInformation> columnInformations = new ArrayList<>();

            for (int i = 0; i < 10; ++i) {
                columnInformations.add(new CategoricalInformation(GenderHierarchy.getInstance(), ColumnType.QUASI));
            }

            new HypergeometricRiskMetric().initialize(dataset, dataset, columnInformations, 10, Collections.singletonMap(HypergeometricRiskMetric.N, Integer.toString(POPULATION)));
        });
    }
}