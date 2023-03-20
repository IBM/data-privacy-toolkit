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
package com.ibm.research.drl.dpt.rest.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ibm.research.drl.dpt.rest.models.serializers.CompleteAlgorithmConfigurationSerializer;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = CompleteAlgorithmConfigurationSerializer.class)
public class CompleteAlgorithmConfiguration {
    public enum SUPPORTED_CONFIGURATION {
        k,
        l,
        c,
        suppressionRate,
        lDiversityAlgorithm,
        epsilon
    }
    private final Map<SUPPORTED_CONFIGURATION, Object> options;

    public CompleteAlgorithmConfiguration() {
        this.options = new HashMap<>();
    }

    @Override
    public String toString() {
        return "CompleteAlgorithmConfiguration[" + options + "]";
    }

    @JsonAnySetter
    public void setOption(String name, Object value) {
        setOption(SUPPORTED_CONFIGURATION.valueOf(name), value);
    }

    public void setOption(SUPPORTED_CONFIGURATION name, Object value) {
        if (value instanceof Byte || value instanceof Character || value instanceof Long) throw new IllegalArgumentException("Unsupported data type: " + value.getClass().getCanonicalName());
        if (value instanceof Float) {
            float f = (float) value;
            value = (double) f;
        }

        this.options.put(name, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;
        if (obj instanceof CompleteAlgorithmConfiguration) {
            CompleteAlgorithmConfiguration other = (CompleteAlgorithmConfiguration) obj;
            if (other.options.size() != this.options.size()) return false;

            for (SUPPORTED_CONFIGURATION name : getOptionNames()) {
                if (other.hasOption(name)) {
                    Object myValue = getOption(name);
                    Object theirValue = other.getOption(name);

                    if ((null == theirValue && null != myValue) || (null != myValue && !myValue.equals(theirValue))) return false;
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public Object getOption(SUPPORTED_CONFIGURATION name) {
        return options.get(name);
    }

    public Iterable<? extends SUPPORTED_CONFIGURATION> getOptionNames() {
        return options.keySet();
    }

    public boolean hasOption(SUPPORTED_CONFIGURATION name) {
        return options.containsKey(name);
    }
}
