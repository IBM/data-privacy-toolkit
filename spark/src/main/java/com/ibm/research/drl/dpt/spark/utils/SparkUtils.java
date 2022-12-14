/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.CSVFormatProcessor;
import com.ibm.research.drl.dpt.providers.ProviderType;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.spark.sql.functions.col;


public class SparkUtils {

    public static Options buildCommonCommandLineOptions() {
        Options options = new Options();

        options.addOption(
                Option.builder("c").longOpt("conf").hasArg().required().desc("configuration file").build()
        );
        options.addOption(
                Option.builder("i").longOpt("input").hasArg().required().desc("json reference to input").build()
        );
        options.addOption(
                Option.builder("o").longOpt("output").hasArg().required().desc("json reference to output").build()
        );

        return options;
    }

    public static InputStream readFile(String path) throws IOException {
        return readFile(path, isRemote(path));
    }

    private static boolean isRemote(String path) {
        return path.startsWith("hdfs://");
    }

    private static InputStream readFile(String path, boolean isRemote) throws IOException {
        if (isRemote) {
            return SparkUtils.createHDFSInputStream(path);
        }
        return new FileInputStream(path);
    }

    public static JsonNode readConfigurationFile(String configurationFile) throws IOException {
        final String lowerCasePath = configurationFile.toLowerCase();
        final ObjectMapper mapper;

        if (lowerCasePath.endsWith(".json")) {
            mapper = new ObjectMapper();
        } else if (lowerCasePath.endsWith(".yaml") || lowerCasePath.endsWith(".yml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else {
            throw new IllegalArgumentException("Unknown extension " + configurationFile);
        }

        try (InputStream inputStream = readFile(configurationFile)) {
            return mapper.readTree(inputStream);
        }
    }

    /**
     * Create spark context spark context.
     *
     * @return the spark context
     */
    public static SparkContext createSparkContext(String appName) {
        SparkConf configuration = new SparkConf().setAppName(appName);

        configuration.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        configuration.registerKryoClasses(new Class[]{ProviderType.class});

        return new SparkContext(configuration);
    }

    public static SparkSession createSparkSession(String appName) {
        return SparkSession.builder().appName(appName).getOrCreate();
    }

    public static SparkSession createSparkSession(String appName, String master) {
        return SparkSession.builder().appName(appName).master(master).getOrCreate();
    }

    /**
     * Create rdd java rdd.
     *
     * @param context the context
     * @param input   the input
     * @return the java rdd
     */
    public static JavaRDD<String> createTextFileRDD(SparkContext context, String input) {
        RDD<String> rdd = context.textFile(input, 16);
        return rdd.toJavaRDD();
    }

    public static JavaRDD<String> createTextFileRDD(JavaSparkContext context, String input) {
        return context.textFile(input);
    }

    public static InputStream createHDFSInputStream(String path) throws IOException {
        Path pt = new Path(path);
        FileSystem fs = FileSystem.get(new Configuration());
        return fs.open(pt);
    }

    public static OutputStream createHDFSOutputStream(String path) throws IOException {
        return createHDFSOutputStream(path, false);
    }

    private static OutputStream createHDFSOutputStream(String path, boolean deleteIfExists) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Path pt = new Path(path);

        if (deleteIfExists && fs.exists(pt)) {
            fs.delete(pt, Boolean.TRUE);
        }

        return fs.create(pt, Boolean.TRUE);
    }

    public static Dataset<Row> createDataset(SparkSession sparkSession, String input, DataTypeFormat inputFormat, DatasetOptions datasetOptions) {
        return createDataset(sparkSession, input, inputFormat, datasetOptions, null);
    }

    public static Dataset<Row> createDataset(SparkSession sparkSession, String input, DataTypeFormat inputFormat,
                                             DatasetOptions datasetOptions, String basePath) {
        DataFrameReader reader = sparkSession.read();

        if (basePath != null) {
            reader = reader.option("basePath", basePath);
        }

        switch (inputFormat) {
            case CSV:
                CSVDatasetOptions csvDatasetOptions = (CSVDatasetOptions) datasetOptions;
                reader = reader.option("sep", Character.toString(csvDatasetOptions.getFieldDelimiter())).option("quote", Character.toString(csvDatasetOptions.getQuoteChar()));

                if (csvDatasetOptions.isHasHeader()) {
                    reader = reader.option("header", true);
                }

                if (csvDatasetOptions.isTrimFields()) {
                    reader = reader.option("ignoreLeadingWhiteSpace", true).option("ignoreTrailingWhiteSpace", true);
                }

                Dataset<Row> dataset = reader.csv(input);

                if (!csvDatasetOptions.isHasHeader()) {
                    int numberOfFields = dataset.schema().fields().length;
                    String[] columnNames = CSVFormatProcessor.generateColumnNames(numberOfFields);
                    return dataset.toDF(columnNames);
                }

                return dataset;

            case PARQUET:
                return reader.parquet(input);
            default:
                return reader.text(input);
        }

    }

    public static List<String> createFieldNames(Dataset<Row> rdd, DataTypeFormat format, DatasetOptions datasetOptions) {

        if (format != DataTypeFormat.CSV && format != DataTypeFormat.PARQUET) {
            return null;
        }

        StructType schema = rdd.schema();
        List<String> fieldNames = new ArrayList<>(schema.fields().length);

        if (format == DataTypeFormat.CSV) {
            CSVDatasetOptions csvDatasetOptions = (CSVDatasetOptions) datasetOptions;

            if (csvDatasetOptions.isHasHeader()) {
                for (StructField field : schema.fields()) {
                    fieldNames.add(field.name());
                }
            } else {
                String[] columnNames = CSVFormatProcessor.generateColumnNames(schema.fields().length);
                fieldNames = Arrays.asList(columnNames);
            }
        } else {
            for (int i = 0; i < schema.fields().length; i++) {
                fieldNames.add(null);
            }

            for (StructField field : schema.fields()) {
                scala.Option<Object> option = schema.getFieldIndex(field.name());
                if (!option.isDefined()) {
                    throw new RuntimeException("undefined field: " + field.name());
                }
                fieldNames.set((Integer) option.get(), field.name());
            }
        }

        return fieldNames;
    }

    public static <T> T deserializeConfiguration(String path, Class<T> configurationClass) throws IOException {
        try (InputStream inputStream = readFile(path)) {
            String lowerCasePath = path.toLowerCase();
            final ObjectMapper mapper;

            if (lowerCasePath.endsWith(".json")) {
                mapper = new ObjectMapper();
            } else if (lowerCasePath.endsWith(".yaml") || lowerCasePath.endsWith(".yml")) {
                mapper = new ObjectMapper(new YAMLFactory());
            } else {
                throw new IllegalArgumentException("Unknown extension " + path);
            }

            return mapper.readValue(inputStream, configurationClass);
        }
    }

    public static OutputStream createOutputStream(String outputPath) throws IOException {
        return createOutputStream(outputPath, isRemote(outputPath));
    }

    private static OutputStream createOutputStream(String outputPath, boolean isRemote) throws IOException {
        if (isRemote) {
            return SparkUtils.createHDFSOutputStream(outputPath);
        } else {
            return new FileOutputStream(outputPath);
        }
    }

    public static Dataset<Row> filterOnFieldValue(Dataset<Row> dataset, String fieldName, String regularExpression) {
        return dataset.where(col(fieldName).cast(DataTypes.StringType).rlike(regularExpression));
    }
}
