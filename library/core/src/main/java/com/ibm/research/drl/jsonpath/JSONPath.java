/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.jsonpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public final class JSONPath implements Serializable {
    private final String pattern;

    private JSONPath(final String pattern) {
        if (null == pattern) throw new NullPointerException("Pattern cannot be null");

        this.pattern = pattern;
    }

    public static JSONPath compile(final String pattern) {
        return new JSONPath(pattern);
    }

    public JsonNode apply(JsonNode obj) {
        return obj.at(pattern);
    }

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
