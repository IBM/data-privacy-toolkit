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

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JSONPathExtractorTest {
    private final String testJson = "{ \"store\": {\n" +
            "    \"book\": [ \n" +
            "      { \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"isbn\": \"0-553-21311-3\",\n" +
            "        \"price\": 8.99\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"J. R. R. Tolkien\",\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"isbn\": \"0-395-19395-8\",\n" +
            "        \"price\": 22.99\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Test
    public void testJSONPathExpressions() throws Exception {
        JsonNode selection = JSONPathExtractor.extract(testJson, "/store/book/0/price");
        assertNotNull(selection);
        assertThat(selection.asDouble(), is(8.95));
    }

    @Test
    public void testUpdate() throws Exception {
        JsonNode selection = JSONPathExtractor.extract(testJson, "/store/book/0/price");
        assertNotNull(selection);
        assertThat(selection.asDouble(), is(8.95));

        JsonNode update = JSONPathExtractor.update(testJson, "/store/book/0/price", 10.001);

        assertNotNull(update);
        assertThat(JSONPathExtractor.extract(update, "/store/book/0/price").asDouble(), is(10.001));

    }

    @Test
    public void testRemove() throws Exception {
        JsonNode selection = JSONPathExtractor.extract(testJson, "/store/book/0/price");
        assertNotNull(selection);
        assertThat(selection.asDouble(), is(8.95));

        JsonNode remove = JSONPathExtractor.remove(testJson, "/store/book/0/price");

        assertNotNull(remove);

        assertThat(JSONPathExtractor.extract(remove, "/store/book/0/price").isMissingNode(), is(true));
        assertThat(JSONPathExtractor.extract(remove, "/store/book/1/price").isMissingNode(), is(false));

        remove = JSONPathExtractor.remove(testJson, "/store/book");

        assertNotNull(remove);

        assertThat(JSONPathExtractor.extract(remove, "/store/book/").isMissingNode(), is(true));
        assertThat(JSONPathExtractor.extract(remove, "/store").isMissingNode(), is(false));

        remove = JSONPathExtractor.remove(testJson, "/store");

        assertNotNull(remove);

        assertThat(JSONPathExtractor.extract(remove, "/store").isMissingNode(), is(true));
    }

    @Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode obj = mapper.readTree(testJson);

        JsonNode res = obj.at("/store/book");

        assertFalse(res.isMissingNode());

        JsonPointer ptr = JsonPointer.compile("/store/book/0/price");
    }
}