/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.spark.informationloss;

import com.ibm.research.drl.dpt.anonymization.AnonymizationUtils;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.informationloss.InformationMetricOptions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import scala.Tuple2;

import java.util.List;


public class DiscernibilityStar implements InformationMetricSpark {
    private Dataset<String> anonymized;
    private List<ColumnInformation> columnInformationList;

    @Override
    public String getName() {
        return "Discernibility Star";
    }

    @Override
    public String getShortName() {
        return "DMSTAR";
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

        return this.anonymized
                .map((MapFunction<String, String>) s -> {
                    StringBuilder key = new StringBuilder();
                    CSVRecord record = CSVParser.parse(s, CSVFormat.RFC4180).getRecords().get(0);
                    for (Integer column : quasiColumns) {
                        key.append(record.get(column)).append(":");
                    }
                    return key.toString();
                }, Encoders.STRING())
                .groupBy(org.apache.spark.sql.functions.col("value"))
                .count()
                .map((MapFunction<org.apache.spark.sql.Row, Double>) row -> Math.pow(row.getLong(1), 2),
                        Encoders.DOUBLE())
                .reduce((org.apache.spark.api.java.function.ReduceFunction<Double>) (a, b) -> a + b);
    }

    @Override
    public InformationMetricSpark initialize(Dataset<String> original, Dataset<String> anonymized, List<ColumnInformation> columnInformationList,
                                             int k, InformationMetricOptions options) {
        this.anonymized = anonymized;
        this.columnInformationList = columnInformationList;
        return this;
    }
}
