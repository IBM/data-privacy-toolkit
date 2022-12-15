/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
