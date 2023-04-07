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

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Histogram;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TClosenessTest {
    @Test
    @Disabled
    public void testHierarchicalDistance() {
        List<String> partitionValues = Arrays.asList("gastric ulcer", "gastritis", "stomach cancer");
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("gastric ulcer", "stomach diseases", "digestive system diseases", "all diseases");
        hierarchy.add("stomach cancer", "stomach diseases", "digestive system diseases", "all diseases");
        hierarchy.add("gastritis", "stomach diseases", "digestive system diseases", "all diseases");
        
        hierarchy.add("colitis", "colon diseases", "digestive system diseases", "all diseases");
        hierarchy.add("colon cancer", "colon diseases", "digestive system diseases", "all diseases");
        
        hierarchy.add("flu", "respiratory infection", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("pneumonia", "respiratory infection", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("bronchitis", "respiratory infection", "vascular and respiratory diseases", "all diseases");
        
        hierarchy.add("pulmonary edema", "vascular diseases", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("pulmonary embolism", "vascular diseases", "vascular and respiratory diseases", "all diseases");
        
        Histogram<String> totalHistogram =
                Histogram.createHistogram(Arrays.asList("gastric ulcer", "gastritis", "stomach cancer", "gastritis", "flu", "bronchitis",
                        "bronchitis", "pneumonia", "stomach cancer"));
        long totalCount = 9L;
        
        double distance = TCloseness.equalDistance(partitionValues, totalHistogram, totalCount);
        System.out.println(distance);
    }

    @Test
    public void testHierarchicalDistance2() {
        List<String> partitionValues = Arrays.asList("gastric ulcer", "stomach cancer", "pneumonia");
        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("gastric ulcer", "stomach diseases", "digestive system diseases", "all diseases");
        hierarchy.add("stomach cancer", "stomach diseases", "digestive system diseases", "all diseases");
        hierarchy.add("gastritis", "stomach diseases", "digestive system diseases", "all diseases");
        hierarchy.add("colitis", "colon diseases", "digestive system diseases", "all diseases");
        hierarchy.add("colon cancer", "colon diseases", "digestive system diseases", "all diseases");
        hierarchy.add("flu", "respiratory infection", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("pneumonia", "respiratory infection", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("bronchitis", "respiratory infection", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("pulmonary edema", "vascular diseases", "vascular and respiratory diseases", "all diseases");
        hierarchy.add("pulmonary embolism", "vascular diseases", "vascular and respiratory diseases", "all diseases");

        Histogram<String> totalHistogram =
                Histogram.createHistogram(Arrays.asList("gastric ulcer", "gastritis", "stomach cancer", "gastritis", "flu", "bronchitis",
                        "bronchitis", "pneumonia", "stomach cancer"));
        long totalCount = 9L;

        double distance = TCloseness.equalDistance(partitionValues, totalHistogram, totalCount);
    }
    
    @Test
    public void testNumeric() throws Exception {
        try (InputStream input = TClosenessTest.class.getResourceAsStream("/100_with_id.csv")) {
            IPVDataset dataset = IPVDataset.load(input, false, ',', '"', false);

            List<ColumnInformation> columnInformationList = new ArrayList<>();
            columnInformationList.add(new NumericalRange(Collections.emptyList(), ColumnType.SENSITIVE));
            for (int i = 1; i < dataset.getNumberOfColumns(); i++) {
                columnInformationList.add(new DefaultColumnInformation());
            }

            List<Integer> sensitiveColumns = List.of(0);

            TCloseness tCloseness = new TCloseness(0.5);
            tCloseness.initialize(dataset, columnInformationList);

            Partition partition = new InMemoryPartition(dataset.getValues());
            assertTrue(tCloseness.check(partition, sensitiveColumns));
        }
    }

    @Test
    public void testCategorical() throws Exception {
        try (InputStream input = TClosenessTest.class.getResourceAsStream("/100_with_id.csv")) {
            IPVDataset dataset = IPVDataset.load(input, false, ',', '"', false);

            List<ColumnInformation> columnInformationList = new ArrayList<>();
            columnInformationList.add(new CategoricalInformation(null, ColumnType.SENSITIVE));
            for (int i = 1; i < dataset.getNumberOfColumns(); i++) {
                columnInformationList.add(new DefaultColumnInformation());
            }

            List<Integer> sensitiveColumns = List.of(0);

            TCloseness tCloseness = new TCloseness(0.5);
            tCloseness.initialize(dataset, columnInformationList);

            Partition partition = new InMemoryPartition(dataset.getValues());
            assertTrue(tCloseness.check(partition, sensitiveColumns));
        }
    }

    @Test
    public void testNumericFail() throws Exception {
        try (InputStream input = TClosenessTest.class.getResourceAsStream("/tcloseness_fail.csv");
             InputStream partitionInputStream = TClosenessTest.class.getResourceAsStream("/tcloseness_fail_partition.csv")) {
            IPVDataset dataset = IPVDataset.load(input, false, ',', '"', false);

            List<ColumnInformation> columnInformationList = new ArrayList<>();
            columnInformationList.add(new NumericalRange(Collections.emptyList(), ColumnType.SENSITIVE));
            for (int i = 1; i < dataset.getNumberOfColumns(); i++) {
                columnInformationList.add(new DefaultColumnInformation());
            }

            List<Integer> sensitiveColumns = List.of(0);

            TCloseness tCloseness = new TCloseness(0.1);
            tCloseness.initialize(dataset, columnInformationList);

            Partition partition = new InMemoryPartition(IPVDataset.load(partitionInputStream, false, ',', '"', false));
            assertFalse(tCloseness.check(partition, sensitiveColumns));
        }
    }


    @Test
    public void testOrderBasedDistance() {
        List<Double> partition = Arrays.asList(6.0, 8.0, 11.0);
        List<Double> totalOrdered = Arrays.asList(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0);
        
        double distance = TCloseness.orderBasedDistance(partition, totalOrdered, Histogram.createHistogram(totalOrdered), 9);
        assertEquals((1.0/8.0) * (12.0/9.0), distance, 0.000001);
    }

    @Test
    public void testOrderBasedDistance2() {
        List<Double> partition = Arrays.asList(3.0, 4.0, 5.0);
        List<Double> totalOrdered = Arrays.asList(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0);

        double distance = TCloseness.orderBasedDistance(partition, totalOrdered, Histogram.createHistogram(totalOrdered), 9);
        assertEquals((1.0/8.0) * (27.0/9.0), distance, 0.000001);
    }
}

