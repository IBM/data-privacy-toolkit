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
package com.ibm.research.drl.dpt.datasets;

import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IPVDatasetJSONSerializer {

    public static void serialize(IPVDataset dataset, JSONDatasetOptions options, Writer writer) throws IOException {
        List<Map<String, Object>> jsonDataset = new ArrayList<>();

        List<? extends IPVSchemaField> fields = dataset.schema.getFields();

        for (List<String> values : dataset) {
            jsonDataset.add(buildValueMap(values, fields));
        }

        JsonUtils.MAPPER.writeValue(writer, jsonDataset);
    }

    private static Map<String, Object> buildValueMap(List<String> values, List<? extends IPVSchemaField> fields) {
        Map<String, Object> obj = new LinkedHashMap<>();

        int limit = Math.min(values.size(), fields.size());
        for (int i = 0; i < limit; ++i) {
            IPVSchemaField field = fields.get(i);
            setValue(obj, field.getName(), toTypedValue(values.get(i), field.getType()));
        }

        return obj;
    }

    private static Object toTypedValue(String raw, IPVSchemaFieldType type) {
        if (raw == null) return null;
        try {
            return switch (type) {
                case INT -> Long.parseLong(raw);
                case FLOAT -> Double.parseDouble(raw);
                case BOOLEAN -> Boolean.parseBoolean(raw);
                default -> raw;
            };
        } catch (NumberFormatException e) {
            // fall back to raw string when the value cannot be parsed
            return raw;
        }
    }

    private static void setValue(Map<String, Object> obj, String field, Object value) {
        int dot = field.indexOf('.');
        if (dot >= 0) {
            String head = field.substring(0, dot);
            String tail = field.substring(dot + 1);
            setValue(extractFieldObj(obj, head), tail, value);
        } else {
            obj.put(field, value);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractFieldObj(Map<String, Object> obj, String fieldName) {
        Object existing = obj.get(fieldName);
        if (existing instanceof Map) {
            return (Map<String, Object>) existing;
        }
        // Create a new nested map, overwriting any previously stored non-map value
        // (this can happen when a schema has both "address" and "address.city").
        Map<String, Object> nested = new LinkedHashMap<>();
        obj.put(fieldName, nested);
        return nested;
    }
}
