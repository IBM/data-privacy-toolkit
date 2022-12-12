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
import com.ibm.research.drl.dpt.util.EntropyUtilities;
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.List;

public class EntropyLDiversity implements PrivacyConstraint {
    private final int l;

    @JsonCreator
    public EntropyLDiversity(
            @JsonProperty("l") int l
    ) {
        this.l = l;
    }

    public int getL() {
        return l;
    }

    private boolean checkHistogramEntropy(Histogram histogram, int total) {
        double sum1 = EntropyUtilities.calculateEntropy(histogram, total);

        return Math.log(this.l) <= sum1;
    }

    private final boolean checkEntropyDiversity(Partition partition, int column) {

        int total = partition.size();

        if (total < this.l) {
            return false;
        }

        Histogram histogram = Histogram.createHistogram(partition.getMember(), column);

        return checkHistogramEntropy(histogram, total);
    }

    @Override
    public boolean check(PrivacyMetric metric) {
        LDiversityMetric lDiversityMetric = (LDiversityMetric)metric;

        long total = lDiversityMetric.getCount();
        if (total < this.l) {
            return false;
        }

        for(Histogram histogram: lDiversityMetric.getHistograms()) {
            if (!checkHistogramEntropy(histogram, (int)total)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean check(Partition partition, List<Integer> sensitiveColumns) {
        if (sensitiveColumns == null) {
            return true;
        }

        for(Integer sensitiveColumn: sensitiveColumns) {
            if (!checkEntropyDiversity(partition, sensitiveColumn)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int contentRequirements() {
        return ContentRequirements.SENSITIVE;
    }

    @Override
    public boolean requiresAnonymizedPartition() {
        return false;
    }

    @Override
    public void sanityChecks(IPVDataset originalDataset) {
        if (this.l > originalDataset.getNumberOfRows()) {
            throw new RuntimeException("l-value is bigger than the original dataset");
        }
    }

    @Override
    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        sanityChecks(dataset);
    }

}
