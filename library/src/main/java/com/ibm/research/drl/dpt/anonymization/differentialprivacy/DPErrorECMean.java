/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

public class DPErrorECMean implements DPError {
    @Override
    public double reportError(Partition original, Partition noisy, int columnIndex) {
        IPVDataset originalDataset = original.getMember();
        IPVDataset noisyDataset = noisy.getMember();

        int records = originalDataset.getNumberOfRows();

        if (records != noisyDataset.getNumberOfRows()) {
            throw new RuntimeException("Partition sizes must be identical");
        }

        double sumOriginal = 0.0;
        double sumNoisy = 0.0;

        for (int i=0;i<records;i++) {
            double originalValue = Double.parseDouble(originalDataset.get(i, columnIndex));
            double noisyValue = Double.parseDouble(noisyDataset.get(i, columnIndex));

            sumOriginal += originalValue;
            sumNoisy += noisyValue;
        }

        return Math.abs(sumOriginal - sumNoisy) / Math.abs(sumOriginal);
    }

    @Override
    public String getName() { return "DP Error: Equivalence Class Mean"; }

    @Override
    public String getDescription() {
        return "Calculates the relative error of the mean of each equivalence class, weighted to the size of the each equivalence class";
    }
}

