/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.List;

public class KAnonymity implements PrivacyConstraint {
    private final int k;

    @JsonCreator
    public KAnonymity(
            @JsonProperty("k") int k
    ) {
        this.k = k;
    }

    public int getK() {
        return k;
    }

    @Override
    public boolean check(PrivacyMetric metric) {
        return ((KAnonymityMetric) metric).getCount() >= this.k;
    }

    @Override
    public boolean check(Partition partition, List<Integer> sensitiveColumns) {
        return partition.size() >= k;
    }

    @Override
    public boolean requiresAnonymizedPartition() {
        return false;
    }

    public String toString() {
        return "K-anonymity constraint with k-value = " + k;
    }

    @Override
    public int contentRequirements() {
        return ContentRequirements.NONE;
    }

    @Override
    public void sanityChecks(IPVDataset originalDataset) {
        if (this.k > originalDataset.getNumberOfRows()) {
            throw new RuntimeException("k-value is larger than the dataset");
        }
    }

    @Override
    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        sanityChecks(dataset);
    }

    @Override
    public PrivacyMetric getMetricInstance() {
        return new KAnonymityMetric();
    }
}
