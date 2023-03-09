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