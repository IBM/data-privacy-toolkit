/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.processors;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import com.ibm.research.drl.dpt.datasets.JSONDatasetOptions;
import com.ibm.research.drl.dpt.processors.records.JSONRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.IdentifierUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONFormatProcessor extends MultipathFormatProcessor {
    protected static final ObjectMapper mapper = new ObjectMapper();

    private static Map<ProviderType, Long> identifyJSONArrayElement(JsonNode node) {
        Iterator<JsonNode> iterator = node.iterator();
        return identifyListOfElements(iterator);
    }

    private static Map<ProviderType, Long> identifyJSONElement(JsonNode node) {
        if (node == null || node.isNull()) {
            Map<ProviderType, Long> results = new HashMap<>();
            results.put(ProviderType.EMPTY, 1L);
            return results;
        }

        if (node.isObject()) {
            Map<ProviderType, Long> results = new HashMap<>();
            results.put(ProviderType.UNKNOWN, 1L);
            return results;
        }

        if (node.isArray()) {
            return identifyJSONArrayElement(node);
        }

        if (node.isNumber() || node.isTextual()) {
            return IdentifierUtils.identifySingleValue(node.asText());
        }

        if (node.isBoolean()) {
            Map<ProviderType, Long> results = new HashMap<>();
            results.put(ProviderType.BOOLEAN, 1L);
            return results;
        }

        return null;
    }

    private static Map<ProviderType, Long> identifyListOfElements(Iterator<JsonNode> iterator) {
        Map<ProviderType, Long> results = new HashMap<>();

        while (iterator.hasNext()) {
            JsonNode element = iterator.next();
            Map<ProviderType, Long> elementResults = identifyJSONElement(element);

            if (elementResults == null) {
                continue;
            }

            for (Map.Entry<ProviderType, Long> entry : elementResults.entrySet()) {
                ProviderType providerType = entry.getKey();
                Long counter = results.get(providerType);

                if (counter == null) {
                    counter = 1L;
                } else {
                    counter += 1;
                }

                results.put(providerType, counter);
            }
        }

        return results;
    }

    @Override
    protected Iterable<Record> extractRecords(InputStream dataset, DatasetOptions datasetOptions, int firstN) throws IOException {
        final MappingIterator<JsonNode> iterator = createIterators(dataset, datasetOptions);

        return () -> new Iterator<Record>() {
            int readSoFar = 0;

            @Override
            public boolean hasNext() {
                if (firstN > 0 && readSoFar >= firstN) {
                    return false;
                }

                return iterator.hasNext();
            }

            @Override
            public Record next() {
                readSoFar++;
                return new JSONRecord(iterator.next());
            }
        };
    }

    private MappingIterator<JsonNode> createIterators(InputStream dataset, DatasetOptions datasetOptions) throws IOException {
        final JsonParser parser = mapper.getFactory().createParser(dataset);

        if (null != datasetOptions) {
            if (!(datasetOptions instanceof JSONDatasetOptions)) {
                throw new IllegalArgumentException("Dataset masking options not consistent with the format processor: JSON");
            }
        }

        return mapper.readerFor(JsonNode.class).readValues(parser);
    }

    @Override
    // NOTE: assumes that we consider the last field, excluding * and such
    protected String extractFieldName(String fieldName) {
        final String[] parts = fieldName.split("/");

        for (int i = parts.length - 1; i >= 0; --i) {
            final String part = parts[i].trim();

            if (part.isEmpty()) continue;
            if ("*".equals(part)) continue;

            return part;
        }
        return "";
    }

    @Override
    public boolean supportsStreams() {
        return true;
    }
}
