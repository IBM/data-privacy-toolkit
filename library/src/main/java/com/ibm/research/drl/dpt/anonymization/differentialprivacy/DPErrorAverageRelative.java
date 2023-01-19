/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

public class DPErrorAverageRelative implements DPError {
    @Override
    public double reportError(Partition original, Partition noisy, int columnIndex) {
        IPVDataset originalDataset = original.getMember();
        IPVDataset noisyDataset = noisy.getMember();

        int records = originalDataset.getNumberOfRows();

        if (records != noisyDataset.getNumberOfRows()) {
            throw new RuntimeException("Partition sizes must be identical");
        }

        double error = 0.0;

        for (int i=0;i<records;i++) {
            double originalValue = Double.parseDouble(originalDataset.get(i, columnIndex));
            double noisyValue = Double.parseDouble(noisyDataset.get(i, columnIndex));
            double tempError = Math.abs(originalValue - noisyValue)/Math.max(10e-5, Math.abs(originalValue));

            error += tempError;
        }

        return error / records;
    }

    @Override
    public String getName() { return "Average relative error"; }

    @Override
    public String getDescription() {
        return "Calculates the relative error of each element, averaging over each equivalence class";
    }
}

