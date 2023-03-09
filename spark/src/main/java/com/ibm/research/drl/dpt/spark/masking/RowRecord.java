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

import com.ibm.research.drl.dpt.processors.records.Record;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Row;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class RowRecord implements Record {
    private final List<String> fieldNames;
    private final Object[] values;
    private final Map<String, Integer> fieldMap;
    private final List<String> fieldTypes;
    
    public static Record fromRow(Row row, List<String> fieldNames, Map<String, Integer> fieldMap, List<String> fieldTypes) { 
        Object[] values = new Object[row.size()];
        
        for(int i = 0; i < row.size(); i++) {
            values[i] = row.get(i);
        }
      
        return new RowRecord(fieldNames, fieldMap, fieldTypes, values);
    }

    public RowRecord(List<String> fieldNames, Map<String, Integer> fieldMap, List<String> fieldTypes, Object[] values) {
        this.fieldNames = fieldNames;
        this.fieldMap = fieldMap;
        this.fieldTypes = fieldTypes;
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public byte[] getFieldValue(String s) {
        int index = this.fieldMap.get(s);
        Object value = this.values[index];
        
        if (value == null) {
            return null;
        }
        
        return value.toString().getBytes();
    }

    @Override
    public void suppressField(String s) {

    }

    private String getIntegerPart(String v) {
        return v.split("\\.")[0];
    }
    
    @Override
    public void setFieldValue(String s, byte[] bytes) {
        int index = this.fieldMap.get(s);
        String type = this.fieldTypes.get(index);
      
        if (bytes == null || bytes.length == 0) {
            this.values[index] = null;
            return;
        }
        
        String v = new String(bytes);
        Object toSet;
        
        switch (type) {
            case "StringType":
                toSet = v;
                break;
            case "BinaryType":
            case "ByteType":
                toSet = bytes[0];
                break;
            case "BooleanType":
                toSet = Boolean.parseBoolean(v);
                break;
            case "DecimalType":
                toSet = new BigDecimal(v);
                break;
            case "IntegerType":
                toSet = Integer.parseInt(getIntegerPart(v));
                break;
            case "LongType":
                toSet = Long.parseLong(getIntegerPart(v));
                break;
            case "FloatType":
                toSet = Float.parseFloat(v);
                break;
            case "DoubleType":
                toSet = Double.parseDouble(v);
                break;
            case "ShortType":
                toSet = Short.parseShort(getIntegerPart(v));
                break;
            case "TimestampType":
                toSet = Timestamp.valueOf(v);
                break;
            case "ObjectType":
            default:
                toSet = values[index];
                break;
                
        }
        
        this.values[index] = toSet;
    }

    @Override
    public String toString() {
        return StringUtils.join(values);
    }

    @Override
    public byte[] toBytes() {
        return this.toString().getBytes();
    }

    @Override
    public Iterable<String> getFieldReferences() {
        return fieldNames;
    }

    @Override
    public boolean isHeader() {
        return false;
    }
}
