/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/
package com.ibm.research.drl.prima.datasets;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;

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
