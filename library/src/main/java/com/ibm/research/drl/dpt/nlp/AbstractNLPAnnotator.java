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
}
