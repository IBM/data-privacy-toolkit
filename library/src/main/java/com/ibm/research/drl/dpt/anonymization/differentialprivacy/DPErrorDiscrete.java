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

