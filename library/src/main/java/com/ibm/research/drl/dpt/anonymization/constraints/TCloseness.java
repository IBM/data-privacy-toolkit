/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.ContentRequirements;
import com.ibm.research.drl.dpt.anonymization.NumericalRange;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.PrivacyMetric;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TCloseness implements PrivacyConstraint {

    private final double t;

    private List<ColumnInformation> columnInformationList;
    private List<Histogram<Double>> histograms;
    private List<Histogram<String>> categoricalHistograms;
    private List<List<Double>> totalOrdered;
    private Long totalCount;

    public static double orderBasedDistance(List<Double> partitionValues, List<Double> totalOrdered,
                                            Histogram<Double> totalHistogram, long totalCount) {

        double sum = 0.0;
        double distance = 0.0;

        Histogram<Double> partitionHistogram = Histogram.createHistogram(partitionValues);
        long partitionCount = partitionValues.size();

        for (Double q : totalOrdered) {

            double qFreq = (double) totalHistogram.get(q) / (double) totalCount;
            double pFreq = (double) partitionHistogram.getOrDefault(q, 0L) / (double) partitionCount;

            sum += (pFreq - qFreq);
            distance += Math.abs(sum);
        }

        return distance / (double) (totalOrdered.size() - 1);
    }

    public static double equalDistance(List<String> partitionValues, Histogram<String> totalHistogram, long totalCount) {

        double sum = 0.0;

        Histogram<String> partitionHistogram = Histogram.createHistogram(partitionValues);
        int partitionSize = partitionValues.size();

        for (Map.Entry<String, Long> entry : totalHistogram.entrySet()) {
            String key = entry.getKey();
            Long count = entry.getValue();
            double qFreq = (double) count / (double) totalCount;
            double pFreq = (double) partitionHistogram.getOrDefault(key, 0L) / (double) partitionSize;

            sum += Math.abs(pFreq - qFreq);
        }

        return sum / 2.0;
    }

    @JsonCreator
    public TCloseness(
            @JsonProperty("t") double t
    ) {
        this.t = t;
    }

    public double getT() {
        return t;
    }

    public void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList) {
        this.columnInformationList = columnInformationList;
        this.totalCount = (long) dataset.getNumberOfRows();

        this.histograms = new ArrayList<>();
        this.categoricalHistograms = new ArrayList<>();
        this.totalOrdered = new ArrayList<>();

        initializeNumericalInformation(dataset, columnInformationList);
        initializeCategoricalInformation(dataset, columnInformationList);
    }

    private void initializeCategoricalInformation(IPVDataset dataset, List<ColumnInformation> columnInformationList) {

        for (int i = 0; i < columnInformationList.size(); i++) {
            ColumnInformation columnInformation = columnInformationList.get(i);

            if (columnInformation instanceof CategoricalInformation && columnInformation.getColumnType() == ColumnType.SENSITIVE) {
                List<String> values = new ArrayList<>();

                for (int row = 0; row < dataset.getNumberOfRows(); row++) {
                    values.add(dataset.get(row, i));
                }

                Histogram<String> histogram = Histogram.createHistogram(values);
                this.categoricalHistograms.add(histogram);
            } else {
                this.categoricalHistograms.add(null);
            }
        }
    }

    private void initializeNumericalInformation(IPVDataset dataset, List<ColumnInformation> columnInformationList) {

        for (int i = 0; i < columnInformationList.size(); i++) {
            ColumnInformation columnInformation = columnInformationList.get(i);

            if (columnInformation instanceof NumericalRange && columnInformation.getColumnType() == ColumnType.SENSITIVE) {
                List<Double> values = new ArrayList<>();

                for (int row = 0; row < dataset.getNumberOfRows(); row++) {
                    Double v = Double.parseDouble(dataset.get(row, i));
                    values.add(v);
                }

                Histogram<Double> histogram = Histogram.createHistogram(values);
                this.histograms.add(histogram);

                Collections.sort(values);
                this.totalOrdered.add(values);
            } else {
                this.histograms.add(null);
                this.totalOrdered.add(null);
            }
        }

    }

    @Override
    public boolean check(PrivacyMetric metric) {
        return false;
    }

    private boolean checkNumerical(IPVDataset members, Integer column) {
        List<Double> partitionValues = new ArrayList<>();

        for (int i = 0; i < members.getNumberOfRows(); i++) {
            Double value = Double.parseDouble(members.get(i, column));
            partitionValues.add(value);
        }

        List<Double> totalOrdered = this.totalOrdered.get(column);
        Histogram<Double> totalHistogram = this.histograms.get(column);

        double distance = orderBasedDistance(partitionValues, totalOrdered, totalHistogram, this.totalCount);
        return distance <= this.t;
    }

    private boolean checkCategorical(IPVDataset members, Integer column) {
        List<String> partitionValues = new ArrayList<>();

        for (int i = 0; i < members.getNumberOfRows(); i++) {
            partitionValues.add(members.get(i, column));
        }

        Histogram<String> totalHistogram = this.categoricalHistograms.get(column);

        double distance = equalDistance(partitionValues, totalHistogram, this.totalCount);
        return distance <= this.t;
    }

    private boolean checkPartition(Partition partition, Integer column) {
        IPVDataset members = partition.getMember();
        ColumnInformation columnInformation = this.columnInformationList.get(column);

        if (columnInformation instanceof NumericalRange) {
            return checkNumerical(members, column);
        } else {
            return checkCategorical(members, column);
        }
    }


    @Override
    public boolean check(Partition partition, List<Integer> sensitiveColumns) {

        for (Integer column : sensitiveColumns) {
            if (!checkPartition(partition, column)) {
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
    }
}
