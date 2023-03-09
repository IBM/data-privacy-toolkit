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
package com.ibm.research.drl.dpt.processors.records;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TabularRecord implements Record {
    protected boolean isHeader;
    protected String[] data;
    protected Map<String, Integer> fieldNames;

    public Iterable<String> getFieldReferences() {
        return fieldNames.keySet();
    }

    @Override
    public byte[] getFieldValue(String fieldReference) {
        if (fieldNames.containsKey(fieldReference)) {
            String fieldValue = data[fieldNames.get(fieldReference)];
            if (null != fieldValue)
                return fieldValue.getBytes();
        }
        return null;
    }

    @Override
    public void setFieldValue(String fieldReference, byte[] value) {
        if (!fieldNames.containsKey(fieldReference)) {
            throw new RuntimeException("Unable to set non existing field value: " + fieldReference);
        }

        data[fieldNames.get(fieldReference)] = value == null ? null : new String(value);
    }

    @Override
    public void suppressField(String field) {
        if (!fieldNames.containsKey(field)) {
            throw new IllegalArgumentException("The input schema does not contain the field marked as to be suppressed.");
        }

        int fieldIndex = fieldNames.get(field);

        if (data == null || fieldIndex < 0 || fieldIndex >= data.length) {
            throw new IllegalArgumentException("The input schema does not contain the field marked as to be suppressed.");
        }

        this.fieldNames = this.fieldNames.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(field))
                .peek(entry -> {
                    if (entry.getValue() > fieldIndex) {
                        entry.setValue(entry.getValue() - 1);
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.data = IntStream.range(0, this.data.length)
                .filter(i -> i != fieldIndex)
                .mapToObj(i -> this.data[i])
                .toArray(String[]::new);
    }

    protected abstract String formatRecord();

    public final String toString() {
        return formatRecord();
    }

    protected byte[] formatRecordBytes() {
        return formatRecord().getBytes();
    }

    @Override
    public final byte[] toBytes() {
        return formatRecordBytes();
    }

    @Override
    public boolean isHeader() {
        return isHeader;
    }
}
