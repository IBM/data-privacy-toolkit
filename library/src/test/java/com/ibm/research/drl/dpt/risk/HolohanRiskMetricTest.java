/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;


public class HolohanRiskMetricTest {
    
    @Test
    @Disabled
    public void testAccuracy() throws Exception {
        final int POPULATION = 13511000;

        InputStream sample = this.getClass().getResourceAsStream("/florida_sample_0.01.txt");
        IPVDataset sampleDataset = IPVDataset.load(sample, false, ',', '"', false);

        System.out.println("loading done");
        int k = 10;

        HolohanRiskMetric risk = new HolohanRiskMetric();

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.ZIPCODE), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("M", "F", "U")), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI, true));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "9")),
                ColumnType.QUASI, true));
        System.out.println("columnInformation done");

        double suppression = 5.0;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLAOptions olaOptions = new OLAOptions(suppression);
        OLA ola = new OLA();
        ola.initialize(sampleDataset, columnInformation, privacyConstraints, olaOptions);

        IPVDataset anonymizedSampleDataset = ola.apply();

        System.out.println("best node: " + ola.reportBestNode());

        Map<String, String> options = new HashMap<>(2);
        options.put(HolohanRiskMetric.POPULATION, Integer.toString(POPULATION));
        risk.initialize(null, anonymizedSampleDataset, columnInformation, k, options);

        System.out.println(risk.report());
    }
}