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
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformationGenerator;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumericalPrecisionTest {

    @Test
    public void testNumerical() throws Exception {
        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testNumericOriginal.csv"), false, ',', '"', false);
        IPVDataset anonymized = IPVDataset.load(getClass().getResourceAsStream("/testNumericAnonymized.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(original, 0, ColumnType.QUASI));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(original, 1, ColumnType.QUASI));

        double precision = (new NumericalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

        //the precision of column 1 is (1 + 1 + 1 + 1) / 13 / 4) = 1/13 = 0.0769...
        //the precision of column 2 is (5 + 5 + 5 + 5) / 15 / 4) = 1/3 = 0.3333...
        // The total precision is (1/13 + 1/3)/2 = 0.205128...

        assertEquals((1/13.0 + 1/3.0)/2, precision, 0.00001);
    }
}

