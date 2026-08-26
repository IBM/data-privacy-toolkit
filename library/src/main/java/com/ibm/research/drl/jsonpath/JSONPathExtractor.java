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
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/** Utility class for extracting and updating values in JSON trees using JSON pointer patterns. */
public final class JSONPathExtractor {
    private static final Logger logger = LogManager.getLogger(JSONPathExtractor.class);

    /** Not instantiable. */
    private JSONPathExtractor() {}

    /**
     * Extracts the value at the given JSON path.
     *
     * @param obj     the root JSON node
     * @param pattern the compiled path
     * @return the referenced node
     */
    public static JsonNode extract(final JsonNode obj, final JSONPath pattern) {
        return pattern.apply(obj);
    }

    /**
     * Extracts the value at the given JSON path string.
     *
     * @param obj     the root JSON node
     * @param pattern the JSON pointer string
     * @return the referenced node
     * @throws JSONPathException if the path cannot be compiled
     */
    public static JsonNode extract(final JsonNode obj, final String pattern) throws JSONPathException {
        return extract(obj, JSONPath.compile(pattern));
    }

    /**
     * Parses a JSON string and extracts the value at the given path.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @return the referenced node
     * @throws IOException if parsing fails
     */
    public static JsonNode extract(final String objString, final String pattern) throws IOException {
        return extract(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern));
    }

    /**
     * Parses a JSON string, sets a string value at the given path, and returns the updated tree.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @param value     the string value to set
     * @return the updated root node
     * @throws IOException if parsing fails
     */
    public static JsonNode update(final String objString, final String pattern, String value) throws IOException {
        return update(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern), new TextNode(value));
    }

    /**
     * Parses a JSON string, sets a long value at the given path, and returns the updated tree.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @param value     the long value to set
     * @return the updated root node
     * @throws IOException if parsing fails
     */
    public static JsonNode update(final String objString, final String pattern, long value) throws IOException {
        return update(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern), new LongNode(value));
    }

    /**
     * Parses a JSON string, sets a double value at the given path, and returns the updated tree.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @param value     the double value to set
     * @return the updated root node
     * @throws IOException if parsing fails
     */
    public static JsonNode update(final String objString, final String pattern, double value) throws IOException {
        return update(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern), new DoubleNode(value));
    }

    /**
     * Parses a JSON string, sets an int value at the given path, and returns the updated tree.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @param value     the int value to set
     * @return the updated root node
     * @throws IOException if parsing fails
     */
    public static JsonNode update(final String objString, final String pattern, int value) throws IOException {
        return update(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern), new IntNode(value));
    }

    private static ValueNode createNodeValue(JsonNode node) {
        JsonNodeType type = node.getNodeType();
        switch (type) {
            case STRING:
                return new TextNode(node.asText());
            case NUMBER:
                if (node.isDouble()) {
                    return new DoubleNode(node.asDouble());
                } else if (node.isInt()) {
                    return new IntNode(node.asInt());
                } else if (node.isLong()) {
                    return new LongNode(node.asLong());
                }
                break;
            case BOOLEAN:
                return BooleanNode.valueOf(node.asBoolean());
            default:
                logger.info("Unexpected value: {}", type);
        }

        return NullNode.getInstance();
    }

    /**
     * Parses a JSON string, sets the given node value at the given path, and returns the updated tree.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @param node      the JSON node value to set
     * @return the updated root node
     * @throws IOException if parsing fails
     */
    public static JsonNode update(final String objString, final String pattern, JsonNode node) throws IOException {
        return update(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern), createNodeValue(node));
    }

    /**
     * Updates the value at the given path in the root node.
     *
     * @param obj     the root JSON node
     * @param pattern the JSON pointer string
     * @param value   the new value
     * @return the updated root node
     * @throws JSONPathException if the path cannot be compiled
     */
    public static JsonNode update(JsonNode obj, String pattern, JsonNode value) throws JSONPathException {
        return update(obj, JSONPath.compile(pattern), value);
    }

    /**
     * Updates the value at the given path in the root node.
     *
     * @param obj     the root JSON node
     * @param pattern the compiled path
     * @param value   the new value
     * @return the updated root node
     */
    public static JsonNode update(JsonNode obj, JSONPath pattern, JsonNode value) {
        return pattern.update(obj, value);
    }

    /**
     * Removes the value at the given path from the root node.
     *
     * @param obj     the root JSON node
     * @param pattern the compiled path
     * @return the updated root node
     */
    public static JsonNode remove(final JsonNode obj, final JSONPath pattern) {
        return pattern.remove(obj);
    }

    /**
     * Removes the value at the given path from the root node.
     *
     * @param obj     the root JSON node
     * @param pattern the JSON pointer string
     * @return the updated root node
     * @throws JSONPathException if the path cannot be compiled
     */
    public static JsonNode remove(final JsonNode obj, final String pattern) throws JSONPathException {
        return remove(obj, JSONPath.compile(pattern));
    }

    /**
     * Parses a JSON string and removes the value at the given path.
     *
     * @param objString the JSON string
     * @param pattern   the JSON pointer string
     * @return the updated root node
     * @throws IOException if parsing fails
     */
    public static JsonNode remove(final String objString, final String pattern) throws IOException {
        return remove(JsonUtils.MAPPER.readTree(objString), JSONPath.compile(pattern));
    }
}
