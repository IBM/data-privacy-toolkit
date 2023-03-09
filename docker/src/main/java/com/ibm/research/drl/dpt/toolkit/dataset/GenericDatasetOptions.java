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
package com.ibm.research.drl.dpt.toolkit.dataset;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;

import java.util.HashMap;
import java.util.Map;

public final class GenericDatasetOptions implements DatasetOptions {
    private final Map<String, JsonNode> properties;

    public GenericDatasetOptions() {
        this.properties = new HashMap<>();
    }

    @JsonAnySetter
    public void setOption(String key, JsonNode value) {
        this.properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return properties;
    }

    public JsonNode getProperty(String key) {
        return properties.getOrDefault(key, NullNode.getInstance());
    }
}