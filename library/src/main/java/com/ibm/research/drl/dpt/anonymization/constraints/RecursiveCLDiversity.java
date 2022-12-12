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
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecursiveCLDiversity implements PrivacyConstraint {
    private final int l;
    private final double c;

    @JsonCreator
    public RecursiveCLDiversity(
            @JsonProperty("l") int l,
            @JsonProperty("c") double c) {
        this.l = l;
        this.c = c;
    }

    public int getL() {
        return l;
    }

    public double getC() {
        return c;
    }

    private boolean checkHistogram(Histogram<String> histogram) {
        List<Long> frequencies = new ArrayList<>();

        for(String key: histogram.keySet()) {
            Long counter = histogram.get(key);
            frequencies.add(counter);
        }

        Collections.sort(frequencies);

        double threshold = 0;
        for (int i = frequencies.size() - this.l; i >= 0; i--) { // minSize=(int)l;
            threshold += frequencies.get(i);
        }

        threshold *= this.c;

        return frequencies.get(frequencies.size() - 1) < threshold;
    }

    private boolean checkRecursiveCLDiversity(Partition partition, int column) {
        if (partition.size() < this.l) {
            return false;
        }

        Histogram histogram = Histogram.createHistogram(partition.getMember(), column);
        return checkHistogram(histogram);
    }

    @Override
    public boolean check(PrivacyMetric metric) {
        LDiversityMetric lDiversityMetric = (LDiversityMetric)metric;

        for(Histogram histogram: lDiversityMetric.getHistograms()) {
            if (!checkHistogram(histogram)) {
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
            if(!checkRecursiveCLDiversity(partition, sensitiveColumn)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean requiresAnonymizedPartition() {
        return false;
    }

    @Override
    public int contentRequirements() {
        return ContentRequirements.SENSITIVE;
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
