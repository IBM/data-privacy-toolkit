/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.util.ArrayList;
import java.util.List;

public class CategoricalPrecision implements InformationMetric {
    private IPVDataset original;
    private IPVDataset anonymized;
    private List<ColumnInformation> columnInformationList;
    private boolean withTransformationLevels;
    private int[] transformationLevels;
    
    
    @Override
    public String toString(){
        return "CategoricalPrecision";
    }

    private double getLossCategorical(String value, ColumnInformation columnInformation) {
        CategoricalInformation categoricalInformation = (CategoricalInformation)columnInformation;
        int height = categoricalInformation.getHierarchy().getHeight();

        int level = categoricalInformation.getHierarchy().getNodeLevel(value);

        if (level < 0) {
           throw new RuntimeException("unknown level for: " + value + ", column info: " + columnInformation.toString()); 
        }
        
        if (level == 0) {
            return 0.0;
        }
        
        return (double)(level) / (double)(height - 1);
    }

    @Override
    public String getName() {
        return "Categorical Precision";
    }

    @Override
    public String getShortName() {
        return "CP";
    }

    @Override
    public double getLowerBound() {
        return 0.0d;
    }

    @Override
    public double getUpperBound() {
        return 1.0d;
    }

    @Override
    public boolean supportsNumerical() {
        return false;
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
        List<InformationLossResult> columnResults = reportPerQuasiColumn();
        
        double sum = 0.0;
        for(InformationLossResult lossResult: columnResults) {
            sum += lossResult.getValue();
        }
        
        return sum / (double)columnResults.size();
    }

    private InformationLossResult reportForColumn(int columnIndex) {

        ColumnInformation columnInformation = columnInformationList.get(columnIndex);
        if (columnInformation.getColumnType() != ColumnType.QUASI) {
            return null;
        }

        if (!columnInformation.isCategorical()) {
            return null;
        }

        double weight = columnInformation.getWeight();
        int numberOfRows = anonymized.getNumberOfRows();
        double precision = 0.0;
        double cells = 0.0;

        for(int i = 0; i < numberOfRows; i++) {
            cells++;

            double loss = weight*getLossCategorical(anonymized.get(i, columnIndex), columnInformation);
            precision += loss;
        }

        int suppressedRows = original.getNumberOfRows() - anonymized.getNumberOfRows();
        cells += suppressedRows;
        precision += weight*1*suppressedRows;

        double iloss = (precision) / cells;
        return new InformationLossResult(iloss, 0.0d, 1.0d);
    }

    private InformationLossResult reportForColumnWithTransformationLevel(int columnIndex, int quasiIndex) {
        ColumnInformation columnInformation = columnInformationList.get(columnIndex);
        if (columnInformation.getColumnType() != ColumnType.QUASI) {
            return null;
        }

        if (!columnInformation.isCategorical()) {
            return null;
        }

        double weight = columnInformation.getWeight();
        
        int suppressedRows = original.getNumberOfRows() - anonymized.getNumberOfRows();

        CategoricalInformation categoricalInformation = (CategoricalInformation)columnInformation;
        int height = categoricalInformation.getHierarchy().getHeight();
        
        int generalizationLevel = this.transformationLevels[quasiIndex];
        
        double iloss = 0.0;
        
        iloss += suppressedRows * 1.0;
        
        //for each anonymized row, the loss is transformation level / height - 1
        iloss += anonymized.getNumberOfRows() * ((double) generalizationLevel / (double)(height - 1));
        
        iloss /= (double) original.getNumberOfRows();
        
        iloss *= weight;
        
        return new InformationLossResult(iloss, 0.0, 1.0);
        
    }
    
    @Override
    public List<InformationLossResult> reportPerQuasiColumn() {
        List<InformationLossResult> results = new ArrayList<>();
        int columnIndex = 0;
        int quasiIndex = 0;

        for(ColumnInformation columnInformation: columnInformationList) {
            if (columnInformation.getColumnType() != ColumnType.QUASI) {
                columnIndex++;
                continue;
            }

            InformationLossResult iloss;
            
            if (this.withTransformationLevels) {
                iloss = reportForColumnWithTransformationLevel(columnIndex, quasiIndex);
            }
            else {
                iloss = reportForColumn(columnIndex);
            }

            results.add(iloss);
            columnIndex++;
            quasiIndex++;
        }

        return results;
    }

    /**
     * Instantiates a new CategoricalPrecision.
     *
     * @param original              the original
     * @param anonymized            the anonymized
     * @param columnInformationList the column information list
     */
    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List< ColumnInformation > columnInformationList, InformationMetricOptions options) {
        this.original = original;
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;
        
        this.withTransformationLevels = false;
        this.transformationLevels = null;
        
        return this;
    }
    
    @Override
    public InformationMetric initialize(IPVDataset original, IPVDataset anonymized, List<Partition> originalPartitions, List<Partition> anonymizedPartitions,
                                        List<ColumnInformation> columnInformationList, int[] transformationLevels, InformationMetricOptions options) {
        this.original = original;
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;

        this.withTransformationLevels = true;
        this.transformationLevels = transformationLevels;
        
        return this;
    }
}

