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

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.GenderHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.YOBHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZayatzEstimatorTest {

    private static final int POPULATION = 13_511_855;

    @Test
    public void generationOfPartitionsForLinkingWithNoColumnInformationThrows() throws Exception {
        assertThrows(RuntimeException.class, () -> {
            IPVDataset dataset = new IPVDataset(Collections.emptyList(), null, false);

            new ZayatzEstimator().initialize(dataset, dataset, Collections.emptyList(), 10, Collections.singletonMap(ZayatzEstimator.POPULATION, Integer.toString(POPULATION)));
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

            new ZayatzEstimator().initialize(dataset, dataset, columnInformations, 10,
                    Collections.singletonMap(ZayatzEstimator.POPULATION, Integer.toString(POPULATION)));
        });
    }

    public void printDataset(IPVDataset dataset) {
        for(int i = 0; i < dataset.getNumberOfRows(); i++) {
            for(int j = 0; j < dataset.getNumberOfColumns(); j++) {
                System.out.print(dataset.get(i, j) + ", ");
            }
            System.out.println();
        }
    }
    
    @Test
    public void testCorrectness() {
        /*
        N = 56372 be the population size, 22026 unique records
        n = 9383 be the sample size, 
        */
        
        int populationSize = 56372;
        int sampleSize = 9383;
        
        Map<Integer, Integer> originalEQSizes = new HashMap<>();

        originalEQSizes.put(1,22026);
        originalEQSizes.put(2,2954);
        originalEQSizes.put(3,1090);
        originalEQSizes.put(4,560);
        originalEQSizes.put(5,354);
        originalEQSizes.put(6,223);
        originalEQSizes.put(7,173);
        originalEQSizes.put(8,109);
        originalEQSizes.put(9,106);
        originalEQSizes.put(10,87);
        originalEQSizes.put(11,64);
        originalEQSizes.put(12,53);
        originalEQSizes.put(13,54);
        originalEQSizes.put(14,48);
        originalEQSizes.put(15,26);
        originalEQSizes.put(16,37);
        originalEQSizes.put(17,25);
        originalEQSizes.put(18,14);
        originalEQSizes.put(19,21);
        originalEQSizes.put(20,16);
        originalEQSizes.put(21,18);
        originalEQSizes.put(22,12);
        originalEQSizes.put(23,23);
        originalEQSizes.put(24,18);
        originalEQSizes.put(25,15);
        originalEQSizes.put(26,11);
        originalEQSizes.put(27,9);
        originalEQSizes.put(28,7);
        originalEQSizes.put(29,7);
        originalEQSizes.put(30,9);
        originalEQSizes.put(31,8);
        originalEQSizes.put(32,12);
        originalEQSizes.put(33,5);
        originalEQSizes.put(34,7);
        originalEQSizes.put(35,6);
        originalEQSizes.put(36,8);
        originalEQSizes.put(37,7);
        originalEQSizes.put(38,3);
        originalEQSizes.put(39,4);
        originalEQSizes.put(40,3);
        originalEQSizes.put(41,6);
        originalEQSizes.put(42,5);
        originalEQSizes.put(43,2);
        originalEQSizes.put(44,1);
        originalEQSizes.put(45,4);
        originalEQSizes.put(46,6);
        originalEQSizes.put(47,3);
        originalEQSizes.put(48,3);
        originalEQSizes.put(49,1);
        originalEQSizes.put(50,2);
        originalEQSizes.put(51,2);
        originalEQSizes.put(52,3);
        originalEQSizes.put(53,3);
        originalEQSizes.put(54,1);
        originalEQSizes.put(55,4);
        originalEQSizes.put(56,1);
        originalEQSizes.put(57,2);
        originalEQSizes.put(58,2);
        originalEQSizes.put(59,1);
        originalEQSizes.put(60,4);
        
        IPVDataset original = generateRandomData(originalEQSizes);
        IPVDataset sample = sampleDataset(original, sampleSize);

        //System.out.println("sample size:" + sample.getNumberOfRows());
        //System.out.println(sample.getNumberOfColumns());

        List<ColumnInformation> columnInformations = List.of(new CategoricalInformation(YOBHierarchy.getInstance(), ColumnType.QUASI, true));

        ZayatzEstimator estimator = new ZayatzEstimator();
        estimator.initialize(original, sample, columnInformations, 0, Collections.singletonMap(ZayatzEstimator.POPULATION, Integer.toString(populationSize)));

        double estimatedUniques = estimator.report();
        //System.out.println("estimated:" + estimatedUniques + "(" + (estimatedUniques / (double) populationSize) +
        //        "), real: 22026 (" + (22026.0 / (double) populationSize));

        assertEquals(estimatedUniques / (double) populationSize, (22026.0 / (double) populationSize), 0.1);
    }

    public IPVDataset sampleDataset(IPVDataset original, int sampleSize) {
        List<List<String>> values = new ArrayList<>();

        original.forEach(values::add);

        // shuffle list
        Collections.shuffle(values);

        return new IPVDataset(
                values.subList(0, sampleSize),
                original.getSchema(),
                original.hasColumnNames()
        );
    }

    public IPVDataset generateRandomData(Map<Integer, Integer> eqSizes) {

        int index = 0;
        List<List<String>> rows = new ArrayList<>();

        for(Map.Entry<Integer, Integer> entry: eqSizes.entrySet()) {
            Integer eqSize = entry.getKey();
            Integer counter = entry.getValue();

            for(int i = 0; i < counter; i++) {

                for(int j = 0; j < eqSize; j++) {
                    rows.add(Collections.singletonList(Integer.toString(index)));
                }

                index++;
            }
        }

        return new IPVDataset(
                rows, IPVDataset.generateSchemaWithoutColumnNames(1),
                false
        );
    }
}
