/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.GenderHierarchy;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetricOptions;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FKRatioMetricTest {

    @Test
    public void generationOfPartitionsForLinkingWithNoColumnInformationThrows() {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(Collections.emptyList(), null, false);

            new FKRatioMetric().initialize(dataset, dataset, Collections.emptyList(), 10, Collections.singletonMap(FKRatioMetric.POPULATION, Integer.toString(POPULATION)));
        });
    }

    @Test
    public void generationOfPartitionsForLinkingWithLinkColumnInformationThrows() {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(Collections.emptyList(), null, false);
            List<ColumnInformation> columnInformation = new ArrayList<>();

            for (int i = 0; i < 10; ++i) {
                columnInformation.add(new CategoricalInformation(GenderHierarchy.getInstance(), ColumnType.QUASI));
            }

            new FKRatioMetric().initialize(dataset, dataset, columnInformation, 10, Collections.singletonMap(FKRatioMetric.POPULATION, Integer.toString(POPULATION)));
        });
    }

    
    private static final int POPULATION = 13_511_855;

    @Test
    @Disabled
    public void testFKRatioMetric() throws Exception {
        try (InputStream inputStream = FKRatioMetricTest.class.getResourceAsStream("/random1.txt")) {
            IPVDataset original = IPVDataset.load(inputStream, false, ',', '"', false);

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation()); //zipcode
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());

//        final List<Double> suppressions = Arrays.asList(5.0, 10.0, 15.0, 20.0);
            final List<Double> suppressions = List.of(5.0);

            for (int k = 2; k < 100; k += 1) {
                List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
                privacyConstraints.add(new KAnonymity(k));

                for (final double suppression : suppressions) {
                    OLAOptions olaOptions = new OLAOptions(suppression);
                    OLA ola = new OLA();
                    ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

                    IPVDataset anonymized = ola.apply();

                    FKRatioMetric fkRatioMetric = new FKRatioMetric();

                    fkRatioMetric.initialize(original, anonymized, ola.getColumnInformationList(), k, Collections.singletonMap(FKRatioMetric.POPULATION, Integer.toString(POPULATION)));

                    for (int i = 0; i < 30; ++i) {
                        System.out.println("::: " + i + " " + k + " " + suppression + " " + ola.reportSuppressionRate() + " " + fkRatioMetric.report());
                    }
                }
            }
        }
    }
}
