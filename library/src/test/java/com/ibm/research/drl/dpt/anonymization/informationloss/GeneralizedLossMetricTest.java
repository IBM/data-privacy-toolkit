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

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformationGenerator;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralizedLossMetricTest {

    @Test
    public void testNumeric() throws Exception {

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testNumericOriginal.csv"), false, ',', '"', false);
        IPVDataset anonymized = IPVDataset.load(getClass().getResourceAsStream("/testNumericAnonymized.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(original, 0, ColumnType.QUASI));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(original, 1, ColumnType.QUASI));

        Double glm = (new GeneralizedLossMetric().initialize(original, anonymized, null, null, columnInformationList, null)).report();

        //the GLM is 1/13 + 5/15 ~= 0.076 + 0.333 ~= 0.41

        assertEquals((1.0 / 13.0 + 5.0 / 15.0), glm);
    }

    @Test
    public void testNumericWithWeights() throws Exception {

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testNumericOriginal.csv"), false, ',', '"', false);

        IPVDataset anonymized = IPVDataset.load(getClass().getResourceAsStream("/testNumericAnonymized.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(original, 0, ColumnType.QUASI, 1.0));
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(original, 1, ColumnType.QUASI, 0.5));

        double glm = (new GeneralizedLossMetric().initialize(original, anonymized, null, null, columnInformationList, null)).report();

        //the GLM is 1/13 + 5/15 ~= 0.076 + 0.333*0.5

        assertEquals(1.0/13.0 + 0.5*5.0/15.0, glm, Double.MIN_VALUE);
    }

    @Test
    public void testCategorical() throws Exception {

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testCategoricalOriginal.csv"), false, ',', '"', false);

        IPVDataset anonymized = IPVDataset.load(getClass().getResourceAsStream("/testCategoricalAnonymized.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Worker", "*");
        hierarchy.add("Manager", "Worker", "*");
        hierarchy.add("Director", "Worker", "*");

        CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
        columnInformationList.add(categoricalInformation);

        Double glm = (new GeneralizedLossMetric().initialize(original, anonymized, null, null, columnInformationList, null)).report();

        //the GLM is (0 + 0 + 1 + 1) / 4 = 0.5
        assertEquals(0.5, glm);
    }


    @Test
    public void testGLMPerColumn() throws Exception {

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testCPWeightsOriginal.csv"), false, ',', '"', false);

        IPVDataset anonymized = IPVDataset.load(getClass().getResourceAsStream("/testCPWeightsAnonymized.csv"), false, ',', '"', false);


        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        MaterializedHierarchy hierarchy1 = new MaterializedHierarchy();
        hierarchy1.add("Scientist", "Worker");
        hierarchy1.add("Manager", "Worker");
        hierarchy1.add("Director", "Worker");

        double weightFirstColumn = 1.0;
        CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy1, ColumnType.QUASI, weightFirstColumn);
        columnInformationList.add(categoricalInformation);

        double weightSecondColumn = 1.0;
        MaterializedHierarchy hierarchy2 = new MaterializedHierarchy();
        hierarchy2.add("Married", "*");
        hierarchy2.add("Single", "*");
        hierarchy2.add("Divorced", "*");
        CategoricalInformation categoricalInformation2 = new CategoricalInformation(hierarchy2, ColumnType.QUASI, weightSecondColumn);
        columnInformationList.add(categoricalInformation2);

        GeneralizedLossMetric glm = new GeneralizedLossMetric();
        glm.initialize(original, anonymized, null, null, columnInformationList, null);

        //first column is (0 + 0 + 1 + 1) / 4 * 1) = 2/4 = 0.5
        //second column is (0 + 0 + 1 + 1) / 4  = 2/4  = 0.5
        assertEquals(0.5 + 0.5, glm.report(), Double.MIN_VALUE);

        List<InformationLossResult> perColumn = glm.reportPerQuasiColumn();
        assertEquals(2, perColumn.size());

        //first column is (0 + 0 + 1 + 1) / 4 * 1) = 2/4 = 0.5
        //second column is (0 + 0 + 1 + 1) / 4  = 2/4  = 0.5
        assertEquals(0.5, perColumn.get(0).getValue(), Double.MIN_VALUE);
        assertEquals(0.5, perColumn.get(1).getValue(), Double.MIN_VALUE);
    }
}

