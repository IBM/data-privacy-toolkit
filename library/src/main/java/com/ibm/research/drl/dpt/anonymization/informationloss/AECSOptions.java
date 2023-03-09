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
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AECSOptions implements InformationMetricOptions {
    private final Map<String, Object> values = new HashMap<>();

    @Override
    public int getIntValue(String optionName) {
        return (int) values.get(optionName);
    }

    @Override
    public String getStringValue(String optionName) {
        return (String) values.get(optionName);
    }

    @Override
    public boolean getBooleanValue(String optionName) {
        return (boolean) values.get(optionName);
    }

    /**
     * Instantiates a new Aecs options.
     *
     * @param normalized the normalized
     */
    @JsonCreator
    public AECSOptions(@JsonProperty("normalized") boolean normalized, @JsonProperty("k") int k) {
        values.put("normalized", normalized);
        values.put("k", k);
    }
}

