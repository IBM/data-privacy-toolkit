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
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.*;

public class ColumnInformationGenerator {

    /**
     * Generate numerical range numerical range.
     *
     * @param dataset    the dataset
     * @param column     the column
     * @param columnType the column type
     * @return the numerical range
     */
    public static NumericalRange generateNumericalRange(IPVDataset dataset, int column, ColumnType columnType) {
        return generateNumericalRange(dataset, column, columnType, 1.0);
    }

    public static NumericalRange generateNumericalRange(IPVDataset dataset, int column, ColumnType columnType, double weight) {
        return generateNumericalRange(dataset, column, columnType, weight, false);
    }

    public static NumericalRange generateNumericalRange(IPVDataset dataset, int column, ColumnType columnType, double weight, boolean isForLinking) {
        List<Double> doubles = new ArrayList<>();

        int numberOfRows = dataset.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            List<String> row = dataset.getRow(i);
            try {
                Double v = Double.valueOf(row.get(column));
                doubles.add(v);
            } catch (NumberFormatException ignored) {
            }
        }

        Collections.sort(doubles);
        return new NumericalRange(doubles, columnType, weight, isForLinking);
    }

    /**
     * Generate categorical from data categorical information.
     *
     * @param dataset    the dataset
     * @param column     the column
     * @param columnType the column type
     * @return the categorical information
     */
    public static CategoricalInformation generateCategoricalFromData(IPVDataset dataset, int column, ColumnType columnType) {
        Set<String> valueSet = new HashSet<>();

        int numberOfRows = dataset.getNumberOfRows();

        for (int i = 0; i < numberOfRows; i++) {
            List<String> row = dataset.getRow(i);
            String v = row.get(column);
            valueSet.add(v);
        }

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();

        for (String v : valueSet) {
            String[] items = new String[2];
            items[0] = v;
            items[1] = "*";
            hierarchy.add(items);
        }

        return new CategoricalInformation(hierarchy, columnType);
    }
}
