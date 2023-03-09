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
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.commons.cli.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.*;

public class DistributionExtractor implements Serializable {
    private static final Logger logger = LogManager.getLogger(DistributionExtractor.class);
    public static void main(String[] args) {
        try {
            CommandLine cmd = createAndParseCommandLine(args);

            DistributionExtractorOptions configuration = SparkUtils.deserializeConfiguration(cmd.getOptionValue("c"), DistributionExtractorOptions.class);

            DataTypeFormat inputFormat = DataTypeFormat.valueOf(cmd.getOptionValue("t", "PARQUET"));
            DatasetOptions datasetOptions = null;

            if (inputFormat == DataTypeFormat.CSV) {
                datasetOptions = new CSVDatasetOptions(cmd.hasOption("h"), ',', '"', false);
            }

            int numberOfBins = Integer.parseInt(cmd.getOptionValue("binNum", "0"));

            logger.info("input: " + cmd.getOptionValue("i"));
            logger.info("input format: " + inputFormat.name());
            logger.info("output: " + cmd.getOptionValue("o"));
            logger.info("number of bins: " + numberOfBins);
            logger.info("configuration: " + (new ObjectMapper()).writeValueAsString(configuration));

            final SparkSession spark = SparkUtils.createSparkSession("DistributionExtractor");
            final Dataset<Row> dataset = SparkUtils.createDataset(spark, cmd.getOptionValue("i"), inputFormat, datasetOptions, cmd.getOptionValue("basePath"));

            JavaRDD<Row> data = extractDistribution(dataset, configuration);

            JavaDoubleRDD numericData = data.mapToDouble(r -> Double.parseDouble(r.get(0).toString()));

            boolean remoteOutput = cmd.hasOption("remoteOutput");
            final String outputPath = cmd.getOptionValue("o");
            final List<String> binsStrings;

            if (numericData.isEmpty()) {
                binsStrings = extractActualValues(numericData);
            } else {
                final double m = numericData.min();
                final double M = numericData.max();

                if (null != configuration.getBinningCondition()) {
                    binsStrings = applyBinningIfRequired(numericData, m, M, configuration.getBinningCondition());
                } else {
                    if (-1 == numberOfBins) {
                        binsStrings = extractActualValues(numericData);
                    } else {
                        binsStrings = extractBins(numericData, m, M, numberOfBins, true);
                    }
                }

                final List<String> dataForCDF = extractBins(numericData, m, M, Math.min(10000, numericData.count()), true);
                final List<Tuple<Double, Double>> numbersForCDF = dataForCDF.parallelStream().map (x -> {
                    String[] toks = x.split(",");
                    return new Tuple<>(Double.parseDouble(toks[0]), Double.parseDouble(toks[1]));
                }).collect(Collectors.toList());

                final List<Tuple<Double, Double>> cdf = calculateCDF(numbersForCDF);

                try (OutputStream os = remoteOutput? SparkUtils.createHDFSOutputStream(outputPath + ".cdf") : new FileOutputStream(outputPath + ".cdf");
                     PrintWriter writer = new PrintWriter(os)) {
                    for (Tuple<Double, Double> t: cdf) {
                        writer.println(t.getFirst() + "," + t.getSecond());
                    }
                }
            }

            try (OutputStream os = remoteOutput ? SparkUtils.createHDFSOutputStream(outputPath) : new FileOutputStream(outputPath);
                 PrintWriter writer = new PrintWriter(os)) {

                for (String line : binsStrings) {
                    writer.println(line);
                }
            }
            
        } catch (ParseException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static JavaRDD<Row> extractDistribution(Dataset<Row> dataset, DistributionExtractorOptions configuration) {
        if (containsCustomExtraction(configuration)) {
            return executeCustomDistributionExtraction(dataset, configuration);
        } else {
            final Column[] idColumns = buildSelect(configuration.getIdentityFields());

            final Dataset<Row> histograms;
            if (configuration.getIdentityFields().isEmpty()) {
                histograms = computeFieldSelection(dataset, buildSelectCondition(configuration.getThresholds()));
            } else {
                List<DistributionExtractorOptions.ProjectedExpression> conditions = configuration.getThresholds();
                Column firstAggregation = buildAggregation(conditions.get(0));
                Column[] subsequentAggregations = buildAggregations(conditions.subList(1, conditions.size()));
                histograms = removeIdentity(configuration.getIdentityFields(), computeAggregation(dataset, idColumns, firstAggregation, subsequentAggregations));
            }

            String valueColumnName = extractDistributionColumn(configuration.getThresholds());

            return histograms.where(col(valueColumnName).isNotNull()).javaRDD();
        }
    }

    private static JavaRDD<Row> executeCustomDistributionExtraction(Dataset<Row> dataset, DistributionExtractorOptions configuration) {
        DistributionExtractorOptions.ProjectedExpression expression = getCustomExpression(configuration);
        String query = getExpression(expression);
        String tableName = getTableName(expression);
        String columnName = getColumnName(expression);

        dataset.createOrReplaceTempView(tableName);

        Dataset<Row> response = dataset.sqlContext().sql(query).select(col(columnName));

        return response.toJavaRDD();
    }

    private static String getColumnName(DistributionExtractorOptions.ProjectedExpression expression) {
        String columnName = expression.getColumnName();
        if (null == columnName) throw new IllegalArgumentException("Missing custom columnName");
        return columnName;
    }

    private static DistributionExtractorOptions.ProjectedExpression getCustomExpression(DistributionExtractorOptions configuration) {
        for (DistributionExtractorOptions.ProjectedExpression expression : configuration.getThresholds()) {
            if (expression.getAggregationType() == AggregationType.CUSTOM) {
                return expression;
            }
        }
        throw new IllegalArgumentException("Missing custom expression");
    }

    private static String getTableName(DistributionExtractorOptions.ProjectedExpression expression) {
        String tableName = expression.getTableName();
        if (null == tableName) throw new IllegalArgumentException("Missing custom table name");
        return tableName;
    }

    private static String getExpression(DistributionExtractorOptions.ProjectedExpression expression) {
        String query = expression.getQuery();
        if (null == query) throw new IllegalArgumentException("Missing custom query");
        return query;
    }

    private static boolean containsCustomExtraction(DistributionExtractorOptions configuration) {
        for (DistributionExtractorOptions.ProjectedExpression expression : configuration.getThresholds()) {
            if (expression.getAggregationType() == AggregationType.CUSTOM) return true;
        }
        return false;
    }


    private static List<String> applyBinningIfRequired(JavaDoubleRDD data, double min, double max, DistributionExtractorOptions.BinningCondition options) {
        switch (options.getType()) {
            case SIZE:
                return extractBinsBySize(data, min, max, options.getBinSize(), options.isReturnMidPoint());
            case NUMBER:
                return extractBins(data, min, max, options.getBinNumber(), options.isReturnMidPoint());
            case NONE:
                return extractActualValues(data);
        }
        throw new IllegalArgumentException("Unknown binning type");
    }

    private static List<String> extractBinsBySize(JavaDoubleRDD data, double m, double M, double binSize, boolean returnMidpoint) {
        JavaRDD<Tuple2<double[], Long>> bins = data.mapToPair(value -> {
            double scaledValue = value - m;
            return new Tuple2<>((int) Math.floor(scaledValue / binSize), 1L);
        }).reduceByKey(Long::sum).map(createRanges(binSize, m, M));

        if (returnMidpoint) {
            return bins.map(tuple -> "" + (tuple._1()[0] + tuple._1()[1])/2.0 + "," + tuple._2()).collect();
        }

        return bins.map(tuple -> "" + tuple._1()[0] + "," + tuple._1()[1] + "," + tuple._2()).collect();
    }

    private static List<String> extractActualValues(JavaDoubleRDD values) {
        return values.map(Object::toString).collect();
    }

    public static List<Tuple<Double,Double>> calculateCDF(List<Tuple<Double,Double>> numbersForCDF) {
        List<Tuple<Double, Double>> cdf = new ArrayList<>(numbersForCDF.size());

        numbersForCDF.sort(Comparator.comparing(Tuple::getFirst));
        
        double totalValues = 0.0;
        for(Tuple<Double, Double> t: numbersForCDF) {
            totalValues += t.getSecond();
        }

        double sum = 0.0;
        
        for(Tuple<Double, Double> t: numbersForCDF) {
            cdf.add(new Tuple<>(t.getFirst(), (sum + t.getSecond()) / totalValues));
            sum += t.getSecond();
        } 
        
        return cdf;
    }

    private static Dataset<Row> removeIdentity(List<String> idColumns, Dataset<Row> dataset) {
        String[] columnNames = dataset.columns();

        List<Column> columns = new ArrayList<>(columnNames.length);

        for (String columnName : columnNames) {
            if (idColumns.contains(columnName)) continue;

            columns.add(col(columnName));
        }

        return dataset.select(columns.toArray(new Column[0]));
    }

    private static String extractDistributionColumn(List<DistributionExtractorOptions.ProjectedExpression> thresholds) {
        if (thresholds.size() > 1) throw new IllegalArgumentException("Supports single column projection");

        return thresholds.iterator().next().getColumnName();
    }

    private static List<String> extractBins(JavaDoubleRDD numericData, double m, double M, double numberOfBins, boolean returnMidpoint) {
        final double range = M - m;
        final double binSize = range / numberOfBins;

        JavaRDD<Tuple2<double[], Long>> bins = numericData.mapToPair(value -> {
            double scaledValue = value - m;
            return new Tuple2<>((int) Math.floor(scaledValue / binSize), 1L);
        }).reduceByKey(Long::sum).map(createRanges(binSize, m, M));

        if (returnMidpoint) {
            return bins.map(tuple -> "" + (tuple._1()[0] + tuple._1()[1])/2.0 + "," + tuple._2()).collect();
        }
        
        return bins.map(tuple -> "" + tuple._1()[0] + "," + tuple._1()[1] + "," + tuple._2()).collect();
    }

    private static Function<Tuple2<Integer, Long>, Tuple2<double[], Long>> createRanges(final double binSize, final double min, double max) {
        return idCount -> {
            int id = idCount._1();
            double[] ranges = new double[2];

            ranges[0] = (id * binSize) + min;
            ranges[1] = ((id + 1) * binSize) + min;

            return new Tuple2<>(ranges, idCount._2());
        };
    }

    private static Column[] buildSelectCondition(List<DistributionExtractorOptions.ProjectedExpression> thresholds) {
        Column[] selectedFields = new Column[thresholds.size()];

        for (int i = 0; i < selectedFields.length; ++i) {
            selectedFields[i] = col(thresholds.get(i).getColumnName());
        }

        return selectedFields;
    }

    private static Dataset<Row> computeFieldSelection(Dataset<Row> dataset, Column ... selectCondition) {
        return dataset.select(selectCondition);
    }

    private static Column[] buildSelect(List<String> identityFields) {
        Column[] selectColumns = new Column[identityFields.size()];

        for (int i = 0; i < selectColumns.length; ++i) {
            selectColumns[i] = col(identityFields.get(i));
        }

        return selectColumns;
    }

    private static Dataset<Row> computeAggregation(Dataset<Row> dataset, Column[] idColumn, Column aggregationFirst, Column ... aggregationRemaining) {
        return dataset.groupBy(idColumn).agg(aggregationFirst, aggregationRemaining);
    }

    private static Column buildAggregation(DistributionExtractorOptions.ProjectedExpression condition) {
        String columnName = condition.getColumnName();
        Column column = col(columnName);

        int properAggregation = 0;

        switch (condition.getAggregationType()) {
            case SUM:
                column = sum(column);
                properAggregation += 1;
                break;
            case COUNT:
                column = sum(lit(1L));
                properAggregation += 1;
                break;
            case MAX:
                column = max(column);
                properAggregation += 1;
                break;
            case MIN:
                column = min(column);
                properAggregation += 1;
                break;
            case NOT_AGGREGATED:
                break;
            default:
                throw new IllegalArgumentException("Unknown aggregation type");
        }

        if (0 == properAggregation) throw new IllegalArgumentException("No aggregation values specified");

        return column.as(columnName);
    }

    private static Column[] buildAggregations(List<DistributionExtractorOptions.ProjectedExpression> aggregationFields) {
        Column[] columns = new Column[aggregationFields.size()];

        for (int i = 0; i < aggregationFields.size(); ++i) {
            columns[i] = buildAggregation(aggregationFields.get(i));
        }

        return columns;
    }

    private static CommandLine createAndParseCommandLine(String[] args) throws ParseException {
        Options options = buildOptions();
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static Options buildOptions() {
        Option i = new Option("i", true, "Path to the input");
        i.setRequired(true);
        Option d = new Option("d", true, "Delimiter");
        Option q = new Option("q", true, "Quote char");
        Option h = new Option("h", false, "Has header");
        Option o = new Option("o", true, "Output file");
        o.setRequired(true);

        Option t = new Option("t", true, "Specified the input type (CSV or PARQUET, defaults to PARQUET if missing)");
        Option c = new Option("c", true, "Path to configuration");
        c.setRequired(true);
        Option basePath = new Option("basePath", false, "Remote configuration files (optional)");
        Option numberOfBins = new Option("binNum", true, "Number of bins");
        numberOfBins.setRequired(false);

        return new Options().
                addOption(i).
                addOption(d).
                addOption(q).
                addOption(h).
                addOption(c).
                addOption(numberOfBins).
                addOption(basePath).
                addOption(t).
                addOption(o);
    }
}
