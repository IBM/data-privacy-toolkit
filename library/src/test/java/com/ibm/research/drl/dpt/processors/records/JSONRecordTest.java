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
package com.ibm.research.drl.dpt.processors.records;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.*;

public class JSONRecordTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testGetBasepath() throws Exception {
        JSONRecord record = new JSONRecord(mapper.readTree("{\"a\": [{\"b\":1},{\"b\":2}]}"));
        String path = "/root/b";
        assertEquals("/root/", record.getBasepath(path));
    }

    @Test
    public void testPathGenerationNoStars() throws Exception {
        JSONRecord record = new JSONRecord(mapper.readTree("{\"a\": [{\"b\":1},{\"b\":2}]}"));

        Iterable<String> nodes = record.generatePaths("/a/1/b");

        assertNotNull(nodes);
        assertThat((int) StreamSupport.stream(nodes.spliterator(), false).count(), is(1));
    }

    @Test
    public void testPathGeneration() throws Exception {
        JSONRecord record = new JSONRecord(mapper.readTree("{\"a\": [{\"b\":1},{\"b\":2}]}"));

        Iterable<String> nodes = record.generatePaths("/a/*/b");

        assertNotNull(nodes);
        assertThat((int) StreamSupport.stream(nodes.spliterator(), false).count(), is(2));
    }

    @Test
    public void testPathGenerationFirstLevel() throws Exception {
        JSONRecord record = new JSONRecord(mapper.readTree("[{\"b\":1},{\"b\":2}]"));

        Iterable<String> nodes = record.generatePaths("/*/b");

        assertNotNull(nodes);
        assertThat((int) StreamSupport.stream(nodes.spliterator(), false).count(), is(2));
    }

    @Test
    public void testPathGenerationMoreThanOne() throws Exception {
        JSONRecord record = new JSONRecord(mapper.readTree("" +
                "{\"a\": [{\"b\":[{\"a\": 1},{\"a\": 1}]},{\"b\":[{\"a\": 1}]}]}" +
                ""));

        Iterable<String> nodes = record.generatePaths("/a/*/b/*/a");

        assertNotNull(nodes);
        assertThat((int) StreamSupport.stream(nodes.spliterator(), false).count(), is(3));
    }

    @Test
    public void testTraverseComplex() throws Exception {
        String jsonS = "{\"a\": 2, \"b\": [2,3], \"c\": {\"d\" : [2,3], \"e\": 3, \"f\": {\"g\": 2}}}";

        List<String> references = new ArrayList<>();
        new JSONRecord(mapper.readTree(jsonS)).getFieldReferences().iterator().forEachRemaining(references::add);

        assertEquals(7, references.size());
        assertTrue(references.contains("/a"));
        assertTrue(references.contains("/b/0"));
        assertTrue(references.contains("/b/1"));
        assertTrue(references.contains("/c/d/0"));
        assertTrue(references.contains("/c/d/1"));
        assertTrue(references.contains("/c/e"));
        assertTrue(references.contains("/c/f/g"));
    }

    @Test
    public void ifNoArraysAllPathsAreEnumerated() throws Exception {
        String test = "{\n" +
                "  \"foo\": 1,\n" +
                "  \"bar\": {\n" +
                "    \"foo\": 1,\n" +
                "    \"bar\": null\n" +
                "  }\n" +
                "}";

        List<String> referencesWithGeneralization = new ArrayList<>();
        new JSONRecord(mapper.readTree(test)).getFieldReferencesWithGeneralization().iterator().forEachRemaining(referencesWithGeneralization::add);

        List<String> referencesWithout = new ArrayList<>();
        new JSONRecord(mapper.readTree(test)).getFieldReferences().iterator().forEachRemaining(referencesWithout::add);

        assertThat(referencesWithGeneralization.size(), is(referencesWithout.size()));

        for (String reference : referencesWithout) {
            assertThat(referencesWithGeneralization, hasItem(reference));
        }
    }

    @Test
    public void generatesAllGenericPathsCorrectly() throws Exception {
        String test = "{\n" +
                "  \"foo\": 1,\n" +
                "  \"bar\": {\n" +
                "    \"foo\": 1,\n" +
                "    \"bar\": null\n" +
                "  },\n" +
                "  \"bar1\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    \"foo\"\n" +
                "    ],\n" +
                "    \"bar2\": [\n" +
                "      {\n" +
                "        \"foo\": [1, 2, 3],\n" +
                "        \"bar\": 0\n" +
                "      },\n" +
                "      1\n" +
                "      ]\n" +
                "}";

        // expected:
        List<String> paths = new ArrayList<>();
        new JSONRecord(mapper.readTree(test)).getFieldReferencesWithGeneralization().forEach(paths::add);

        assertThat(paths, hasItem("/foo"));
        assertThat(paths, hasItem("/bar/foo"));
        assertThat(paths, hasItem("/bar/bar"));
        assertThat(paths, hasItem("/bar1/*"));
        assertThat(paths, hasItem("/bar2/*"));
        assertThat(paths, hasItem("/bar2/*/foo/*"));
        assertThat(paths, hasItem("/bar2/*/bar"));
    }

    @Test
    void testSetToNullShouldWork() throws JsonProcessingException {
        String test = "{\n" +
                "  \"foo\": 1,\n" +
                "  \"bar\": {\n" +
                "    \"foo\": 1,\n" +
                "    \"bar\": null\n" +
                "  },\n" +
                "  \"bar1\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    \"foo\"\n" +
                "    ],\n" +
                "    \"bar2\": [\n" +
                "      {\n" +
                "        \"foo\": [1, 2, 3],\n" +
                "        \"bar\": 0\n" +
                "      },\n" +
                "      1\n" +
                "      ]\n" +
                "}";

        // expected:
        JSONRecord record = new JSONRecord(mapper.readTree(test));
        record.setFieldValue("/bar/foo", null);

        Object value = record.getFieldValue("/bar/foo");

        assertThat(value, nullValue());
    }

    @Test
    void testSuppressFieldShouldRemoveProperty() throws JsonProcessingException {
        String test = "{\n" +
                "  \"foo\": 1,\n" +
                "  \"bar\": {\n" +
                "    \"foo\": 1,\n" +
                "    \"bar\": null\n" +
                "  },\n" +
                "  \"bar1\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    \"foo\"\n" +
                "    ],\n" +
                "    \"bar2\": [\n" +
                "      {\n" +
                "        \"foo\": [1, 2, 3],\n" +
                "        \"bar\": 0\n" +
                "      },\n" +
                "      1\n" +
                "      ]\n" +
                "}";

        // expected:
        List<String> paths = new ArrayList<>();
        JSONRecord record = new JSONRecord(mapper.readTree(test));
        record.suppressField("/bar/foo");
        record.suppressField("/bar1");

        record.getFieldReferencesWithGeneralization().forEach(paths::add);

        assertThat(paths, hasItem("/foo"));
        assertThat(paths, not(hasItem("/bar/foo")));
        assertThat(paths, hasItem("/bar/bar"));
        assertThat(paths, not(hasItem("/bar1")));
        assertThat(paths, hasItem("/bar2/*"));
        assertThat(paths, hasItem("/bar2/*/foo/*"));
        assertThat(paths, hasItem("/bar2/*/bar"));
    }

    @Test
    public void replaceValueWithDifferentType() throws Exception {
        JSONRecord record = new JSONRecord(mapper.readTree("{" +
                "\"a\":123," +
                "\"b\":\"foo\"," +
                "\"c\":false," +
                "\"d\": {}" +
                "}"));

        record.setFieldValue("/a", "FOOO".getBytes());
        record.setFieldValue("/c", "FOOO".getBytes());
        record.setFieldValue("/d", "REDACTED".getBytes());

        System.out.println(record);
    }
}
