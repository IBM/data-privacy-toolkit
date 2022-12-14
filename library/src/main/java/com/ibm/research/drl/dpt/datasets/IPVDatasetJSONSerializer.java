/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.datasets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class IPVDatasetJSONSerializer {
    private static final Logger logger = LogManager.getLogger(IPVDatasetJSONSerializer.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public void serialize(IPVDataset dataset, JSONDatasetOptions options, Writer writer) throws IOException {
        List<Map<String, Object>> jsonDataset = new ArrayList<>();

        List<String> fields = dataset.schema.getFields().stream().map(IPVSchemaField::getName).collect(Collectors.toList());
        List<IPVSchemaFieldType> types = dataset.schema.getFields().stream().map(IPVSchemaField::getType).collect(Collectors.toList());

        for (List<String> values : dataset) {
            jsonDataset.add(
                    buildValueMap(values, fields)
            );
        }

        mapper.writeValue(writer, jsonDataset);
    }

    private Map<String, Object> buildValueMap(List<String> values, List<String> fields) {
        Map<String, Object> obj = new HashMap<>();

        for (int i = 0; i < values.size(); ++i) {
            String field = fields.get(i);
            String value = values.get(i);

            setValue(obj, field, value);
        }

        return obj;
    }

    private void setValue(Map<String, Object> obj, String field, String value) {
        if (field.contains(".")) {
            String fieldName = extractFieldName(field);
            String fieldPath = extractFieldPath(field);

            obj = extractFieldObj(obj, fieldName);

            setValue(obj, fieldPath, value);
        } else {
            obj.put(field, value);
        }
    }

    private Map<String, Object> extractFieldObj(Map<String, Object> obj, String fieldName) {
        if (!obj.containsKey(fieldName)) {
            obj.put(fieldName, new HashMap<>());
        }

        return (Map<String, Object>) obj.get(fieldName);
    }

    private String extractFieldPath(String field) {
        String[] parts = field.split("\\.");

        return Arrays.stream(parts, 1, parts.length).collect(Collectors.joining("."));
    }

    private String extractFieldName(String field) {
        String[] parts = field.split("\\.");

        return parts[0];
    }
}
