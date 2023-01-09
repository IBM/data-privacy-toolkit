/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public abstract class AbstractNLPAnnotator implements NLPAnnotator {
    protected Map<String, String> extractMapping(JsonNode mapping) {
        return extractMapping(mapping, false);
    }

    public List<String> extractList(JsonNode node) {
        if (node == null || node.size() == 0) {
            return Collections.emptyList();
        }

        return StreamSupport.stream(node.spliterator(), false).map(JsonNode::asText).collect(Collectors.toList());
    }
    
    protected Map<String, String> extractMapping(JsonNode mapping, boolean ignoreCase) {
        if (null == mapping || mapping.isNull() || mapping.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<java.lang.String, java.lang.String> typeMap = new HashMap<>();
        mapping.fields().forEachRemaining(field -> {
            java.lang.String key = field.getKey();
            java.lang.String value = field.getValue().textValue();

            if (!key.isEmpty()) {
                if (ignoreCase) {
                    typeMap.put(key.toLowerCase(), value);
                } else {
                    typeMap.put(key, value);
                }
            }
        });

        return typeMap;
    }

    @Override
    public List<IdentifiedEntity> identifyMissing(List<IdentifiedEntity> identifiedEntities, String text, Language language) {
        return identifiedEntities;
    }

    @Override
    public List<IdentifiedEntity> mergeEntities(List<IdentifiedEntity> identifiedEntities, String text) {
        return identifiedEntities;
    }
}
