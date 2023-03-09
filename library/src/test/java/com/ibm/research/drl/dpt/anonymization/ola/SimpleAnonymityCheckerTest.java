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
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleAnonymityCheckerTest {

    @Test
    public void testSuppressionRateCalculation() throws Exception {
        int k = 3;

        MaterializedHierarchy dateHierarchy = new MaterializedHierarchy();
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        MaterializedHierarchy genderHierarchy = new MaterializedHierarchy();
        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        MaterializedHierarchy ageHierarchy = new MaterializedHierarchy();
        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));


        final IPVDataset original;
        try (InputStream inputStream = getClass().getResourceAsStream("/testOLA.csv")) {
            original = IPVDataset.load(inputStream, false, ',', '"', false);
        }

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        SimpleAnonymityChecker simpleAnonymityChecker = new SimpleAnonymityChecker(original, columnInformationList, privacyConstraints);
        LatticeNode node = new LatticeNode(new int[]{0, 0, 0});

        double suppressionRate = simpleAnonymityChecker.calculateSuppressionRate(node);
        //total records : 10,  only 3 rows are 3-anonymous
        assertEquals(70.0d, suppressionRate, 0.1d);

        node = new LatticeNode(new int[]{0, 0, 1});
        suppressionRate = simpleAnonymityChecker.calculateSuppressionRate(node);
        /*
        we generalize age by 1 level. the new dataset should look like:
            01/01/2008,M,15-19 x
            01/01/2008,M,15-19 x
            01/01/2008,M,15-19 x
            01/01/2008,M,10-14
            01/01/2008,M,15-19 x
            02/01/2008,F,15-19
            02/01/2008,F,20-24 x
            02/01/2008,F,20-24 x
            02/01/2008,F,20-24 x
            01/01/2008,M,20-24

            total records: 10, 3-anonymous rows: 7
        */
        assertEquals(30.0d, suppressionRate, 0.1d);

        node = new LatticeNode(new int[]{2, 1, 4});
        suppressionRate = simpleAnonymityChecker.calculateSuppressionRate(node);
        //we generalize everything, suppression rate is 0
        assertEquals(0.0d, suppressionRate, 0.001d);

        node = new LatticeNode(new int[]{1, 0, 0});
        /* dataset looks like:
            JAN_2008,M,18, x
            JAN_2008,M,18, x
            JAN_2008,M,18, x
            JAN_2008,M,13,
            JAN_2008,M,19,
            JAN_2008,F,18,
            JAN_2008,F,22,
            JAN_2008,F,23,
            JAN_2008,F,21,
            JAN_2008,M,22,
         */
        suppressionRate = simpleAnonymityChecker.calculateSuppressionRate(node);
        assertEquals(70.0d, suppressionRate, 0.1d);

        node = new LatticeNode(new int[]{1, 1, 1});
        suppressionRate = simpleAnonymityChecker.calculateSuppressionRate(node);
    }
}

