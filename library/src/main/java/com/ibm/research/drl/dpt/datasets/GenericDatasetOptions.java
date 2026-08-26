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
package com.ibm.research.drl.dpt.datasets;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.HashMap;
import java.util.Map;

/** Generic dataset options that accept arbitrary key-value properties. */
public final class GenericDatasetOptions implements DatasetOptions {
    private final Map<String, JsonNode> properties;

    /** Constructs a GenericDatasetOptions with an empty properties map. */
    public GenericDatasetOptions() {
        this.properties = new HashMap<>();
    }

    /**
     * Sets a dataset option.
     *
     * @param key   the option key
     * @param value the option value
     */
    @JsonAnySetter
    public void setOption(String key, JsonNode value) {
        this.properties.put(key, value);
    }

    /**
     * Returns all dataset option properties.
     *
     * @return map of option key to value
     */
    @JsonAnyGetter
    public Map<String, JsonNode> getProperties() {
        return properties;
    }

    /**
     * Returns the value of the named property, or {@code NullNode} if absent.
     *
     * @param key the property key
     * @return the property value, or NullNode if not set
     */
    public JsonNode getProperty(String key) {
        return properties.getOrDefault(key, NullNode.getInstance());
    }
}
