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
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.linkability.AnonymizedDatasetLinker;
import com.ibm.research.drl.dpt.linkability.LinkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class ReidentificationRisk implements PrivacyConstraint {
    private final InputStream populationDataset;
    private final Collection<LinkInfo> linkInformation;
    private final List<ColumnInformation> columnInformation;
    private final double riskThreshold;
    private final AnonymizedDatasetLinker anonymizedDatasetLinker;
    private final boolean quasiSameAsLinking;

    @Override
    public boolean check(PrivacyMetric metric) {
        return false;
    }

    @Override
    public boolean check(Partition partition, List<Integer> sensitiveColumns) {

        if (this.quasiSameAsLinking) {
            List<String> row = partition.getMember().getRow(0);
            Integer matchedRows = anonymizedDatasetLinker.matchAnonymizedRow(row, this.linkInformation, this.columnInformation);

            if (matchedRows == 0) {
                return true;
            }

            double risk = 1.0 / (double) matchedRows;

            return (!(risk > riskThreshold));
        }

        List<Integer> matches = anonymizedDatasetLinker.matchesPerRecord(partition, this.linkInformation, this.columnInformation);

        if (matches.isEmpty()) {
            return true;
        }

        for (Integer match : matches) {
            if (match == 0) {
                continue;
            }

            double risk = 1.0 / (double) match;
            if (risk > this.riskThreshold) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean requiresAnonymizedPartition() {
        return true;
    }

    @Override
    public int contentRequirements() {
        return ContentRequirements.NORMAL & ContentRequirements.SENSITIVE & ContentRequirements.QUASI;
    }

    @Override
    public void sanityChecks(IPVDataset originalDataset) {

    }

    @Override
    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        sanityChecks(dataset);
    }

    @Override
    public PrivacyMetric getMetricInstance() {
        throw new UnsupportedOperationException();
    }

    public ReidentificationRisk(InputStream populationDataset,
                                Collection<LinkInfo> linkInformation,
                                List<ColumnInformation> columnInformation,
                                double riskThreshold) {

        this.populationDataset = populationDataset;
        this.linkInformation = linkInformation;
        this.riskThreshold = riskThreshold;
        this.columnInformation = columnInformation;

        this.quasiSameAsLinking = checkIfQuasiAndLinkAreTheSame(this.columnInformation, this.linkInformation);

        try {
            this.anonymizedDatasetLinker = new AnonymizedDatasetLinker(populationDataset, this.linkInformation);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("unable to initialize ReidentificationRisk: " + e.getMessage());
        }
    }

    private boolean checkIfQuasiAndLinkAreTheSame(List<ColumnInformation> columnInformation, Collection<LinkInfo> linkInformation) {

        for (int i = 0; i < columnInformation.size(); ++i) {
            if (columnInformation.get(i).getColumnType() != ColumnType.QUASI) {
                continue;
            }

            boolean match = false;

            for (LinkInfo info : linkInformation) {
                if (info.getSourceIndex() == i) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                return false;
            }
        }

        return true;
    }


}
