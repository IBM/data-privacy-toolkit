/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.ArrayList;
import java.util.List;

public class GeneralizedLossMetric implements  InformationMetric {
    private IPVDataset original;
    private IPVDataset anonymized;
    private List<ColumnInformation> columnInformationList;


    @Override
    public String toString() {
        return "Generalized Loss Metric";
    }

    private double getLossNumerical(String anonymized, NumericalRange columnInformation) {
        Double lowest = columnInformation.getLow();
        Double highest = columnInformation.getHigh();

        String[] tokens = anonymized.split("-");

        if (tokens.length == 1) {
            throw new RuntimeException("not implemented yet");
        }

        Long low = Long.valueOf(tokens[0]);
        Long high = Long.valueOf(tokens[1]);

        return ((double) (high - low)) / (highest - lowest);
    }

    private double getLossCategorical(String anonymized, CategoricalInformation columnInformation) {
        long hierarchyLeaves = columnInformation.getHierarchy().getTotalLeaves();

        int leaves = columnInformation.getHierarchy().leavesForNode(anonymized);

        if (leaves == 0) {
            return 0.0;
        }

        return ((double) (leaves - 1)) / ((double)(hierarchyLeaves - 1));
    }

    private double getLoss(String anonymized, ColumnInformation columnInformation) {

        String anonymizedValue = anonymized;

        if (columnInformation.isCategorical()) {
            return getLossCategorical(anonymizedValue, (CategoricalInformation) columnInformation);
        }
        else {
            return getLossNumerical(anonymizedValue, (NumericalRange) columnInformation);
        }

    }

    @Override
    public String getName() {
        return "Generalized Loss Metric (GLM)";
    }

    @Override
    public String getShortName() {
        return "GLM";
    }

    @Override
    public double getLowerBound() {
        return 0.0;
    }

    @Override
    public double getUpperBound() {
        Double lm = 0.0;

        int numberOfColumns = original.getNumberOfColumns();
        List<Double> lossPerColumn = new ArrayList<>(numberOfColumns);

        for(int k = 0; k < numberOfColumns; k++) {
            lossPerColumn.add(0.0);
        }

        int diff = original.getNumberOfRows();
        for(int  j = 0; j < numberOfColumns; j++) {
            ColumnInformation columnInformation = columnInformationList.get(j);
            if (columnInformation.getColumnType() != ColumnType.QUASI) {
                continue;
            }

            double loss = diff;
            lossPerColumn.set(j, lossPerColumn.get(j) + loss);
        }

        for(int k = 0; k < numberOfColumns; k++) {
            double averageColumnLoss = columnInformationList.get(k).getWeight() * lossPerColumn.get(k) / ((double) original.getNumberOfRows());
            lm += averageColumnLoss;
        }


        return lm;
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
        return true;
    }

    /**
     * Report double.
     *
     * @return the double
     */
    public double report() {

        int numberOfColumns = original.getNumberOfColumns();
        List<Double> lossPerColumn = new ArrayList<>(numberOfColumns);

        for(int k = 0; k < numberOfColumns; k++) {
            lossPerColumn.add(0.0);
        }

        int anonymizedNumberOfRows = anonymized.getNumberOfRows();

        for(int i = 0; i < anonymizedNumberOfRows; i++) {

            for(int  j = 0; j < numberOfColumns; j++) {

                ColumnInformation columnInformation = columnInformationList.get(j);
                if (columnInformation.getColumnType() != ColumnType.QUASI) {
                    continue;
                }

                double loss = getLoss(anonymized.get(i, j), columnInformation);
                lossPerColumn.set(j, lossPerColumn.get(j) + loss);
            }
        }

        Double lm = 0.0;

        if (anonymized.getNumberOfRows() < original.getNumberOfRows()) {
            int diff = original.getNumberOfRows() - anonymized.getNumberOfRows();

            for(int  j = 0; j < numberOfColumns; j++) {
                ColumnInformation columnInformation = columnInformationList.get(j);
                if (columnInformation.getColumnType() != ColumnType.QUASI) {
                    continue;
                }

                double loss = diff;
                lossPerColumn.set(j, lossPerColumn.get(j) + loss);
            }
        }

        for(int k = 0; k < numberOfColumns; k++) {
            double weight = columnInformationList.get(k).getWeight();
            double averageColumnLoss = weight * lossPerColumn.get(k) / ((double) original.getNumberOfRows());
            lm += averageColumnLoss;
        }


        return lm;
    }

    private InformationLossResult reportForColumn(int columnIndex) {

        ColumnInformation columnInformation = columnInformationList.get(columnIndex);
        if (columnInformation.getColumnType() != ColumnType.QUASI) {
            return null;
        }

        double lossPerColumn = 0.0;

        int anonymizedNumberOfRows = anonymized.getNumberOfRows();
        double weight = columnInformationList.get(columnIndex).getWeight();

        for(int i = 0; i < anonymizedNumberOfRows; i++) {
            double loss = getLoss(anonymized.get(i, columnIndex), columnInformation);
            lossPerColumn += loss;
        }

        if (anonymized.getNumberOfRows() < original.getNumberOfRows()) {
            int diff = original.getNumberOfRows() - anonymized.getNumberOfRows();
            lossPerColumn += weight*diff;
        }

        double averageColumnLoss = weight * lossPerColumn / ((double) original.getNumberOfRows());

        return new InformationLossResult(averageColumnLoss, 0.0, 1.0);
    }

    @Override
    public List<InformationLossResult> reportPerQuasiColumn() {
        List<InformationLossResult> results = new ArrayList<>();
        int columnIndex = 0;

        for(ColumnInformation columnInformation: columnInformationList) {
            if (columnInformation.getColumnType() != ColumnType.QUASI) {
                columnIndex++;
                continue;
            }

            InformationLossResult iloss = reportForColumn(columnIndex);

            results.add(iloss);
            columnIndex++;
        }

        return results;
    }

    /**
     * Instantiates a new Generalized loss metric.
     *
     * @param original              the original
     * @param anonymized            the anonymized
     * @param columnInformationList the column information list
     */
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions, 
                                        List<ColumnInformation> columnInformationList, InformationMetricOptions options) {

        if (original.getNumberOfColumns() != anonymized.getNumberOfColumns()) {
            throw new RuntimeException("mismatch on columns");
        }

        this.original = original;
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;
        return this;
    }
    
    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        return initialize(original, anonymized, originalPartitions, anonymizedPartitions, columnInformationList, options);
    }
}

