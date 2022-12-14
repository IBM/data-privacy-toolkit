/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.dataset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchema;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchema;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchemaField;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JSONIPVDataset extends IPVDataset {
    private JSONIPVDataset(List<SimpleSchemaField> fields, List<List<String>> values) {
        super(values, new SimpleSchema(UUID.randomUUID().toString(), fields), true);
    }

    public static IPVDataset load(Reader reader) throws IOException {
        final List<JsonNode> values = readObjects(reader);
        final List<SimpleSchemaField> fields = buildHeaders("/*/", values);
        final List<List<String>> records = buildRecords(values, fields);

        return new JSONIPVDataset(
                fields,
                records
        );
    }

    private static List<JsonNode> readObjects(Reader reader) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonParser parser = mapper.getFactory().createParser(reader);
        final MappingIterator<JsonNode> nodes = mapper.readerFor(JsonNode.class).readValues(parser);

        List<JsonNode> dataset = new ArrayList<>();

        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            if (node.isArray()) {
                node.elements().forEachRemaining(dataset::add);
            } else {
                dataset.add(node);
            }
        }

        return dataset;
    }

    private static List<SimpleSchemaField> buildHeaders(String prefix, List<JsonNode> nodes) {
        List<String> allFieldNames = nodes.stream().flatMap(
                node -> StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(node.fieldNames(), Spliterator.ORDERED),
                        false
                )
        ).distinct().collect(Collectors.toList());

        List<SimpleSchemaField> fields = new ArrayList<>(allFieldNames.size());

        for (final JsonNode node : nodes) {
            if (allFieldNames.isEmpty()) break;

            Iterator<String> fieldNamesIterator = allFieldNames.iterator();
            while (fieldNamesIterator.hasNext()) {
                final String fieldName = fieldNamesIterator.next();
                if (node.has(fieldName)) {
                    final JsonNode field = node.get(fieldName);

                    if (field.isValueNode()) {
                        final IPVSchemaFieldType fieldType = getFieldType(field);
                        fields.add(
                            new SimpleSchemaField(
                                prefix + fieldName,
                                fieldType
                            )
                        );
                    } else {
                        fields.addAll(
                            buildHeaders(
                                prefix + fieldName + "/",
                                Collections.singletonList(field)
                            )
                        );
                    }

                    fieldNamesIterator.remove();
                }
            }
        }

        return fields;
    }

    private static IPVSchemaFieldType getFieldType(JsonNode field) {
        if (field.isBoolean()) return IPVSchemaFieldType.BOOLEAN;
        else if (field.isDouble() || field.isFloat()) return IPVSchemaFieldType.FLOAT;
        else if (field.isTextual()) return IPVSchemaFieldType.STRING;
        else if (field.isIntegralNumber()) return IPVSchemaFieldType.INT;
        else if (field.isNull()) return null;
        else throw new UnsupportedOperationException("Type not supported");
    }

    private static List<List<String>> buildRecords(List<JsonNode> nodes, List<SimpleSchemaField> fields) {
        return nodes.stream().map( node -> fields.stream().map(
                    field -> {
                        final String fieldName = field.getName();
                        JsonNode child = node.at(fieldName.startsWith("/*") ? fieldName.substring(2) : fieldName);
                        if (child != null) {
                            return child.asText();
                        }
                        return "";
                    }
            ).collect(Collectors.toList())
        ).collect(Collectors.toList());
    }

    @Override
    public IPVSchema getSchema() {
        return this.schema;
    }
}
