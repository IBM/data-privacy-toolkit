/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.Partition;

import java.util.List;

public interface DPError {
    default double reportError(DifferentialPrivacy differentialPrivacy) {
        return reportError(differentialPrivacy.getOriginalPartitions(), differentialPrivacy.getAnonymizedPartitions(), differentialPrivacy.columnIndex);
    }
    double reportError(Partition original, Partition noisy, int columnIndex);

    default double reportError(List<Partition> original, List<Partition> noisy, int columnIndex) {
        int partitions = original.size();
        double error = 0.0;
        int totalSize = 0;

        if (noisy.size() != partitions) {
            throw new RuntimeException("Number of partitions between original and noisy datasets must be the same (" + partitions + " and " + noisy.size() + ")");
        }

        for (int i=0; i<partitions;i++) {
            Partition originalPartition = original.get(i);
            Partition noisyPartition = noisy.get(i);

            error += this.reportError(originalPartition, noisyPartition, columnIndex) * originalPartition.size();
            totalSize += originalPartition.size();
        }

        return error / totalSize;
    }

    String getName();

    String getDescription();
}

