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


public class KRatioMetricTest {
    @Test
    public void generationOfPartitionsForLinkingWithNoColumnInformationThrows() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(Collections.emptyList(), null, false);

            new KRatioMetric().initialize(dataset, dataset, Collections.emptyList(), 10, Collections.singletonMap(KRatioMetric.GAMMA, Double.toString(0.01)));
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

            new KRatioMetric().initialize(dataset, dataset, columnInformations, 10, Collections.singletonMap(KRatioMetric.GAMMA, Double.toString(0.01)));
        });
    }
}