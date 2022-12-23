/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.informationloss;

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetricOptions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.List;

public class Discernibility implements InformationMetricSpark {
    private JavaRDD<String> anonymized;
    private JavaRDD<String> original;
    private List<ColumnInformation> columnInformationList;
    private int k;

    @Override
    public String getName(){
        return "Discernibility";
    }

    @Override
    public String getShortName(){
        return "DM";
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
        final double totalRows = this.original.count();
        final int kValue = this.k;

        return this.anonymized.mapToPair(new PairFunction<String, String, Long>() {
            @Override
            public Tuple2<String, Long> call(String s) throws Exception {
                String key = "";
                CSVRecord record = CSVParser.parse(s, CSVFormat.RFC4180).getRecords().get(0);

                for(Integer column: quasiColumns) {
                    key += record.get(column) + ":";
                }

                return new Tuple2<String, Long>(key, 1L);
            }
        }).reduceByKey(new Function2<Long, Long, Long>() {
            @Override
            public Long call(Long a, Long b) throws Exception {
                return a + b;
            }
        }).map(new Function<Tuple2<String,Long>, Double>() {
            @Override
            public Double call(Tuple2<String, Long> s) throws Exception {
                Long value = s._2();
                if (value >= kValue) {
                    return Math.pow(s._2(), 2);
                }

                return value * totalRows;
            }
        }).reduce(new Function2<Double, Double, Double>() {
            @Override
            public Double call(Double a, Double b) throws Exception {
                return a + b;
            }
        });

    }

    @Override
    public InformationMetricSpark initialize(JavaRDD<String> original, JavaRDD<String> anonymized, List<ColumnInformation> columnInformationList,
                                             int k, InformationMetricOptions options) {
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;
        this.k = k;

        return this;
    }
}
