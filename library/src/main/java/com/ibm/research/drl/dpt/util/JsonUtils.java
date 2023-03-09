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
package com.ibm.research.drl.dpt.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;

import java.util.*;

public class JsonUtils {

    public final static ObjectMapper MAPPER = new ObjectMapper();

    public static void validateField(JsonNode configuration, String key, JsonNodeType expectedType) throws MisconfigurationException {
        JsonNode node = configuration.get(key);
        if (node == null) {
            throw new MisconfigurationException("Missing key " + key + " from configuration");
        } else if (node.getNodeType() != expectedType) {
            throw new MisconfigurationException("Key " + key + " has wrong type. Expected is: " + expectedType.toString());
        }
    }

    public static Set<String> setFromArrayOfStrings(JsonNode array) {
        if (array == null || !array.isArray()) {
            return Collections.emptySet();
        }

        Set<String> set = new HashSet<>();

        for (JsonNode anArray : array) {
            set.add(anArray.asText());
        }

        return set;
    }

    private static void mergeMaps(Map<String, List<JsonNode>> a, Map<String, List<JsonNode>> b) {

        for (Map.Entry<String, List<JsonNode>> entry : b.entrySet()) {
            String key = entry.getKey();
            if (a.containsKey(key)) {
                a.get(key).addAll(entry.getValue());
            } else {
                a.put(key, entry.getValue());
            }
        }

    }

    private static void addToMap(Map<String, List<JsonNode>> a, String key, JsonNode node) {
        List<JsonNode> l = new ArrayList<>(Collections.singletonList(node));

        if (a.containsKey(key)) {
            a.get(key).addAll(l);
        } else {
            a.put(key, l);
        }
    }

    private static Map<String, List<JsonNode>> traverseObject(JsonNode node, String parentPath) {
        Map<String, List<JsonNode>> pathMap = new HashMap<>();

        if (node.isObject()) {
            Iterator<String> iterator = node.fieldNames();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JsonNode value = node.get(key);
                Map<String, List<JsonNode>> innerPaths = traverseObject(value, parentPath + "/" + key);
                mergeMaps(pathMap, innerPaths);
            }
        } else {
            String newParent = parentPath;

            if (node.isArray()) {
                for (int i = 0; i < node.size(); ++i) {
                    JsonNode n = node.get(i);
                    if (n.isObject()) {
                        Map<String, List<JsonNode>> innerPaths = traverseObject(n, newParent);
                        mergeMaps(pathMap, innerPaths);
                    }
                }
            }

            if (parentPath.isEmpty()) {
                newParent += "/";
            }

            addToMap(pathMap, newParent, node);
        }

        return pathMap;

    }

    public static Map<String, List<JsonNode>> traverseObject(JsonNode node) {
        return traverseObject(node, "");
    }
}
