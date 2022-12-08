/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonUtilsTest {
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testTraverseSingleObject() throws Exception {
        String jsonS = "{\"a\": 2}";
        JsonNode node = MAPPER.readTree(jsonS);

        Map<String, List<JsonNode>> traverseMap = JsonUtils.traverseObject(node);


        assertEquals(1, traverseMap.size());
        assertEquals(1, traverseMap.get("/a").size());
    }

    @Test
    public void testTraverseArray() throws Exception {
        String jsonS = "[\"a\", 2]";
        JsonNode node = MAPPER.readTree(jsonS);

        Map<String, List<JsonNode>> traverseMap = JsonUtils.traverseObject(node);

        assertEquals(1, traverseMap.size());
        assertTrue(traverseMap.containsKey("/"));
    }

    @Test
    public void testTraverseArrayInnerObject() throws Exception {
        String jsonS = "[{\"c\": 2}, {\"c\": 3} , {\"d\": 3}]";
        JsonNode node = MAPPER.readTree(jsonS);

        Map<String, List<JsonNode>> traverseMap = JsonUtils.traverseObject(node);

        assertEquals(3, traverseMap.size());
        assertTrue(traverseMap.containsKey("/"));
        assertTrue(traverseMap.containsKey("/d"));
        assertEquals(2, traverseMap.get("/c").size());
    }

    @Test
    public void testTraverseArrayInnerObject2() throws Exception {
        String jsonS = "{\"b\": [{\"c\": 2, \"d\": 3}]}";
        JsonNode node = MAPPER.readTree(jsonS);

        Map<String, List<JsonNode>> traverseMap = JsonUtils.traverseObject(node);

        assertEquals(3, traverseMap.size());
        assertTrue(traverseMap.containsKey("/b"));
        assertTrue(traverseMap.containsKey("/b/c"));
        assertTrue(traverseMap.containsKey("/b/d"));
    }

    @Test
    public void testTraverseComplex() throws Exception {
        String jsonS = "{\"a\": 2, \"b\": [2,3], \"c\": {\"d\" : [2,3], \"e\": 3, \"f\": {\"g\": 2}}}";
        JsonNode node = MAPPER.readTree(jsonS);

        Map<String, List<JsonNode>> traverseMap = JsonUtils.traverseObject(node);

        assertEquals(5, traverseMap.size());
        assertTrue(traverseMap.containsKey("/a"));
        assertTrue(traverseMap.containsKey("/b"));
        assertTrue(traverseMap.containsKey("/c/d"));
        assertTrue(traverseMap.containsKey("/c/e"));
        assertTrue(traverseMap.containsKey("/c/f/g"));
    }
}
