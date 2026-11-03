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
package com.ibm.research.drl.dpt.nlp;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;


/**
 * Abstract base class for NLP annotators providing common utility methods.
 */
public abstract class AbstractNLPAnnotator implements NLPAnnotator {
    /**
     * Constructs a new AbstractNLPAnnotator.
     */
    public AbstractNLPAnnotator() {
    }

    /**
     * Extracts a case-sensitive string-to-string mapping from a JSON node.
     *
     * @param mapping the JSON object node representing the mapping
     * @return a map of string keys to string values
     */
    protected Map<String, String> extractMapping(JsonNode mapping) {
        return extractMapping(mapping, false);
    }

    /**
     * Extracts a list of string values from a JSON array node.
     *
     * @param node the JSON array node
     * @return list of string values, or empty list if null/empty
     */
    public List<String> extractList(JsonNode node) {
        if (node == null || node.size() == 0) {
            return Collections.emptyList();
        }

        return StreamSupport.stream(node.spliterator(), false).map(JsonNode::asText).toList();
    }
    
    /**
     * Extracts a string-to-string mapping from a JSON node.
     *
     * @param mapping    the JSON object node representing the mapping
     * @param ignoreCase if true, keys are lowercased before insertion
     * @return a map of string keys to string values
     */
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
}
