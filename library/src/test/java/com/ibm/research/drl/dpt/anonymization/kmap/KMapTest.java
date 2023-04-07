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
package com.ibm.research.drl.dpt.anonymization.kmap;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.constraints.ReidentificationRisk;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.linkability.LinkInfo;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KMapTest {
    
    @Test
    public void testBasic() throws Exception {
        try (
                InputStream inputStream = KMapTest.class.getResourceAsStream("/kmap_test_input.csv");
                InputStream populationDataset = KMapTest.class.getResourceAsStream("/kmap_test_population.csv");
        ) {
            IPVDataset dataset = IPVDataset.load(inputStream, false, ',', '"', false);

            GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("22", "25", "27", "28", "29"));
            List<ColumnInformation> columnInformationList = new ArrayList<>();
            columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0));

            double riskThreshold = 0.25;

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            PrivacyConstraint rr = new ReidentificationRisk(populationDataset, linkInformation, columnInformationList, riskThreshold);
            privacyConstraints.add(rr);

            KMap kMap = new KMap();
            kMap.initialize(dataset, columnInformationList, privacyConstraints, new KMapOptions(0.0));

            IPVDataset anonymized = kMap.apply();

            assertEquals(7, anonymized.getNumberOfRows());
            assertEquals(0.0, kMap.reportSuppressionRate(), 0.0001);
        }
    }

    @Test
    public void testBasicWithSuppression() throws Exception {
        try (
            InputStream inputStream = KMapTest.class.getResourceAsStream("/kmap_test_input.csv");
            InputStream populationDataset = KMapTest.class.getResourceAsStream("/kmap_test_population.csv");
        ) {
            IPVDataset dataset = IPVDataset.load(inputStream, false, ',', '"', false);

            GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("22", "25", "27", "28", "29"));
            List<ColumnInformation> columnInformationList = new ArrayList<>();
            columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0));

            double riskThreshold = 0.25;

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            PrivacyConstraint rr = new ReidentificationRisk(populationDataset, linkInformation, columnInformationList, riskThreshold);
            privacyConstraints.add(rr);

            KMap kMap = new KMap();
            kMap.initialize(dataset, columnInformationList, privacyConstraints, new KMapOptions(30.0));

            IPVDataset anonymized = kMap.apply();

            assertEquals(5, anonymized.getNumberOfRows());
            assertEquals(200.0 / 7.0, kMap.reportSuppressionRate(), 0.0001);
        }
    }
}

