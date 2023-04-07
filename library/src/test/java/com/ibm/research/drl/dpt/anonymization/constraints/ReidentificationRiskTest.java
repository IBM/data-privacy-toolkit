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
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.InMemoryPartition;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.linkability.LinkInfo;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReidentificationRiskTest {
    
    @Test
    public void testRiskConstraintNoAnonymization() throws Exception {
        try (InputStream population = ReidentificationRiskTest.class.getResourceAsStream("/testRiskConstraintData.csv");) {
            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, "*"));

            double riskThreshold = 0.4;

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                    ColumnType.QUASI, true));
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());

            PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

            List<List<String>> anonValues = new ArrayList<>();
            anonValues.add(Arrays.asList("a", "2", "w"));
            anonValues.add(Arrays.asList("a", "2", "w"));
            anonValues.add(Arrays.asList("a", "2", "w"));

            Partition partition = new InMemoryPartition(anonValues);
            assertTrue(privacyConstraint.check(partition, null));
        }
    }

    @Test
    public void testRiskConstraintNoAnonymizationNoMatch() throws Exception {

        try (InputStream population = ReidentificationRiskTest.class.getResourceAsStream("/testRiskConstraintData.csv");) {

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, "*"));

            double riskThreshold = 0.2;

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                    ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());

            PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

            List<List<String>> anonValues = new ArrayList<>();
            anonValues.add(Arrays.asList("a", "2", "w"));

            Partition partition = new InMemoryPartition(anonValues);
            assertFalse(privacyConstraint.check(partition, null));
        }
    }

    @Test
    public void testRiskConstraintWithAnonymization() throws Exception {

        try (InputStream population = ReidentificationRiskTest.class.getResourceAsStream("/testRiskConstraintData.csv");) {

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, "*"));

            double riskThreshold = 0.4;

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                    ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());

            PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

            List<List<String>> anonValues = new ArrayList<>();
            anonValues.add(Arrays.asList("*", "2", "w"));

            Partition partition = new InMemoryPartition(anonValues);
            assertTrue(privacyConstraint.check(partition, null));
        }
    }

    @Test
    public void testRiskConstraintNoMatches() throws Exception {

        try (InputStream population = ReidentificationRiskTest.class.getResourceAsStream("/testRiskConstraintData.csv");) {

            List<LinkInfo> linkInformation = new ArrayList<>();
            linkInformation.add(new LinkInfo(0, 0, "*"));

            double riskThreshold = 0.4;

            List<ColumnInformation> columnInformation = new ArrayList<>();
            columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                    ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());
            columnInformation.add(new DefaultColumnInformation());

            PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

            List<List<String>> anonValues = new ArrayList<>();
            anonValues.add(Arrays.asList("b", "2", "w"));

            Partition partition = new InMemoryPartition(anonValues);
            assertTrue(privacyConstraint.check(partition, null));
        }
    }
}

