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
package com.ibm.research.drl.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/** Compiled JSON pointer expression used to locate and mutate nodes in a JSON tree. */
public final class JSONPath implements Serializable {
    /** The JSON pointer string for this path expression. */
    private final String pattern;

    private JSONPath(final String pattern) {
        if (null == pattern) throw new NullPointerException("Pattern cannot be null");

        this.pattern = pattern;
    }

    /**
     * Compiles a JSON pointer pattern.
     *
     * @param pattern the JSON pointer string (RFC 6901)
     * @return the compiled JSONPath
     */
    public static JSONPath compile(final String pattern) {
        return new JSONPath(pattern);
    }

    /**
     * Applies this path to the given JSON node and returns the referenced value.
     *
     * @param obj the root JSON node
     * @return the referenced node, or {@code MissingNode} if not found
     */
    public JsonNode apply(JsonNode obj) {
        return obj.at(pattern);
    }

    /**
     * Updates the value referenced by this path in the given JSON tree.
     *
     * @param obj   the root JSON node
     * @param value the new value to set
     * @return the (possibly mutated) root node
     */
    public JsonNode update(JsonNode obj, JsonNode value) {
        List<String> list = Arrays.asList(pattern.split("/"));
        JsonNode node;
        if (list.size() > 2) {
            // because of "" before the first / and the second one will be the field we want to modify
            String newPattern = StringUtils.join(list.subList(0, list.size() - 1), "/");

            node = obj.at(newPattern);
        } else {
            node = obj.at("");
        }

        if (node.isArray()) {
            ((ArrayNode) node).set(Integer.parseInt(list.get(list.size() - 1), 10), value);
        } else if (node.isObject()) {
            ((ObjectNode) node).set(list.get(list.size() - 1), value);
        }

        return obj;
    }

    /**
     * Removes the node referenced by this path from the given JSON tree.
     *
     * @param obj the root JSON node
     * @return the (possibly mutated) root node
     */
    public JsonNode remove(JsonNode obj) {
        List<String> list = Arrays.asList(pattern.split("/"));
        JsonNode node;
        if (list.size() > 2) {
            // because of "" before the first / and the second one will be the field we want to modify
            String newPattern = StringUtils.join(list.subList(0, list.size() - 1), "/");

            node = obj.at(newPattern);
        } else {
            node = obj.at("");
        }

        if (node.isArray()) {
            ((ArrayNode) node).remove(Integer.parseInt(list.get(list.size() - 1), 10));
        } else if (node.isObject()) {
            ((ObjectNode) node).remove(list.get(list.size() - 1));
        }

        return obj;
    }

}
