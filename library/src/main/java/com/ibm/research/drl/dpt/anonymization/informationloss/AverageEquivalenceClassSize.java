/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.ArrayList;
import java.util.List;

public class AverageEquivalenceClassSize implements InformationMetric {
    private List<Partition> partitions;
    private double total_records;
    private boolean normalized;
    private int k;
    private int quasiIdentifiersLength;

    @Override
    public String getName() {
        return "Average Equivalence Class Size";
    }

    @Override
    public String getShortName() {
        return "AECS";
    }

    @Override
    public double getLowerBound() {
        return Double.NaN;
    }

    @Override
    public double getUpperBound() {
        return Double.NaN;
    }

    @Override
    public boolean supportsNumerical() {
        return true;
    }

    @Override
    public boolean supportsCategorical() {
        return true;
    }

    @Override
    public boolean supportsSuppressedDatasets() {
        return true;
    }

    @Override
    public boolean supportsWeights() {
        return false;
    }

    /**
     * Report double.
     *
     * @return the double
     */
    public double report() {
        int equivalence_classes = 0;

        for(Partition p: partitions) {
            if (p.size() > 0) {
                equivalence_classes++;
            }
        }

        double aecs = total_records/(double)equivalence_classes;

        if (!normalized) {
            return aecs;
        } else {
            return aecs / this.k;
        }

    }

    @Override
    public List<InformationLossResult> reportPerQuasiColumn() {
        double iloss = report();
        double lower = getLowerBound();
        double upper = getUpperBound();

        List<InformationLossResult> results = new ArrayList<>();

        for(int i = 0; i < quasiIdentifiersLength; i++) {
            results.add(new InformationLossResult(iloss, lower, upper));
        }

        return results;
    }

    /**
     * Instantiates a new Average equivalence class size.
     *
     * @param original              the original
     * @param anonymized            the anonymized
     * @param columnInformationList the column information list
     */
    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {
        this.partitions = anonymizedPartitions; /* TODO: verify this */
        this.total_records = original.getNumberOfRows();
        this.k = options.getIntValue("k");
        this.normalized = options != null && options.getBooleanValue("normalized");
        this.quasiIdentifiersLength = (int) columnInformationList.stream().filter(columnInformation -> columnInformation.getColumnType().equals(ColumnType.QUASI)).count();
        return this;
    }

    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        return initialize(original, anonymized, originalPartitions, anonymizedPartitions, columnInformationList, options);
    }
}
