/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;


import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;


public class ValidationUtils {

    public static void mustBeTheSame(IPVDataset original, IPVDataset reloaded) {
        if (original.getNumberOfRows() != reloaded.getNumberOfRows()) throw new RuntimeException("Size differ");
        if (original.getNumberOfColumns() != reloaded.getNumberOfColumns()) throw new RuntimeException("Size differ");

        int numberOfRows = original.getNumberOfRows();
        int numberOfColumns = original.getNumberOfColumns();

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (!reloaded.get(i, j).equals(original.get(i, j))) throw new RuntimeException("Value differ");
            }
        }
    }

    public static void validateIsKAnonymous(IPVDataset dataset, List<ColumnInformation> columnInformation, int k) {
        List<Partition> partitions = PartitionUtils.createPartitions(dataset, columnInformation);
        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformation, ColumnType.QUASI);

        for (Partition p : partitions) {
            if (!(p.size() >= k)) throw new RuntimeException("Size differ");

            IPVDataset data = p.getMember();

            String firstKey = AnonymizationUtils.generateEQKey(data.getRow(0), quasiColumns);
            for (int i = 1; i < data.getNumberOfRows(); i++) {
                String key = AnonymizationUtils.generateEQKey(data.getRow(i), quasiColumns);
                if (!key.equals(firstKey)) throw new RuntimeException("Value differ");
            }
        }

    }

}
