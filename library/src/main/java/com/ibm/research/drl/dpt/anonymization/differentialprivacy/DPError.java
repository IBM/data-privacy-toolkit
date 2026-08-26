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
package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.Partition;

import java.util.List;

/** Computes error metrics for differential privacy algorithms. */
public interface DPError {
    /**
     * Reports the error for the given DifferentialPrivacy run.
     *
     * @param differentialPrivacy the completed DP algorithm
     * @return the computed error value
     */
    default double reportError(DifferentialPrivacy differentialPrivacy) {
        return reportError(differentialPrivacy.getOriginalPartitions(), differentialPrivacy.getAnonymizedPartitions(), differentialPrivacy.columnIndex);
    }

    /**
     * Reports the error for a single partition.
     *
     * @param original    the original partition
     * @param noisy       the noisy partition
     * @param columnIndex the column index
     * @return the computed error value
     */
    double reportError(Partition original, Partition noisy, int columnIndex);

    /**
     * Reports the error across a list of partitions.
     *
     * @param original    the original partitions
     * @param noisy       the noisy partitions
     * @param columnIndex the column index
     * @return the weighted average error
     */
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

    /**
     * Returns the name of this error metric.
     *
     * @return the metric name
     */
    String getName();

    /**
     * Returns a description of this error metric.
     *
     * @return the metric description
     */
    String getDescription();
}

