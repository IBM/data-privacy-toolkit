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
package com.ibm.research.drl.dpt.spark.masking;

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.processors.FormatProcessor;
import com.ibm.research.drl.dpt.processors.FormatProcessorFactory;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;
import com.ibm.research.drl.dpt.spark.utils.RecordUtils;
import com.ibm.research.drl.dpt.spark.utils.SparkUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

import java.util.*;

public class DataMasking {

    private static Dataset<Row> applyPreprocessing(Dataset<Row> rdd, DataTypeFormat inputType) {
        if (inputType == DataTypeFormat.FHIR_JSON) {
            return rdd.map((MapFunction<Row, String>) value -> FHIRMaskingUtils.preprocessFHIRObject(value.mkString()), Encoders.STRING()).toDF();
        }

        return rdd;
    }

    private static Dataset<Row> applyPostprocessing(Dataset<Row> rdd, DataTypeFormat inputType) {
        if (inputType == DataTypeFormat.FHIR_JSON) {
            return rdd.map((MapFunction<Row, String>) value -> FHIRMaskingUtils.postProcessFHIRObject(value.mkString()), Encoders.STRING()).toDF();
        }

        return rdd;
    }
   
    private static Dataset<Row> handleConsistency(Dataset<Row> dataset, DataMaskingOptions maskingOptions, 
                                          ConfigurationManager configurationManager, Set<String> alreadyMaskedFields, Map<String, MaskingProvider> maskingProviders,
                                          Map<String, Integer> fieldMap) {
        
        final Map<String, Set<String>> consistentMaskingFields = analyzeConfiguration(maskingOptions, configurationManager);
        
        if (consistentMaskingFields.isEmpty()) {
            return dataset;
        }

        dataset = applyPreprocessing(dataset, maskingOptions.getInputFormat());

        for (Map.Entry<String, Set<String>> entry : consistentMaskingFields.entrySet()) {
            Set<String> fieldsInNamespace = entry.getValue();
            alreadyMaskedFields.addAll(fieldsInNamespace);

            if (fieldsInNamespace.size() == 1) {
                dataset = ConsistentDataMasking.doConsistentMasking(dataset.sparkSession(), dataset,
                        maskingProviders, fieldsInNamespace.iterator().next(), fieldMap, maskingOptions);
            } else {
                dataset = ConsistentDataMasking.doGloballyConsistentMasking(dataset, maskingOptions.getToBeMasked(), maskingProviders,
                        fieldsInNamespace, fieldMap, maskingOptions);
            }
        }

        dataset = applyPostprocessing(dataset, maskingOptions.getInputFormat());

        return dataset;
    }
    /**
     * Run java rdd.
     *
     * @param dataset                     the rdd
     * @return the java rdd
     */
    public static Dataset<Row> run(final ConfigurationManager configurationManager,  
                                      final DataMaskingOptions maskingOptions, Dataset<Row> dataset) {
        MaskingProviderFactory maskingProviderFactory = new MaskingProviderFactory(configurationManager,
                maskingOptions.getToBeMasked());

        final Map<String, MaskingProvider> maskingProviders = new HashMap<>();
        for (String key : maskingOptions.getToBeMasked().keySet()) {
            maskingProviders.put(key, maskingProviderFactory.get(key));
        }
        
        final List<String> fieldNames = SparkUtils.createFieldNames(dataset, maskingOptions.getInputFormat(), maskingOptions.getDatasetOptions());
        final Map<String, Integer> fieldMap = RecordUtils.createFieldMap(dataset.schema());
        
        final Set<String> alreadyMaskedFields = new HashSet<>();
       
        dataset = handleConsistency(dataset, maskingOptions, configurationManager, alreadyMaskedFields, maskingProviders, fieldMap);

        final DataTypeFormat inputFormat = maskingOptions.getInputFormat();
        final FormatProcessor formatProcessor = FormatProcessorFactory.getProcessor(inputFormat);
        final DatasetOptions datasetOptions = maskingOptions.getDatasetOptions();
        
        final List<String> fieldTypes = RecordUtils.getFieldClasses(dataset.schema().fields());

        JavaRDD<Row> rdd = dataset.toJavaRDD().map(value -> {
            Record record = RecordUtils.createRecord(value, inputFormat, datasetOptions, fieldNames, fieldMap, fieldTypes, false);
            Record maskedRecord = formatProcessor.maskRecord(record, maskingProviderFactory, alreadyMaskedFields, maskingOptions);

            if (inputFormat == DataTypeFormat.CSV || inputFormat == DataTypeFormat.PARQUET) {
                return RowFactory.create(((RowRecord) maskedRecord).getValues());
            } else {
                return RowFactory.create(Collections.singletonList(maskedRecord.toString()).toArray());
            }
        });

        return dataset.sparkSession().createDataFrame(rdd, dataset.schema());
    }
  
    public static Map<String,Set<String>> analyzeConfiguration(DataMaskingOptions maskingOptions, ConfigurationManager configurationManager) {
        
        Map<String, Set<String>> consistentFields = new HashMap<>();
        
        Set<String> fields = maskingOptions.getToBeMasked().keySet();
        
        for(String field: fields) {
            MaskingConfiguration fieldConfiguration = configurationManager.getFieldConfiguration(field);
            boolean isConsistent = fieldConfiguration.getBooleanValue("persistence.export");
            String consistentType = fieldConfiguration.getStringValue("persistence.type");
                    
            if (isConsistent && consistentType.equals("memory")) {
                String ns = fieldConfiguration.getStringValue("persistence.namespace");
                
                if (!consistentFields.containsKey(ns)) {
                    consistentFields.put(ns, new HashSet<>());
                }
                
                consistentFields.get(ns).add(field);
            }
            
        }
        
        return consistentFields;
    }

}
