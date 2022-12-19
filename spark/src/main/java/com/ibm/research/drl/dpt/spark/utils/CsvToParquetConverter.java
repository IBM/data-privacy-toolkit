/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.spark.export.Export;
import org.apache.commons.cli.*;
import org.apache.commons.csv.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.DecimalType;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.apache.spark.sql.functions.*;

public class CsvToParquetConverter {
    private static final Logger logger = LogManager.getLogger(CsvToParquetConverter.class);

    public static void main(String[] args) {
        try {
            CommandLine cmd = createAndParseCommandLine(args);

            SparkSession spark = SparkSession.builder().sparkContext(new SparkContext(new SparkConf(true))).getOrCreate();

            boolean remoteConf = cmd.hasOption("remoteConf");
            String pathToSchemaFile = cmd.getOptionValue("s");

            JsonNode configuration =  SparkUtils.readConfigurationFile(pathToSchemaFile);
            logger.info("configuration: " +  configuration.toString());
            
            StructType schema = buildSchema(configuration);
            for(StructField field: schema.fields()) {
                logger.info("Field from template: " + field.name() + ", type:" + field.dataType().typeName() + ", nullable: " + field.nullable());
            }
            
            Map<String, String> timestampFormats = getTimestampFormats(configuration);
            List<String> nullableDates = getNullableDates(schema);
            
            final String mode = "FAILFAST";
            String userProviderDelimiter = cmd.getOptionValue("d", ",");
            String userProvidedQuoteChar = cmd.getOptionValue("q", "\"");
            boolean userProvidedHasHeader = cmd.hasOption("h");
            
            String quoteMode = cmd.getOptionValue("quoteMode", "ALL");
           
            
            logger.info("delimiter: " + userProviderDelimiter);
            logger.info("quote char: >" + userProvidedQuoteChar + "<");
            logger.info("quote mode: " + quoteMode);
            
            logger.info("parse mode: " + mode);
            logger.info("has header: " + userProvidedHasHeader);
            logger.info("schema: " + StringUtils.join(schema.fieldNames(), ','));

            if (userProviderDelimiter.length() > 1) {
                userProviderDelimiter = StringEscapeUtils.unescapeJava(userProviderDelimiter);
            }
            
            final String finalDelimiter;
            final String finalQuoteChar;
            final boolean finalHasHeader;
            
            JavaRDD<String> stringRDD = null;
            
            if(quoteMode.equals("NONE")) {
                stringRDD = spark.read().textFile(cmd.getOptionValue("i")).javaRDD();
                
                finalDelimiter = ",";
                finalQuoteChar = "\"";
                finalHasHeader = false;
               
                if (userProvidedHasHeader) {
                    stringRDD = stringRDD.zipWithIndex().filter( tuple -> (tuple._2 > 0)).keys();
                }
                
                logger.info("manually parsing data");
                final char delimForQuoteNone = userProviderDelimiter.charAt(0);
                
                stringRDD = stringRDD.map( line -> {
                    CSVParser csvParser = CSVParser.parse(line,
                            CSVFormat.RFC4180.withDelimiter(delimForQuoteNone).
                                    withQuote(userProvidedQuoteChar.isEmpty() ? null : userProvidedQuoteChar.charAt(0)).
                                    withEscape('\\').
                                    withQuoteMode(QuoteMode.NONE));

                    CSVRecord record = csvParser.getRecords().get(0);

                    try(StringWriter stringWriter = new StringWriter()) {
                        CSVPrinter csvPrinter = new CSVPrinter(stringWriter,
                                CSVFormat.RFC4180.withDelimiter(finalDelimiter.charAt(0)).
                                        withQuote(finalQuoteChar.charAt(0)).
                                        withQuoteMode(QuoteMode.MINIMAL));
                        csvPrinter.printRecord(record);

                        return stringWriter.toString();
                    }
                });
            } else {
                finalDelimiter = userProviderDelimiter;
                finalQuoteChar = userProvidedQuoteChar;
                finalHasHeader = userProvidedHasHeader;
            }
            
            DataFrameReader dataFrameReader = spark.read()
                    .option("header", finalHasHeader)
                    .option("delimiter", finalDelimiter)
                    .option("quote", finalQuoteChar)
                    .option("ignoreLeadingWhiteSpace", true)
                    .option("ignoreTrailingWhiteSpace", true)
                    .option("inferSchema", false)
                    .option("mode", mode);

            if (cmd.hasOption("charSet")) {
                logger.info("charSet: " + cmd.getOptionValue("charSet"));
                dataFrameReader = dataFrameReader.option("charset", cmd.getOptionValue("charSet"));
            }
            
            int uniqueTimestampFormats = countUniqueTimestampFormats(timestampFormats);

            StructType originalSchema = null;

            if (uniqueTimestampFormats == 1) {
                String tsFormat = timestampFormats.values().iterator().next();
                logger.info("we have a unique timestamp format: " + tsFormat);
                if (null != tsFormat) {
                    dataFrameReader = dataFrameReader.option("timestampFormat", tsFormat);
                }
            } 
            
            if (! nullableDates.isEmpty() || uniqueTimestampFormats > 1 ) {
                originalSchema = schema;
                schema = removeTimeStampFieldsAndNullableDates(schema);
            }

            Dataset<Row> dataset;
            
            if (quoteMode.equals("NONE")) {
                dataset = dataFrameReader.schema(schema).csv(spark.createDataset(stringRDD.rdd(), Encoders.STRING()));
            } else {
                dataset = dataFrameReader.schema(schema).csv(cmd.getOptionValue("i"));
            }

            if (null != originalSchema) {
                dataset = convertDatasetToOriginalSchema(dataset, originalSchema, timestampFormats);
            }
            
            checkNotNullableAndValue(dataset, schema, cmd.getOptionValue("checkField"), cmd.getOptionValue("checkValue"));
            
            List<String> partitions = Collections.emptyList();
            if (cmd.hasOption("p")) {
                partitions = Arrays.asList(cmd.getOptionValue("p").split(","));
            }
            
            logger.info("partitions: " + StringUtils.join(partitions));
            
            Export.doExport(dataset, DataTypeFormat.PARQUET, cmd.getOptionValue("o"),
                    partitions, cmd.hasOption("a"));

        } catch (ParseException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private static List<String> getNullableDates(StructType schema) {
        final StructField[] fields = schema.fields();

        final List<String> fieldsWithDates = new ArrayList<>(fields.length);
        for (StructField field : fields) {
            if (field.nullable() && field.dataType().sameType(DataTypes.DateType)) {
                fieldsWithDates.add(field.name());
            }
        }

        return fieldsWithDates;
    }

    private static Dataset<Row> convertDatasetToOriginalSchema(final Dataset<Row> dataset, 
                                                               final StructType originalSchema, final Map<String, String> timestampFormats) {
        final StructField[] fields = originalSchema.fields();
        final Column[] columns = new Column[fields.length];

        for (int i = 0; i < fields.length; ++i) {
            StructField field = fields[i];
            Column column = col(field.name());

            if (field.dataType().sameType(DataTypes.TimestampType)) {
                String format = timestampFormats.get(field.name());
                if (null != format) {
                    logger.info("will apply to_timestamp for " + field.name() + ", format is " + format);
                    column = unix_timestamp(column, format);
                } else {
                    logger.info("will apply to_timestamp for " + field.name() + ", no format specified");
                    column = unix_timestamp(column);
                }
                column = column.cast(DataTypes.TimestampType);
            } else if (field.dataType().sameType(DataTypes.DateType) && field.nullable()) {
                column = to_date(column);
                column = column.cast(DataTypes.DateType);
            }

            columns[i] = column.as(field.name());
        }

        return dataset.select(
                columns
        );
    }

    private static StructType removeTimeStampFieldsAndNullableDates(StructType schema) {
        StructField[] fields = Arrays.copyOf(schema.fields(), schema.fields().length);

        for (int i = 0; i < fields.length; ++i) {
            StructField field = fields[i];
            if (
                    field.dataType().sameType(DataTypes.TimestampType)
                            || (field.dataType().sameType(DataTypes.DateType) && field.nullable())
                    ) {
                fields[i] = new StructField(
                        field.name(),
                        DataTypes.StringType,
                        field.nullable(),
                        field.metadata()
                );
            }
        }

        return new StructType(fields);
    }

    public static void checkNotNullableAndValue(Dataset<Row> dataset, StructType schema, String checkField, String value) {
        
        boolean needsCheck = false;

        for(StructField field: schema.fields()) {
            if (!field.nullable()) {
                needsCheck = true;
                break;
            }
        }

        if (Objects.nonNull(checkField) && Objects.nonNull(value)) {
            needsCheck = true;
        }
        
        if (!needsCheck) {
            return;
        }
        
        dataset.foreach((ForeachFunction<Row>) row -> {
            for(StructField field: schema.fields()) {
                if (!field.nullable()) {
                    int fieldIndex = row.fieldIndex(field.name());
                    Object v = row.get(fieldIndex);

                    if (v == null) {
                        throw new RuntimeException("field is null: " + field.name());
                    }

                    String s = v.toString();
                    if (s.isEmpty()) {
                        throw new RuntimeException("field is empty: " + field.name());
                    }
                }
            }
            if (Objects.nonNull(checkField) && Objects.nonNull(value)) {
                int fieldIndex = row.fieldIndex(checkField);
                Object v = row.get(fieldIndex);

                if (v == null) {
                    logger.error("Field to check is null: " + checkField);
                    throw new RuntimeException("field to check is null: " + checkField);
                }
                String s = v.toString();
                if (!s.equals(value)) {
                    logger.error("Unexpected value in row " + checkField);
                    throw new RuntimeException("Unexpected value in row " + checkField);
                }
            }
        });
    }

    protected static int countUniqueTimestampFormats(Map<String,String> timestampFormats) {
        Set<String> set = new HashSet<>(timestampFormats.values());
        return set.size();
    }

    protected static Map<String,String> getTimestampFormats(JsonNode fields) {
        Map<String, String> timestampFormats = new HashMap<>();

        fields.forEach(
                fieldElement -> {
                    String dt = fieldElement.get("type").asText();
                    if (dt.equals("timestamp")) {
                        String fieldName = fieldElement.get("fieldName").asText();
                        if (fieldElement.has("format")) {
                            timestampFormats.put(fieldName, fieldElement.get("format").asText());
                        } else {
                            timestampFormats.put(fieldName, null);
                        }
                    }
                }
        );
        
        return timestampFormats;
    }

    public static StructType buildSchema(JsonNode fields) {
        List<StructField> fieldList = new ArrayList<>();

        fields.forEach(
                fieldElement -> {
                    fieldList.add(new StructField(
                            fieldElement.get("fieldName").asText(),
                            converToDataType(fieldElement.get("type")),
                            isNullable(fieldElement.get("nullable")),
                            Metadata.empty()
                    ));
                }
        );

        return new StructType(fieldList.toArray(new StructField[0]));
    }

    private static DataType converToDataType(JsonNode type) {
        switch(type.asText().split("\\(")[0].toLowerCase()) {
            case "binary":
                return DataTypes.BinaryType;

            case "boolean":
                return DataTypes.BooleanType;

            case "byte":
                return DataTypes.ByteType;

            case "interval":
                return DataTypes.CalendarIntervalType;

            case "date":
                return DataTypes.DateType;

            case "numeric":
            case "decimal":
                return extractDecimalType(type.asText().toLowerCase());
                
            case "double":
                return DataTypes.DoubleType;

            case "float":
                return DataTypes.FloatType;

            case "int":
            case "int32":
                return DataTypes.IntegerType;
                
            case "int64":
            case "long":
                return DataTypes.LongType;

            case "short":
                return DataTypes.ShortType;

            case "timestamp":
                return DataTypes.TimestampType;

            case "string":
            case "varchar":
            case "char":
                return DataTypes.StringType;

            case "null":
                return DataTypes.NullType;

            default:
                throw new IllegalArgumentException("UnknownType: " + type.asText());
        }
    }

    public  static DataType extractDecimalType(String s) {
        int idx = s.indexOf('(');
        
        if (idx == -1) {
            return new DecimalType();
        }
        
        int idx2 = s.indexOf(')', idx);
        
        if (idx2 == -1) {
            throw new IllegalArgumentException("Unclosed parenthesis in decimal type: " + s);
        }
       
        String internal = s.substring(idx + 1, idx2);
        String[] toks = internal.split(",");
        
        if (toks.length > 2) {
            throw new IllegalArgumentException("Too many values in precision, scale part: " + s);
        }
        
        if (toks.length == 1) {
            return new DecimalType(Integer.parseInt(toks[0].trim()));
        }

        return new DecimalType(Integer.parseInt(toks[0].trim()), Integer.parseInt(toks[1].trim()));
    }

    private static boolean isNullable(JsonNode value) {
        return Objects.isNull(value) || value.booleanValue();
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
        Option p = new Option("p", true, "Partition fields");
        Option a = new Option("a", false, "Append mode");
        Option s = new Option("s", true, "Schema description file");
        s.setRequired(true);
        Option remoteConf = new Option("remoteConf", false, "Remote configuration files (optional)");
        Option characterSet = new Option("charSet", true, "Character set (Java name space)");
        Option checkField = new Option("checkField", true, "Field to be used for value verification");
        Option checkValue = new Option("checkValue", true, "Value to verify in teh checked field");
        Option quoteMode = new Option("quoteMode", true, "Set quote mode");
        
        return new Options().
                addOption(i).
                addOption(d).
                addOption(q).
                addOption(h).
                addOption(s).
                addOption(p).
                addOption(a).
                addOption(remoteConf).
                addOption(characterSet).
                addOption(o).
                addOption(checkField).
                addOption(checkValue).
                addOption(quoteMode);
        
    }
}
