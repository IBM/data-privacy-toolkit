/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.informationloss;

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetricOptions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.util.DoubleAccumulator;

import java.util.List;

public class CategoricalPrecision implements InformationMetricSpark {
    private JavaRDD<String> anonymized;
    private JavaRDD<String> original;
    private List<ColumnInformation> columnInformationList;

    private double getLossCategorical(String value, ColumnInformation columnInformation) {
        CategoricalInformation categoricalInformation = (CategoricalInformation)columnInformation;

        int level = categoricalInformation.getHierarchy().getNodeLevel(value);

        if (level == 0) {
            return 0.0;
        }

        return (level + 1.0) / (double) categoricalInformation.getHierarchy().getHeight();
    }

    @Override
    public String getName(){
        return "Categorical Precision";
    }

    @Override
    public String getShortName(){
        return "CP";
    }

    @Override
    public Double getLowerBound() {
        return null;
    }

    @Override
    public Double getUpperBound() {
        return null;
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
    public Double report() {
        final List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(this.columnInformationList, ColumnType.QUASI);

        final DoubleAccumulator cells = this.anonymized.rdd().sparkContext().doubleAccumulator();

        double precision = this.anonymized.map((Function<String, Double>) s -> {
            CSVRecord record = CSVParser.parse(s, CSVFormat.RFC4180).getRecords().get(0);

            double precision1 = 0.0;

            for(int  j: quasiColumns) {
                ColumnInformation columnInformation = columnInformationList.get(j);
                if (!columnInformation.isCategorical()) {
                    continue;
                }

                cells.add(1.0);

                double loss = getLossCategorical(record.get(j), columnInformation) * columnInformation.getWeight();
                precision1 += loss;
            }

            return precision1;
        }).reduce((Function2<Double, Double, Double>) (aDouble, aDouble2) -> aDouble + aDouble2);

        return (precision) / cells.value();

    }

    @Override
    public InformationMetricSpark initialize(JavaRDD<String> original, JavaRDD<String> anonymized, List<ColumnInformation> columnInformationList,
                                             int k, InformationMetricOptions options) {
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;

        return this;
    }
}
