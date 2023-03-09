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

