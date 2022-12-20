/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.apache.spark.sql.functions.*;


public class DatasetLinkingRiskEstimator {
    private static final Logger logger = LogManager.getLogger(DatasetLinkingRiskEstimator.class);
    private static final String RISK_LABEL = "___RISK___";
    private static final String COUNT_COLUMN = "__COUNT__";
    private static final String COUNT_DEMOGRAPHIC = "__COUNT_DEMOGRAPHIC__";
    private final Dataset<Row> knowledgeBase;
    
    
    public static void main(String[] args) throws IOException {
        Options options = SparkUtils.buildCommonCommandLineOptions();
        options.addOption("basePath", true, "Base path for reading partitioned data");

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);

            DatasetLinkingOptions configuration =
                    SparkUtils.deserializeConfiguration(cmd.getOptionValue("c"), DatasetLinkingOptions.class);

            logger.info("input: " + cmd.getOptionValue("i"));
            logger.info("output: " + cmd.getOptionValue("o"));
            logger.info("configuration file: " + (new ObjectMapper()).writeValueAsString(configuration));
            
            SparkSession sparkSession = SparkUtils.createSparkSession("Dataset Linking");

            String basePath = cmd.hasOption("basePath") ? cmd.getOptionValue("basePath") : null;
            Dataset<Row> dataset = SparkUtils.createDataset(sparkSession, cmd.getOptionValue("i"), 
                    configuration.getInputFormat(), configuration.getInputDatasetOptions(), basePath);
            
            Dataset<Row> knowledgeBase = SparkUtils.createDataset(sparkSession, configuration.getTarget(),
                    configuration.getTargetFormat(), configuration.getTargetDatasetOptions());
            
            
            DatasetLinkingRiskEstimator riskEstimator = new DatasetLinkingRiskEstimator(knowledgeBase);
            
