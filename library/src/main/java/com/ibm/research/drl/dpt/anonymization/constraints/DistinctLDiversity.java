/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ContentRequirements;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistinctLDiversity implements PrivacyConstraint {
    private final int l;

    @JsonCreator
    public DistinctLDiversity(@JsonProperty("l") int l) {
        this.l = l;
    }

    public int getL() {
        return l;
    }

    private boolean checkDistinctLDiversity(Partition partition, List<Integer> sensitiveColumns) {
        for (Integer sensitiveColumn : sensitiveColumns) {
            Set<String> uniqueValues = new HashSet<>();

            IPVDataset members = partition.getMember();
            int numberOfRows = members.getNumberOfRows();

            for (int i = 0; i < numberOfRows; i++) {
                String value = members.get(i, sensitiveColumn);
                uniqueValues.add(value.toUpperCase());

                //break early if we have l-represented values
                if (uniqueValues.size() >= l) {
                    break;
                }
            }

            if (uniqueValues.size() < this.l) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean check(PrivacyMetric metric) {
        LDiversityMetric lDiversityMetric = (LDiversityMetric) metric;

        List<Histogram> histograms = lDiversityMetric.getHistograms();

        for (Histogram histogram : histograms) {
            if (histogram.size() < this.l) {
                return false;
            }
        }

        return true;
    }

    public boolean check(Partition partition, List<Integer> sensitiveColumns) {
        if (sensitiveColumns == null) {
            return true;
        }

        return checkDistinctLDiversity(partition, sensitiveColumns);

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
            throw new RuntimeException("l-value is bigger than the size of original dataset");
        }
    }

    @Override
    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        sanityChecks(dataset);
    }


    public String toString() {
        return "Distinct L-Diversity constraint, l value = " + l;
    }

}
