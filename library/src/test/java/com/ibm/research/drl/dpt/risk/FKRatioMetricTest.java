/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
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

import java.util.ArrayList;
import java.util.Arrays;
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
    public void generationOfPartitionsForLinkingWithLinkColumnInformationThrows() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(Collections.emptyList(), null, false);
            List<ColumnInformation> columnInformations = new ArrayList<>();

            for (int i = 0; i < 10; ++i) {
                columnInformations.add(new CategoricalInformation(GenderHierarchy.getInstance(), ColumnType.QUASI));
            }

            new FKRatioMetric().initialize(dataset, dataset, columnInformations, 10, Collections.singletonMap(FKRatioMetric.POPULATION, Integer.toString(POPULATION)));
        });
    }

    
    private static final int POPULATION = 13_511_855;

    @Test
    @Disabled
    public void testFKRatioMetric() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/random1.txt"), false, ',', '"', false);

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

        final InformationMetricOptions options = new InformationMetricOptions() {
            @Override
            public int getIntValue(String key) {
                return POPULATION;
            }

            @Override
            public String getStringValue(String key) {
                return null;
            }

            @Override
            public boolean getBooleanValue(String key) {
                return false;
            }
        };

        System.out.println("::: iteration k suppression ola.reportSuppressionRate() i_risk kRatio.report() categoricalPrecision.report()  ola.reportBestNode().toString()");

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
