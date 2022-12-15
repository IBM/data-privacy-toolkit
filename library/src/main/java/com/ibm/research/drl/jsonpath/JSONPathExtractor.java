/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class JSONPathExtractor {
    private static final Logger logger = LogManager.getLogger(JSONPathExtractor.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode extract(final JsonNode obj, final JSONPath pattern) {
        return pattern.apply(obj);
    }

    public static JsonNode extract(final JsonNode obj, final String pattern) throws JSONPathException {
        return extract(obj, JSONPath.compile(pattern));
    }

    public static JsonNode extract(final String objString, final String pattern) throws IOException {
        return extract(mapper.readTree(objString), JSONPath.compile(pattern));
    }

    public static JsonNode update(final String objString, final String pattern, String value) throws IOException {
        return update(mapper.readTree(objString), JSONPath.compile(pattern), new TextNode(value));
    }

    public static JsonNode update(final String objString, final String pattern, long value) throws IOException {
        return update(mapper.readTree(objString), JSONPath.compile(pattern), new LongNode(value));
    }

    public static JsonNode update(final String objString, final String pattern, double value) throws IOException {
        return update(mapper.readTree(objString), JSONPath.compile(pattern), new DoubleNode(value));
    }

    public static JsonNode update(final String objString, final String pattern, int value) throws IOException {
        return update(mapper.readTree(objString), JSONPath.compile(pattern), new IntNode(value));
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

    public static JsonNode update(final String objString, final String pattern, JsonNode node) throws IOException {
        return update(mapper.readTree(objString), JSONPath.compile(pattern), createNodeValue(node));
    }

    public static JsonNode update(JsonNode obj, String pattern, JsonNode value) throws JSONPathException {
        return update(obj, JSONPath.compile(pattern), value);
    }

    public static JsonNode update(JsonNode obj, JSONPath pattern, JsonNode value) {
        return pattern.update(obj, value);
    }

    public static JsonNode remove(final JsonNode obj, final JSONPath pattern) {
        return pattern.remove(obj);
    }

    public static JsonNode remove(final JsonNode obj, final String pattern) throws JSONPathException {
        return remove(obj, JSONPath.compile(pattern));
    }

    public static JsonNode remove(final String objString, final String pattern) throws IOException {
        return remove(mapper.readTree(objString), JSONPath.compile(pattern));
    }
}
