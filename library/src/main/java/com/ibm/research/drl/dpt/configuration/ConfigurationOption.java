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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class ConfigurationOption implements Serializable {
    private String description;
    private String category;
    private Object value;

    /**
     * Instantiates a new Configuration option.
     *
     * @param value       the value
     * @param description the description
     * @param category    the category
     */
    @JsonCreator
    public ConfigurationOption(@JsonProperty("value") Object value, @JsonProperty("description") String description, @JsonProperty("category") String category) {
        this.description = description;
        this.value = value;
        this.category = category;
    }

    /* for serialization only */
    private ConfigurationOption() {
        description = null;
        category = null;
        value = null;
    }

    /**
     * Instantiates a new Configuration option.
     *
     * @param value       the value
     * @param description the description
     */
    public ConfigurationOption(Object value, String description) {
        this.description = description;
        this.value = value;
        this.category = "Misc.";
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public String getCategory() {
        return this.category;
    }


    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(description);
        out.writeObject(category);

        if (Objects.isNull(value) || value instanceof Serializable) {
            out.writeObject(value);
        } else if (value instanceof JsonNode) {
            out.writeObject(value.toString().getBytes());
        } else {
            throw new RuntimeException("Not serializable: " + value.getClass().getCanonicalName());
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.description = (String) in.readObject();
        this.category = (String) in.readObject();

        Object value = in.readObject();

        if (value instanceof byte[]) {
            this.value = JsonUtils.MAPPER.readTree((byte[]) value);
        } else {
            this.value = value;
        }
    }
}
