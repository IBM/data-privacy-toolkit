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
package com.ibm.research.drl.dpt.configuration;


import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataFormatPropertiesHelper {

    public static Map<DataTypeFormat, DataFormatProperties> buildProperties(InputStream inputStream) throws IOException {
        Map<DataTypeFormat, DataFormatProperties> results = new HashMap<>();

        JsonNode contents = JsonUtils.MAPPER.readTree(inputStream);

        contents.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode jsonNode = entry.getValue();

            Set<DataTypeFormat> validOutputs = new HashSet<>();

            JsonNode outputNode = jsonNode.get("output");
            outputNode.forEach(o -> {
                validOutputs.add(DataTypeFormat.valueOf(o.asText()));
            });

            DataFormatProperties properties = new DataFormatProperties(
                    jsonNode.get("identification").asBoolean(),
                    jsonNode.get("vulnerabilityAssessment").asBoolean(),
                    jsonNode.get("masking").asBoolean(),
                    jsonNode.get("anonymization").asBoolean(),
                    jsonNode.get("freeText").asBoolean(),
                    validOutputs
            );

            results.put(DataTypeFormat.valueOf(key), properties);
        });

        return results;
    }

}