            riskEstimator.estimateLinkingRiskForAnonymized(dataset, configuration, cmd.getOptionValue("o"));
            
        } catch (ParseException e) {
            String header = "Dataset Linker\n\n";
            String footer = "\n";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DatasetLinker", header, options, footer, true);
            throw new RuntimeException("invalid arguments");
        }
    } 
    
    private DatasetLinkingRiskEstimator(Dataset<Row> knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    protected static int countAnonymisedColumns(List<BasicColumnInformation> basicColumnInformation) {
        int count = 0;
        for(BasicColumnInformation bci: basicColumnInformation) {
            if (bci.isAnonymised()) {
                count++;
            }
        }
        
        return count;
    }
    
    public void estimateLinkingRiskForAnonymized(Dataset<Row> dataset,
                                                                DatasetLinkingOptions configuration, String outputPath) {
        
        final List<BasicColumnInformation> basicColumnInformation = configuration.getColumnInformation();

        final Column[] groupByCondition = extractGroupByCondition(basicColumnInformation, dataset, false);
        Dataset<Row> classes = extractEquivalenceClasses(dataset, groupByCondition, COUNT_COLUMN);

        final Column[] groupByConditionKB = extractGroupByCondition(basicColumnInformation, this.knowledgeBase, true);
        Dataset<Row> knowledgeBaseClasses;
        
        if (configuration.isTargetAggregated()) {
            knowledgeBaseClasses = this.knowledgeBase.withColumnRenamed(configuration.getTargetCountColumn(), COUNT_DEMOGRAPHIC);
        }
        else {
            knowledgeBaseClasses = extractEquivalenceClasses(this.knowledgeBase, groupByConditionKB, COUNT_DEMOGRAPHIC);
        }
        
        logger.info("Extracted equivalence classes: #" + classes.count());

        List<List<Integer>> generalizationLevels = filterDistinctGeneralizationLevels(dataset, basicColumnInformation);

        logger.info("Extracted generalization levels: " + generalizationLevels.size());
        
        final int anonymizedColumnsCount = countAnonymisedColumns(basicColumnInformation);

        logger.info("Anonymized columns count " + anonymizedColumnsCount);
        
        generalizationLevels.forEach( generalizationLevel -> {
            Dataset<Row> glDataset = extractDataset(classes, generalizationLevel, anonymizedColumnsCount , basicColumnInformation);

            Dataset<Row> modifiedCensus = generalizeKnowledgeBase(knowledgeBaseClasses, generalizationLevel, basicColumnInformation);
            
            final Column joinCondition = extractJoinCondition(basicColumnInformation, glDataset, modifiedCensus);
            Dataset<Row> joint = modifiedCensus.join(glDataset, joinCondition);
           
            final Column[] groupByConditionInner = extractGroupByCondition(basicColumnInformation, glDataset, false);
            final Column[] groupByConditionForRisk = extractRiskGroupByCondition(groupByConditionInner);
            
            Dataset<Row> aggregated = joint.groupBy(groupByConditionForRisk).agg(sum(col(COUNT_DEMOGRAPHIC)).alias(COUNT_DEMOGRAPHIC));;
            Dataset<Row> risk = aggregated.select(col(COUNT_COLUMN), lit(1d).divide(col(COUNT_DEMOGRAPHIC)).alias(RISK_LABEL));

            risk.select(COUNT_COLUMN, RISK_LABEL).write().mode(SaveMode.Append).csv(outputPath);
        });
        
    }

    private Column[] extractRiskGroupByCondition(Column[] originalGroupByCondition) {
        Column[] riskGroupByCondition = Arrays.copyOf(originalGroupByCondition, originalGroupByCondition.length + 1);

        riskGroupByCondition[originalGroupByCondition.length] = col(COUNT_COLUMN);

        return riskGroupByCondition;
    }

    private static Dataset<Row> generalizeKnowledgeBase(
            Dataset<Row> knowledgeBase,
            List<Integer> generalizationLevel, 
            List<BasicColumnInformation> basicColumnInformation
    ) {
        final String[] names = knowledgeBase.columns();
        JavaRDD<Object[]> rdd = knowledgeBase.rdd().toJavaRDD().map(row -> {
            Object[] o = new Object[row.length()];

            for (int i = 0; i < o.length; ++i) {
                o[i] = row.get(i);
            }

            return o;
        });

        int quasiIndex = 0;
        
        for (int i = 0; i < basicColumnInformation.size(); ++i) {
            final BasicColumnInformation bci = basicColumnInformation.get(i);
            
            if (!bci.isAnonymised()) {
                continue;
            }
            
            final int level = generalizationLevel.get(quasiIndex);
            quasiIndex++;
            
            if (0 == level) continue;

            final String target = bci.getTarget();

            final GeneralizationHierarchy hierarchy = bci.getHierarchy();

            final int position = ArrayUtils.indexOf(names, target);

            if (position < 0) continue;

            rdd = rdd.map(row -> {
                if (position < row.length) {
                    Object rowValue = row[position];

                    if (Objects.nonNull(rowValue)) {
                        String originalValue = rowValue.toString();
                        String encodedValue = hierarchy.encode(originalValue, level, false);

                        row[position] = encodedValue;
                    }
                } else {
                    logger.error(String.format("SOMETHING WENT WRONG! %s: %d >= %d", target, position, row.length));
                }

                return row;
            });
        }

        return knowledgeBase.sparkSession().createDataFrame(rdd.map(RowFactory::create), knowledgeBase.schema());
    }

    protected static Dataset<Row> extractDataset(
            Dataset<Row> dataset,
            List<Integer> generalizationLevel,
            int generalizedColumnsCount,
            List<BasicColumnInformation> basicColumnInformation) 
    {
        
        if (generalizedColumnsCount == 0) {
            return dataset;
        }
        
        return dataset.filter((Row row) -> {
            List<Integer> levels = new ArrayList<>();
            
            for(BasicColumnInformation bci: basicColumnInformation) {
                if (!bci.isAnonymised()) {
                    continue;
                }

                String name = bci.getName();
                final GeneralizationHierarchy hierarchy = bci.getHierarchy();
                String value = row.getString(row.fieldIndex(name));

                int level = hierarchy.getNodeLevel(value);
                levels.add(level);
            }
            
            return levels.equals(generalizationLevel);
        });
        
    }

    protected static List<List<Integer>> filterDistinctGeneralizationLevels(Dataset<Row> anonymisedDataset, 
                                                                   List<BasicColumnInformation> basicColumnInformation) {
        final int anonymizedColumnsCount = countAnonymisedColumns(basicColumnInformation);
      
        if (anonymizedColumnsCount == 0) {
            List<Integer> zeros = new ArrayList<>(basicColumnInformation.size());
            for(int i = 0; i < basicColumnInformation.size(); i++) {
                zeros.add(0);
            }
            return Arrays.asList(zeros);
        }
        
        List<String> uniqueLevelsAsString = anonymisedDataset.javaRDD().map(row -> {
            StringBuilder builder = new StringBuilder();

            for(BasicColumnInformation bci: basicColumnInformation) {
                if (!bci.isAnonymised()) {
                    continue;
                }

                String name = bci.getName();
                final GeneralizationHierarchy hierarchy = bci.getHierarchy();
                String value = row.getString(row.fieldIndex(name));

                int level = hierarchy.getNodeLevel(value);

                builder.append(level).append(":");
            }

            return builder.toString();
        }).distinct().collect();
        
        List<List<Integer>> distinctLevels = new ArrayList<>();
        
        for(String levelS: uniqueLevelsAsString) {
            List<Integer> levels = convertToList(levelS);
            distinctLevels.add(levels);
        }
        
        return distinctLevels;
    }

    protected static List<Integer> convertToList(String levelS) {
        List<Integer> results = new ArrayList<>();

        for(String token: levelS.split(":")) {
            results.add(Integer.valueOf(token));
        }
        
        return results;
    }

    private Column extractJoinCondition(List<BasicColumnInformation> basicColumnInformation, Dataset<Row> dataset, Dataset<Row> kb) {
        return basicColumnInformation.stream().map( bci -> {
            String name = bci.getName();
            String target = bci.getTarget();

            if (null == target) return null;

            return kb.col(target).equalTo(dataset.col(name));
        }).filter(Objects::nonNull).reduce(Column::and).orElseThrow(RuntimeException::new);
    }


    private Column[] extractGroupByCondition(List<BasicColumnInformation> basicColumnInformation, Dataset<Row> dataset, boolean useTargetName) {
        
        Column[] condition = new Column[basicColumnInformation.size()];
        
        for(int i = 0; i < basicColumnInformation.size(); i++) {
            BasicColumnInformation bci = basicColumnInformation.get(i);
            String name = useTargetName ? bci.getTarget() : bci.getName();
            condition[i] = dataset.col(name);    
        }
        
        return condition;
    }

    private Dataset<Row> extractEquivalenceClasses(Dataset<Row> dataset, Column[] groupByCondition, String countColumnName) {
        return dataset.groupBy(groupByCondition).agg(count("*").alias(countColumnName));
    }
}
