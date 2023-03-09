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

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.processors.CSVFormatProcessor;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.spark.utils.RecordUtils;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.*;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


public class TransactionUniquenessTest {
    @Test
    public void testTransactionToUID() throws Exception {
        String[] idColumns = new String[]{"id"};
        String[] targetColumns = new String[]{"date", "location"};

        Row row = RowFactory.create("x", "d1", "loc1");
        
        List<String> fieldNames = Arrays.asList("id", "date", "location");
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(fieldNames);
        List<String> fieldTypes = Arrays.asList("string", "string", "string");

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV, new HashMap<>(), false, new HashMap<>(), new CSVDatasetOptions(false, ',', '"', false));
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());
        
        Tuple2<String, Set<String>> t = TransactionUniqueness.transactionToUID(row, idColumns, targetColumns,
                new CSVFormatProcessor(), DataTypeFormat.CSV, new CSVDatasetOptions(false, ',', '"', false), fieldNames, fieldMap, fieldTypes, dataMaskingOptions, maskingProviderFactory);
        
        assertEquals("d1:loc1", t._1);
        assertEquals(1, t._2.size());
        assertEquals("x", t._2.iterator().next());
    }

    @Test
    public void testTransactionToUIDHandlesNull() throws Exception {
        String[] idColumns = new String[]{"id"};
        String[] targetColumns = new String[]{"date", "location"};

        Row row = RowFactory.create("x", "d1", null);

        List<String> fieldNames = Arrays.asList("id", "date", "location");
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(fieldNames);
        List<String> fieldTypes = Arrays.asList("string", "string", "string");

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV, new HashMap<>(), false, new HashMap<>(), new CSVDatasetOptions(false, ',', '"', false));
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

        Tuple2<String, Set<String>> t = TransactionUniqueness.transactionToUID(row, idColumns, targetColumns,
                new CSVFormatProcessor(), DataTypeFormat.CSV, new CSVDatasetOptions(false, ',', '"', false), fieldNames, fieldMap, fieldTypes, dataMaskingOptions, maskingProviderFactory);

        assertNull(t); 
    }

    @Test
    public void testTransactionToUIDHandlesNull2() throws Exception {

        String[] idColumns = new String[]{"id"};
        String[] targetColumns = new String[]{"date", "location"};

        Row row = RowFactory.create("x", null, "loc");

        List<String> fieldNames = Arrays.asList("id", "date", "location");
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(fieldNames);
        List<String> fieldTypes = Arrays.asList("string", "string", "string");

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV, new HashMap<>(), false, new HashMap<>(), new CSVDatasetOptions(false, ',', '"', false));
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

        Tuple2<String, Set<String>> t = TransactionUniqueness.transactionToUID(row, idColumns, targetColumns,
                new CSVFormatProcessor(), DataTypeFormat.CSV, new CSVDatasetOptions(false, ',', '"', false), fieldNames, fieldMap, fieldTypes, dataMaskingOptions, maskingProviderFactory);
        
        assertNull(t); 
    }
    
    @Test
    public void testNoCombinationsNoMasking() {
        String[] idColumns = new String[]{"id"};
        String[] targetColumns = new String[]{"date", "location"};

        List<String> fieldNames = Arrays.asList("id", "date", "location");
        Map<String, Integer> fieldMap = RecordUtils.createFieldMap(fieldNames);
        List<String> fieldTypes = Arrays.asList("string", "string", "string");

        DataMaskingOptions dataMaskingOptions = new DataMaskingOptions(DataTypeFormat.CSV, DataTypeFormat.CSV, new HashMap<>(), false, new HashMap<>(), new CSVDatasetOptions(false, ',', '"', false));
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap());

        try (SparkSession sparkSession = SparkUtils.createSparkSession("uniqueness test", "local[4]")) {

            StructType schema = new StructType(new StructField[]{
                    new StructField("id", DataTypes.StringType, false, Metadata.empty()),
                    new StructField("date", DataTypes.StringType, false, Metadata.empty()),
                    new StructField("location", DataTypes.StringType, false, Metadata.empty()),
            });
            List<Row> data = new ArrayList<>();
            data.add(RowFactory.create("x", "d1", "loc1"));

            ExpressionEncoder<Row> encoder = RowEncoder.apply(schema);
            Dataset<Row> dataset = sparkSession.createDataset(data, encoder);

            Map<String, Long> results = TransactionUniqueness.run(dataset, idColumns, targetColumns, DataTypeFormat.CSV, new CSVDatasetOptions(false, ',', '"', false), fieldNames,
                    fieldMap, fieldTypes, dataMaskingOptions, maskingProviderFactory, 1, 1);

            assertNotNull(results);
        }
    }
}