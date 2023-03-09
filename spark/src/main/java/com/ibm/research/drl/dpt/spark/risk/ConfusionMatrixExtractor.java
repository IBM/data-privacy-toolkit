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
import com.ibm.research.drl.dpt.spark.export.Export;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.RelationalGroupedDataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.spark.sql.functions.*;

public class ConfusionMatrixExtractor implements Serializable {
    private static final Logger logger = LogManager.getLogger(ConfusionMatrixExtractor.class);

    public static void main(String[] args) {
        try {
            CommandLine cmd = createAndParseCommandLine(args);

            OutlierRemovalOptions configuration = SparkUtils.deserializeConfiguration(cmd.getOptionValue("c"), OutlierRemovalOptions.class);

            final SparkSession spark = SparkSession.builder().sparkContext(new SparkContext(new SparkConf(true))).getOrCreate();

            DataTypeFormat inputFormat = DataTypeFormat.valueOf(cmd.getOptionValue("t", "PARQUET"));
            DatasetOptions datasetOptions = null;

            if (inputFormat == DataTypeFormat.CSV) {
                datasetOptions = new CSVDatasetOptions(cmd.hasOption("h"), ',', '"', false);
            }

            logger.info("input: " + cmd.getOptionValue("i"));
            logger.info("input format: " + inputFormat.name());
            logger.info("output: " + cmd.getOptionValue("o"));
            logger.info("append: " + cmd.hasOption("a"));
            logger.info("configuration: " + (new ObjectMapper()).writeValueAsString(configuration));
            final List<String> partitions;
                partitions = Collections.emptyList();

            final Dataset<Row> dataset = SparkUtils.createDataset(spark, cmd.getOptionValue("i"), inputFormat, datasetOptions, cmd.getOptionValue("basePath"));

            List<OutlierRemovalFilter> filters = configuration.getFilters();

            Dataset<Row> withOutlierCondition = computeConfusionMatrix(dataset, filters);

            List<String> partitioningFields = buildPartitioningField(partitions, configuration.getFilterColumnName());
            Export.doExport(
                    withOutlierCondition,
                    inputFormat,
                    cmd.getOptionValue("o"),
                    partitioningFields,
                    cmd.hasOption("a"));

        } catch (ParseException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Dataset<Row> computeConfusionMatrix(final Dataset<Row> dataset, List<OutlierRemovalFilter> filters) {
        final Column[] selectionColumns = new Column[filters.size()];

        Dataset<Row> operationalDataset = dataset;

        int i = 0;
        for (OutlierRemovalFilter filter : filters) {
            String newColumn = UUID.randomUUID().toString();
            selectionColumns[i++] = col(newColumn);

            operationalDataset = augmentDatasetWithFilter(operationalDataset, filter, newColumn);
        }

        return operationalDataset.select(selectionColumns);
    }

    private static Dataset<Row> augmentDatasetWithFilter(Dataset<Row> dataset, OutlierRemovalFilter filter, String columnName) {
        logger.info("Augmenting dataset with filter: " + filter);

        List<ThresholdCondition> thresholds = filter.getThresholds();
        if (thresholds.size() != 1) {
            throw new UnsupportedOperationException("Each filter supports only one threshold in this installment");
        }

        if (filter.getIdentityFields().isEmpty()) {
            return dataset.withColumn(columnName, dataset.col(thresholds.get(0).getColumnName()));
        } else {
            ThresholdCondition threshold = thresholds.get(0);
            List<String> identityFields = filter.getIdentityFields();

            RelationalGroupedDataset groupedDataset = dataset.groupBy(buildAggregationCondition(dataset, identityFields));

            Dataset<Row> aggregatedDataset = groupedDataset.agg(buildAggregation(threshold).as(columnName));

            return dataset.join(
                    aggregatedDataset,
                    buildJoinExpression(dataset, aggregatedDataset, identityFields),
                    "leftouter"
            ).select(buildSelection(dataset, columnName));
        }
    }

    private static Column[] buildSelection(Dataset<Row> dataset, String columnName) {
        final String[] datasetColumnNames = dataset.columns();
        final Column[] columns = new Column[datasetColumnNames.length + 1];
        
        for (int i = 0; i < datasetColumnNames.length; ++i) {
            columns[i] = dataset.col(datasetColumnNames[i]);
        }

        columns[datasetColumnNames.length] = col(columnName);

        return columns;
    }

    private Tuple2<Dataset<Row>, Column> addAggregationConstraints(List<OutlierRemovalFilter> withAggregation, Dataset<Row> dataset) {
        Column outlierCondition = null;
        Dataset<Row> operationalDataset = dataset;

        for (List<OutlierRemovalFilter> filtersWithSameAggregation : groupByIdentityFields(withAggregation)) {
            List<String> identityFields = filtersWithSameAggregation.get(0).getIdentityFields();
            RelationalGroupedDataset groupedDataset = dataset.groupBy(buildAggregationCondition(dataset, identityFields));

            List<Column> aggregationExpressions = new ArrayList<>();

            for (OutlierRemovalFilter filter : filtersWithSameAggregation) {
                Tuple2<List<Column>, Column> response = addAggregationFilter(filter);

                aggregationExpressions.addAll(response._1);

                if (outlierCondition == null) {
                    outlierCondition = response._2;
                } else {
                    outlierCondition = outlierCondition.or(response._2);
                }
            }

            Dataset<Row> aggregatedDataset = groupedDataset.agg(aggregationExpressions.get(0), aggregationExpressions.subList(1, aggregationExpressions.size()).toArray(new Column[0]));
            operationalDataset = operationalDataset.join(
                    aggregatedDataset,
                    buildJoinExpression(dataset, aggregatedDataset, identityFields),
                    "leftouter"
            );
        }

        return new Tuple2<>(operationalDataset, outlierCondition);
    }

    private Collection<? extends List<OutlierRemovalFilter>> groupByIdentityFields(List<OutlierRemovalFilter> filters) {
        Map<List<String>, List<OutlierRemovalFilter>> groups = new HashMap<>();

        for (OutlierRemovalFilter filter : filters) {
            List<String> ids = filter.getIdentityFields();
            if (groups.containsKey(ids)) {
                groups.get(ids).add(filter);
            } else {
                List<OutlierRemovalFilter> group = new ArrayList<>();
                group.add(filter);
                groups.put(ids, group);
            }
        }

        return groups.values();
    }

    private Tuple2<List<Column>, Column> addAggregationFilter(OutlierRemovalFilter filter) {
        Column filterCondition = null;
        List<Column> aggregationCondition = new ArrayList<>();

        for (ThresholdCondition threshold : filter.getThresholds()) {
            String aggregationName = UUID.randomUUID().toString();

            // update aggregation
            aggregationCondition.add(buildAggregation(threshold).as(aggregationName));

            // update filter
            Column filterColumn = buildFilterCondition(threshold, aggregationName);

            if (threshold.isTrueOnNull()) {
                filterColumn = col(aggregationName).isNull().or(filterColumn);
            } else {
                filterColumn = col(aggregationName).isNotNull().and(filterColumn);
            }

            if (filterCondition == null) {
                filterCondition = filterColumn;
            } else {
                filterCondition = filterCondition.and(filterColumn);
            }
        }

        return new Tuple2<>(aggregationCondition, filterCondition);
    }

    private static Column[] buildAggregationCondition(Dataset<Row> dataset, List<String> identityFields) {
        Column[] columns = new Column[identityFields.size()];

        for (int i = 0; i < columns.length; ++i) {
            columns[i] = dataset.col(identityFields.get(i));
        }

        return columns;
    }

    private Column addNonAggregatedConstraints(Dataset<Row> dataset, Column outlierCondition, List<OutlierRemovalFilter> filters) {
        for (OutlierRemovalFilter filter : filters) {
            final Column filterColumn = buildSimpleFiltering(dataset, filter.getThresholds());

            if (null == outlierCondition) {
                outlierCondition = filterColumn;
            } else {
                outlierCondition = outlierCondition.or(filterColumn);
            }
        }

        return outlierCondition;
    }

    private static Column buildJoinExpression(Dataset<Row> dataset, Dataset<Row> aggregatedDataset, Collection<String> identityColumns) {
        Column joinExpression = null;
        for (String identityColumn : identityColumns) {
            Column expression = dataset.col(identityColumn).equalTo(aggregatedDataset.col(identityColumn));

            if (null == joinExpression) {
                joinExpression = expression;
            } else {
                joinExpression = joinExpression.and(expression);
            }
        }

        return joinExpression;
    }

    private Column buildFilterCondition(ThresholdCondition condition, String aggregationName) {
        if (null == condition.getCondition()) return null;

        Column column = col(aggregationName);
        switch (condition.getCondition()) {
            case LT:
                return column.lt(condition.getValue());
            case LEQ:
                return column.leq(condition.getValue());
            case GT:
                return column.gt(condition.getValue());
            case GEQ:
                return column.geq(condition.getValue());
            case EQ:
                return column.equalTo(condition.getValue());
            case NEQ:
                return column.notEqual(condition.getValue());
            default:
                throw new IllegalArgumentException("Unknown condition");
        }
    }

    private static List<String> buildPartitioningField(List<String> partitioningFields, String filterColumnName) {
        List<String> fields = new ArrayList<>( partitioningFields.size() +  1);
        fields.add(filterColumnName);
        fields.addAll(partitioningFields);
        
        return new ArrayList<>(fields);
    }

    private Column buildSimpleFiltering(Dataset<Row> dataset, List<ThresholdCondition> conditions) {
        Column filter = null;

        for (ThresholdCondition condition : conditions) {
            Column original = dataset.col(condition.getColumnName());
            Column column = original;

            switch (condition.getCondition()) {
                case LT:
                    column = column.lt(condition.getValue());
                    break;
                case LEQ:
                    column = column.leq(condition.getValue());
                    break;
                case GT:
                    column = column.gt(condition.getValue());
                    break;
                case GEQ:
                    column = column.geq(condition.getValue());
                    break;
                case EQ:
                    column = column.equalTo(condition.getValue());
                    break;
                case NEQ:
                    column = column.notEqual(condition.getValue());
                    break;
            }

            if (condition.isTrueOnNull()) {
                column = original.isNull().or(column);
            } else {
                column = original.isNotNull().and(column);
            }

            if (null == filter) {
                filter = column;
            } else {
                filter = filter.and(column);
            }
        }

        return filter;
    }

    private boolean hasAtLeastOneAggregation(List<ThresholdCondition> conditions) {
        for (ThresholdCondition condition : conditions) {
            if (!condition.getAggregationType().equals(AggregationType.NOT_AGGREGATED)) return true;
        }
        return false;
    }

    private static Column buildAggregation(ThresholdCondition condition) {
        String columnName = condition.getColumnName();
        Column column = col(columnName);

        boolean properAggregation = false;

        switch (condition.getAggregationType()) {
            case SUM:
                column = sum(column);
                properAggregation = true;
                break;
            case COUNT:
                column = sum(lit(1L));
                properAggregation = true;
                break;
            case MAX:
                column = max(column);
                properAggregation = true;
                break;
            case MIN:
                column = min(column);
                properAggregation = true;
                break;
            case NOT_AGGREGATED:
                break;
            default:
                throw new IllegalArgumentException("Unknown aggregation type");
        }

        if (!  properAggregation) throw new IllegalArgumentException("No aggregation values specified");

        return column;
    }

    private static CommandLine createAndParseCommandLine(String[] args) throws ParseException {
        Options options = buildOptions();
        CommandLineParser parser = new PosixParser();
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
        o.setRequired(true);
        Option basePath = new Option("basePath", false, "Remote configuration files (optional)");
        Option a = new Option("a", false, "Append");
        Option p = new Option("p", true, "Partitions");

        return new Options().
                addOption(i).
                addOption(a).
                addOption(p).
                addOption(d).
                addOption(q).
                addOption(h).
                addOption(c).
                addOption(basePath).
                addOption(t).
                addOption(o);
    }
}
