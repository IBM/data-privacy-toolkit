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

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.ola.LatticeNode;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoricalPrecisionTest {

    @Test
    public void testCategorical() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);


            List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

            MaterializedHierarchy hierarchy = new MaterializedHierarchy();
            hierarchy.add("Scientist", "Worker");
            hierarchy.add("Manager", "Worker");
            hierarchy.add("Director", "Worker");

            CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
            columnInformationList.add(categoricalInformation);

            double precision = (new CategoricalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

            //the precision is (0 + 0 + 1 + 1) / 4 * 1) = 2/4 = 0.5 = 0.5
            assertEquals(0.5, precision, 0.00001);
        }
    }

    @Test
    public void testCategoricalSuppression() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);
            List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

            MaterializedHierarchy hierarchy = new MaterializedHierarchy();
            hierarchy.add("Scientist", "Worker");
            hierarchy.add("Manager", "Worker");
            hierarchy.add("Director", "Worker");

            CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
            columnInformationList.add(categoricalInformation);

            double precision = (new CategoricalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

            //the precision is (0 + 0 ) = 0
            // plus two suppressed : 1 + 1
            // total = 2/4
            assertEquals(0.5, precision, Double.MIN_VALUE);
        }
    }

    @Test
    public void testCategoricalWithWeightsSingleColumn() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);
            List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

            MaterializedHierarchy hierarchy = new MaterializedHierarchy();
            hierarchy.add("Scientist", "Worker");
            hierarchy.add("Manager", "Worker");
            hierarchy.add("Director", "Worker");

            double weight = 0.5;
            CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI, weight);
            columnInformationList.add(categoricalInformation);

            double precision = (new CategoricalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

            //the precision is (0 + 0 + 1 + 1) / 4 * 1) = 2/4 = 0.5 = 0.5
            assertEquals(0.25, precision, Double.MIN_VALUE);
        }
    }

    @Test
    public void testCategoricalWithWeightsMultipleColumns() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCPWeightsOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCPWeightsAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);

            List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

            MaterializedHierarchy hierarchy1 = new MaterializedHierarchy();
            hierarchy1.add("Scientist", "Worker");
            hierarchy1.add("Manager", "Worker");
            hierarchy1.add("Director", "Worker");

            double weightFirstColumn = 1.0;
            CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy1, ColumnType.QUASI, weightFirstColumn);
            columnInformationList.add(categoricalInformation);

            double weightSecondColumn = 0.5;
            MaterializedHierarchy hierarchy2 = new MaterializedHierarchy();
            hierarchy2.add("Married", "*");
            hierarchy2.add("Single", "*");
            hierarchy2.add("Divorced", "*");
            CategoricalInformation categoricalInformation2 = new CategoricalInformation(hierarchy2, ColumnType.QUASI, weightSecondColumn);
            columnInformationList.add(categoricalInformation2);

            double precision = (new CategoricalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

            //first column is (0 + 0 + 1 + 1) / 8 * 1) = 2/8 = 0.25
            //second column is (0 + 0 + 1 + 1) / 8 * 0.5 = 2/8 * 0.5 = 0.125
            assertEquals(0.25 + 0.125, precision, Double.MIN_VALUE);
        }
    }

    @Test
    public void testCategoricalPerColumn() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCPWeightsOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCPWeightsAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);

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

            CategoricalPrecision cp = new CategoricalPrecision();
            cp.initialize(original, anonymized, null, null, columnInformationList, null);

            //first column is (0 + 0 + 1 + 1) / 8 * 1) = 2/8 = 0.25
            //second column is (0 + 0 + 1 + 1) / 8  = 2/8  = 0.25
            assertEquals(0.25 + 0.25, cp.report(), Double.MIN_VALUE);

            List<InformationLossResult> perColumn = cp.reportPerQuasiColumn();
            assertEquals(2, perColumn.size());

            //first column is (0 + 0 + 1 + 1) / 4 * 1) = 2/4 = 0.5
            //second column is (0 + 0 + 1 + 1) / 4  = 2/4  = 0.5
            assertEquals(0.5, perColumn.get(0).getValue(), Double.MIN_VALUE);
            assertEquals(0.5, perColumn.get(1).getValue(), Double.MIN_VALUE);
        }
    }

    @Test
    public void testCategoricalWithWeightsSingleColumnZero() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/testCategoricalAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);
            List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

            MaterializedHierarchy hierarchy = new MaterializedHierarchy();
            hierarchy.add("Scientist", "Worker");
            hierarchy.add("Manager", "Worker");
            hierarchy.add("Director", "Worker");

            double weight = 0.0;
            CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI, weight);
            columnInformationList.add(categoricalInformation);

            double precision = (new CategoricalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

            //the precision is (0 + 0 + 1 + 1) / 4 * 1) = 2/4 = 0.5 = 0.5
            assertEquals(0.0, precision, Double.MIN_VALUE);
        }
    }

    @Test
    public void testCategoricalWithFactory() throws Exception {
        try (
                InputStream originalIS = CategoricalPrecisionTest.class.getResourceAsStream("/cityOriginal.csv");
                InputStream anonymizedIS = CategoricalPrecisionTest.class.getResourceAsStream("/cityAnonymized.csv")
        ) {
            IPVDataset original = IPVDataset.load(originalIS, false, ',', '"', false);

            IPVDataset anonymized = IPVDataset.load(anonymizedIS, false, ',', '"', false);
            List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

            GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.CITY);
            CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
            columnInformationList.add(categoricalInformation);

            double precision = (new CategoricalPrecision().initialize(original, anonymized, null, null, columnInformationList, null)).report();

            //the precision is ((2/3 + 2/3) / 2 * 1) = 2/3 =  0.666
            assertEquals(2.0 / 3.0, precision, 0.00001);
        }
    }

    @Test
    public void testCategoricalWithOLA() throws Exception {
        try (InputStream inputStream = CategoricalPrecisionTest.class.getResourceAsStream("/random1_height_weight.txt")) {
            IPVDataset original = IPVDataset.load(inputStream, false, ',', '"', false);

            GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
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
            columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());

            int k = 10;
            double suppression = 10.0;

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(suppression);
            ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();

            LatticeNode node = ola.reportBestNode();

            CategoricalPrecision cp = new CategoricalPrecision();
            cp.initialize(original, anonymized, null, null, columnInformation, null);

            List<InformationLossResult> lossResults = cp.reportPerQuasiColumn();

            int suppressedRows = original.getNumberOfRows() - anonymized.getNumberOfRows();
            int nonSuppressedRows = anonymized.getNumberOfRows();

            int yobLevel = node.getValues()[0];
            double yobLoss = lossResults.get(0).getValue();
            double estimatedYobLoss = (((double) yobLevel) * nonSuppressedRows + suppressedRows * 1.0) / (double) original.getNumberOfRows();
            assertEquals(estimatedYobLoss, yobLoss, 0.0001);

            int heightLevel = node.getValues()[4];
            double heightLoss = lossResults.get(4).getValue();
            int heightHierarchyLevel = heightHierarchy.getHeight();
            double estimatedHeightLoss = (((double) heightLevel / (heightHierarchyLevel - 1.0)) * nonSuppressedRows + suppressedRows * 1.0) / (double) original.getNumberOfRows();
            assertEquals(estimatedHeightLoss, heightLoss, 0.0001);
        }
    }

    @Test
    public void testCategoricalWithOLAWithTransformationLevels() throws Exception {
        try (InputStream inputStream = CategoricalPrecisionTest.class.getResourceAsStream("/random1_height_weight.txt")) {
            IPVDataset original = IPVDataset.load(inputStream, false, ',', '"', false);

            GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
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
            columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI));
            columnInformation.add(new DefaultColumnInformation());

            int k = 10;
            double suppression = 10.0;

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(suppression);
            ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();

            LatticeNode node = ola.reportBestNode();

            CategoricalPrecision cp = new CategoricalPrecision();
            cp.initialize(original, anonymized, null, null, columnInformation, node.getValues(), null);

            List<InformationLossResult> lossResults = cp.reportPerQuasiColumn();

            int suppressedRows = original.getNumberOfRows() - anonymized.getNumberOfRows();
            int nonSuppressedRows = anonymized.getNumberOfRows();

            int yobLevel = node.getValues()[0];
            double yobLoss = lossResults.get(0).getValue();
            double estimatedYobLoss = (((double) yobLevel) * nonSuppressedRows + suppressedRows * 1.0) / (double) original.getNumberOfRows();
            assertEquals(estimatedYobLoss, yobLoss, 0.0001);

            int heightLevel = node.getValues()[4];
            double heightLoss = lossResults.get(4).getValue();
            int heightHierarchyLevel = heightHierarchy.getHeight();
            double estimatedHeightLoss = (((double) heightLevel / (heightHierarchyLevel - 1.0)) * nonSuppressedRows + suppressedRows * 1.0) / (double) original.getNumberOfRows();
            assertEquals(estimatedHeightLoss, heightLoss, 0.0001);
        }
    }

    @Test
    public void testCategoricalWithOLAWithTransformationLevelsWithWeights() throws Exception {
        try (InputStream inputStream = CategoricalPrecisionTest.class.getResourceAsStream("/random1_height_weight.txt")) {
            IPVDataset original = IPVDataset.load(inputStream, false, ',', '"', false);

            GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
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
            columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI, 0.5));
            columnInformation.add(new DefaultColumnInformation());

            int k = 10;
            double suppression = 10.0;

            List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
            privacyConstraints.add(new KAnonymity(k));

            OLA ola = new OLA();
            OLAOptions olaOptions = new OLAOptions(suppression);
            ola.initialize(original, columnInformation, privacyConstraints, olaOptions);

            IPVDataset anonymized = ola.apply();

            LatticeNode node = ola.reportBestNode();

            CategoricalPrecision cp = new CategoricalPrecision();
            cp.initialize(original, anonymized, null, null, columnInformation, node.getValues(), null);

            List<InformationLossResult> lossResults = cp.reportPerQuasiColumn();

            int suppressedRows = original.getNumberOfRows() - anonymized.getNumberOfRows();
            int nonSuppressedRows = anonymized.getNumberOfRows();

            int yobLevel = node.getValues()[0];
            double yobLoss = lossResults.get(0).getValue();
            double estimatedYobLoss = (((double) yobLevel) * nonSuppressedRows + suppressedRows * 1.0) / (double) original.getNumberOfRows();
            assertEquals(estimatedYobLoss, yobLoss, 0.0001);

            int heightLevel = node.getValues()[4];
            double heightLoss = lossResults.get(4).getValue();
            int heightHierarchyLevel = heightHierarchy.getHeight();
            double estimatedHeightLoss = 0.5 * (((double) heightLevel / (heightHierarchyLevel - 1.0)) * nonSuppressedRows + suppressedRows * 1.0) / (double) original.getNumberOfRows();
            assertEquals(estimatedHeightLoss, heightLoss, 0.0001);
        }
    }
}

