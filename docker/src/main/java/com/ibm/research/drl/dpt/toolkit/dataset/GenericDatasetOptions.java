/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
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