/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/

package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

public class DPErrorDiscrete implements DPError {
    @Override
    public double reportError(Partition original, Partition noisy, int columnIndex) {
        IPVDataset originalDataset = original.getMember();
        int numberOfRows = originalDataset.getNumberOfRows();
        IPVDataset noisyDataset = noisy.getMember();

        if (numberOfRows != noisyDataset.getNumberOfRows()) {
            throw new RuntimeException("Original and Noisy datasets must have the same number of rows");
        }

        double error = 0.0;

        for (int i = 0; i < numberOfRows; i++) {
            String originalValue = originalDataset.get(i, columnIndex);
            String noisyValue = noisyDataset.get(i, columnIndex);

            if (!originalValue.equalsIgnoreCase(noisyValue)) {
                error += 1.0;
            }
        }

        return error / numberOfRows;
    }

    @Override
    public String getName() { return "Discrete error"; }

    @Override
    public String getDescription() {
        return "Average frequency of each value being incorrectly reassigned by the randomisation";
    }
}

