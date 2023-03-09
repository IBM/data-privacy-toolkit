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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;

import java.util.Iterator;

public class TransactionUniquenessOptions {
    private final int factor;
    private final int threshold;
    private final String[] idColumns;
    private final String[] targetColumns;
    private final DataTypeFormat inputFormat;
    private final DatasetOptions datasetOptions;

    private final boolean joinRequired;
    private final JoinInformation joinInformation;
    
    public int getThreshold() {
        return threshold;
    }

    public boolean isJoinRequired() {
        return joinRequired;
    }

    public JoinInformation getJoinInformation() {
        return joinInformation;
    }

    public int getFactor() {
        return factor;
    }

    public String[] getIdColumns() {
        return idColumns;
    }

    public String[] getTargetColumns() {
        return targetColumns;
    }

    public DataTypeFormat getInputFormat() {
        return inputFormat;
    }

    public DatasetOptions getDatasetOptions() {
        return datasetOptions;
    }

    public static void validateField(JsonNode configuration, String key, JsonNodeType expectedType) throws MisconfigurationException {
        JsonNode node = configuration.get(key);
        if (node == null) {
            throw new MisconfigurationException("Missing key " + key + " from configuration");
        } else if (node.getNodeType() != expectedType) {
            throw new MisconfigurationException("Key " + key + " has wrong type. Expected is: " + expectedType.toString());
        }
    }
   
    private String[] readArray(JsonNode arr) {
        String[] s = new String[arr.size()];

        Iterator<JsonNode> iterator = arr.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            s[i++] = iterator.next().asText();
        }
        
        return s;
    }
    
    public TransactionUniquenessOptions(JsonNode configuration) {
        validateField(configuration, "factor", JsonNodeType.NUMBER);
        validateField(configuration, "threshold", JsonNodeType.NUMBER);
        validateField(configuration, "inputFormat", JsonNodeType.STRING);
        validateField(configuration, "idColumn", JsonNodeType.ARRAY);
        validateField(configuration, "targetColumns", JsonNodeType.ARRAY);
        validateField(configuration, "joinRequired", JsonNodeType.BOOLEAN);
        
        this.factor = configuration.get("factor").asInt();
        this.threshold = configuration.get("threshold").asInt();
        this.idColumns = readArray(configuration.get("idColumn"));
        
        if (this.idColumns.length == 0) {
            throw new RuntimeException("id columns cannot be empty");
        }
        
        this.targetColumns = readArray(configuration.get("targetColumns"));
       
        if (this.targetColumns.length == 0) {
            throw new RuntimeException("target columns cannot be empty");
        }
        
        this.inputFormat = DataTypeFormat.valueOf(configuration.get("inputFormat").asText());
        this.datasetOptions = createDatasetOptions(configuration, this.inputFormat);
        
        
        
        this.joinRequired = configuration.get("joinRequired").asBoolean();
        if (this.joinRequired) {
            validateField(configuration, "joinInformation", JsonNodeType.OBJECT);
            
            JsonNode joinInformation = configuration.get("joinInformation");
            validateField(joinInformation, "rightTable", JsonNodeType.STRING);
            validateField(joinInformation, "rightColumn", JsonNodeType.STRING);
            validateField(joinInformation, "leftColumn", JsonNodeType.STRING);
            validateField(joinInformation, "rightTableInputFormat", JsonNodeType.STRING);
            validateField(joinInformation, "rightTableDatasetOptions", JsonNodeType.OBJECT);
           
            DataTypeFormat rightTableInputFormat = DataTypeFormat.valueOf(joinInformation.get("rightTableInputFormat").asText());
            DatasetOptions rightTableDatasetOptions = createDatasetOptions(joinInformation.get("rightTableDatasetOptions"), rightTableInputFormat);
            
            this.joinInformation = new JoinInformation(joinInformation.get("rightTable").asText(),
                    joinInformation.get("leftColumn").asText(), joinInformation.get("rightColumn").asText(), 
                    rightTableInputFormat, rightTableDatasetOptions);
        } else {
            this.joinInformation = null;
        }
    }

    private DatasetOptions createDatasetOptions(JsonNode configuration, DataTypeFormat inputFormat) {
        if (inputFormat != DataTypeFormat.CSV) {
            return null;
        }
        
        validateField(configuration, "delimiter", JsonNodeType.STRING);
        validateField(configuration, "quoteChar", JsonNodeType.STRING);
        validateField(configuration, "hasHeader", JsonNodeType.BOOLEAN);
        validateField(configuration, "trimFields", JsonNodeType.BOOLEAN);
        String delimiter = configuration.get("delimiter").asText();
        String quoteChar = configuration.get("quoteChar").asText();
        boolean hasHeader = configuration.get("hasHeader").asBoolean();
        boolean trimFields = configuration.get("trimFields").asBoolean();
        return new CSVDatasetOptions(hasHeader, delimiter.charAt(0), quoteChar.charAt(0), trimFields);
    }
}
