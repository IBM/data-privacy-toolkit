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
package com.ibm.research.drl.dpt.risk;

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;
import java.util.Map;

public class KRatioMetric implements RiskMetric {
    public static final String GAMMA = "gamma";
    private List<Partition> partitions;
    private double gamma;

    @Override
    public String getName() {
        return "K Ratio Metric";
    }

    @Override
    public String getShortName() {
        return "KRM";
    }

    @Override
    public double report() {
        double risk = 0.0;

        for (Partition partition : partitions) {
            risk = Math.max(gamma / partition.size(), risk);
        }

        return risk;
    }

    @Override
    public void validateOptions(Map<String, String> options) throws IllegalArgumentException {
        if (!options.containsKey(GAMMA)) throw new IllegalArgumentException("Missing parameter gamma");
        String gammaString = null;
        try {
            gammaString = options.get(GAMMA);

            if (null == gammaString) throw new IllegalArgumentException("Missing parameter gamma");
            double gamma = Double.parseDouble(gammaString);

            if (gamma <= 0 || gamma > 1)
                throw new IllegalArgumentException("gamma value is not a valid real number in (0, 1] " + gammaString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("gamma value is not a valid real number in (0, 1] " + gammaString);
        }
    }

    @Override
    public RiskMetric initialize(IPVDataset original, IPVDataset anonymized, List<ColumnInformation> columnInformationList, int k, Map<String, String> options) {
        this.partitions = AnonymizationUtils.generatePartitionsForLinking(anonymized, columnInformationList);
        this.gamma = Double.parseDouble(options.get(GAMMA));
        return this;
    }
}
