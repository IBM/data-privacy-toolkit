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

package com.ibm.research.drl.dpt.spark.task.option;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;

import java.util.List;
import java.util.Objects;

public class IdentificationOptions implements TaskOptions {
    private final String localization;
    private final int firstN;
    private final double sampleSize;

    private final IdentificationConfiguration configuration;
    private final JsonNode identifiers;

    @JsonCreator
    public IdentificationOptions(
            @JsonProperty("localization") String localization,
            @JsonProperty(value = "firstN", defaultValue = "0") int firstN,
            @JsonProperty(value = "sampleSize", defaultValue = "0.0") double sampleSize,
            @JsonProperty("identifiers") JsonNode identifiers,
            @JsonProperty("configuration") IdentificationConfiguration configuration
    ) {
        this.localization = localization;
        this.firstN = firstN;
        this.sampleSize = sampleSize;
        this.identifiers = identifiers;
        this.configuration = configuration;
    }

    public String getLocalization() {
        return localization;
    }

    public int getFirstN() {
        return firstN;
    }

    public JsonNode getIdentifiers() {
        return identifiers;
    }

    public double getSampleSize() {
        return sampleSize;
    }

    public IdentificationConfiguration getConfiguration() {
        return Objects.requireNonNullElse(this.configuration, IdentificationConfiguration.DEFAULT);
    }
}
