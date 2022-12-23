/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.anonymization.mondrian;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.mondrian.CategoricalSplitStrategy;
import com.ibm.research.drl.dpt.anonymization.mondrian.Interval;
import com.ibm.research.drl.dpt.anonymization.mondrian.Mondrian;
import com.ibm.research.drl.dpt.anonymization.mondrian.MondrianPartition;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import org.apache.commons.csv.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import scala.Tuple3;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MondrianSpark {
   
    private static void anonymize(MondrianSparkPartition partition, int level, List<MondrianSparkPartition> finalPartitions) {
        if(!partition.isSplittable()) {
            finalPartitions.add(partition);
            return;
        }

        int dim = partition.chooseDimension();

        long start = System.currentTimeMillis();
        List<MondrianSparkPartition> subPartitions = partition.split(dim, level);
        System.out.println("split took: " + (System.currentTimeMillis() - start));
        
        System.out.println(level + ":" + subPartitions.size());
        
        if (subPartitions.size() == 0) {
            partition.disallow(dim);
            anonymize(partition, level + 1, finalPartitions);
        }  else {
            for(MondrianSparkPartition p: subPartitions) {
                System.out.println(level + " :: " + p.getMember().count());
                anonymize(p, level + 1, finalPartitions);
            }
        }
    }
    
    public static JavaRDD<String> anonymizePartition(MondrianSparkPartition partition, final List<Integer> quasiColumns,
                                                     final List<Integer> nonQuasiColumns, List<ColumnInformation> columnInformationList,
                                                     CategoricalSplitStrategy categoricalSplitStrategy) {

        /*
        if (categoricalSplitStrategy == CategoricalSplitStrategy.ORDER_BASED) {
            commonAncestors = calculateCommonAncestors(partition, columnInformationList);
        }*/

        JavaRDD<String[]> input = partition.getMember();
       
        final List<String> middle = partition.getMiddle();
        
        return input.map((Function<String[], String>) s -> {
            List<String> anonymizedRow = new ArrayList<>(s.length);

            for (String ignored : s) {
                anonymizedRow.add(null);
            }

            for (Integer quasiColumn : quasiColumns) {
                int quasiCol = quasiColumn;
                ColumnInformation columnInformation = columnInformationList.get(quasiCol);
                if (!columnInformation.isCategorical() || categoricalSplitStrategy == CategoricalSplitStrategy.HIERARCHY_BASED) {
                    anonymizedRow.set(quasiCol, middle.get(quasiCol));
                } else {
                    throw new RuntimeException("not implemented yet");
                }
            }

            for(Integer k: nonQuasiColumns) {
                anonymizedRow.set(k, s[k]);
            }

            try (StringWriter stringWriter = new StringWriter();
                CSVPrinter writer = new CSVPrinter(stringWriter, CSVFormat.RFC4180.withDelimiter(',').withQuoteMode(QuoteMode.MINIMAL))) {
                writer.printRecord(anonymizedRow);
                return stringWriter.toString().trim();
            }
        });
        
    }

    private static JavaRDD<String> getAnonymizedDataset(List<MondrianSparkPartition> finalPartitions, 
                                                        List<ColumnInformation> columnInformationList, CategoricalSplitStrategy categoricalSplitStrategy) {

        List<Integer> quasiColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.QUASI);;
        List<Integer> nonQuasiColumns = Mondrian.getNonQuasiColumns(columnInformationList.size(), quasiColumns);
        
        JavaRDD<String> anonUnion = null; 
        
        for(MondrianSparkPartition partition: finalPartitions) {
            JavaRDD<String> anonP = anonymizePartition(partition, quasiColumns, nonQuasiColumns, columnInformationList, categoricalSplitStrategy);
            if (anonUnion == null) {
                anonUnion = anonP;
            }
            else {
                anonUnion = anonUnion.union(anonP);
            }
        }

        return anonUnion;
    }

    public static JavaRDD<String> run(InputStream confStream, JavaRDD<String> inputRDD) throws IOException, MisconfigurationException {
        AnonymizationOptions anonymizationOptions = new ObjectMapper().readValue(confStream, AnonymizationOptions.class);

        JavaRDD<String[]> inputParsed = inputRDD.map((Function<String, String[]>) s -> {
            CSVParser parser = CSVParser.parse(s, CSVFormat.RFC4180.withDelimiter(','));
            CSVRecord csvRecord = parser.getRecords().get(0);

            String[] results = new String[csvRecord.size()];

            for(int i = 0; i < csvRecord.size(); i++) {
                results[i] = csvRecord.get(i);
            }

            return results;
        });
        
        List<String> middle = new ArrayList<>();
        List<Interval> width = new ArrayList<>();
        
        List<ColumnInformation> columnInformationList = anonymizationOptions.getColumnInformation();
        
        for(int i = 0; i < columnInformationList.size(); i++) {
            ColumnInformation columnInformation = columnInformationList.get(i);

            if (columnInformation.getColumnType() == ColumnType.QUASI) {
                if (!columnInformation.isCategorical()) {
                    Tuple3<Double, Double, Double> numericalInformation = MondrianSparkUtils.findMedian(
                            MondrianSparkUtils.extractValues(inputParsed, i, true));
                    
                    double low = numericalInformation._1();
                    double max = numericalInformation._2();
                   
                    columnInformationList.set(i, new NumericalRange(Arrays.asList(low, max), ColumnType.QUASI)); 
                    
                    middle.add(MondrianPartition.generateMiddleKey(low, max));
                    width.add(new Interval(low, max, numericalInformation._3()));
                } else {
                    CategoricalInformation categoricalInformation = (CategoricalInformation) columnInformation;
                    String topTerm = categoricalInformation.getHierarchy().getTopTerm();
                    middle.add(topTerm.toUpperCase());
                    width.add(new Interval(0d, MondrianSparkUtils.calculateCardinality(inputParsed, i)));
                }
            }
            else {
                middle.add(null);
                width.add(null);
            }
        }

        CategoricalSplitStrategy categoricalSplitStrategy = CategoricalSplitStrategy.ORDER_BASED;
        List<MondrianSparkPartition> finalPartitions = new ArrayList<>();
        anonymize(new MondrianSparkPartition(inputParsed, middle, width, anonymizationOptions.getColumnInformation(), 
                anonymizationOptions.getPrivacyConstraints(), categoricalSplitStrategy), 0, finalPartitions);

        return getAnonymizedDataset(finalPartitions, anonymizationOptions.getColumnInformation(), categoricalSplitStrategy);
    }

}
