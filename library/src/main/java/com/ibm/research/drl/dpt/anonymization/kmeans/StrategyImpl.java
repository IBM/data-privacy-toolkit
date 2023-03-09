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
package com.ibm.research.drl.dpt.anonymization.kmeans;


import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

public abstract class StrategyImpl {
    private final IPVDataset original;
    private final double suppressionRate;
    private final List<ColumnInformation> columnInformationList;
    private final List<PrivacyConstraint> privacyConstraints;

    public StrategyImpl(IPVDataset original,
                        double suppressionRate,
                        List<ColumnInformation> columnInformationList,
                        List<PrivacyConstraint> privacyConstraints) {
        this.original = original;
        this.suppressionRate = suppressionRate;
        this.columnInformationList = columnInformationList;
        this.privacyConstraints = privacyConstraints;
    }

    public abstract IPVDataset buildAnonymizedDataset(List<KMeansCluster> clusters);

    public abstract List<Partition> getOriginalPartitions();

    public abstract List<Partition> getAnonymizedPartitions();

    public abstract Long getSuppressedRows();
}

