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
package com.ibm.research.drl.dpt.toolkit.identification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;

import java.util.List;
import java.util.Objects;

public class IdentificationOptions extends TaskOptions {
    private String localization;
    private int firstN;
    private final JsonNode identifiers;
    private ComplianceFramework framework;
    private boolean buildMaskingConfiguration;

    private final IdentificationConfiguration configuration;

    @JsonCreator
    public IdentificationOptions(
            @JsonProperty("localization") String localization,
            @JsonProperty("firstN") int firstN,
            @JsonProperty("identifiers") JsonNode identifiers,
            @JsonProperty("complianceFramework") ComplianceFramework framework,
            @JsonProperty("buildMaskingConfiguration") boolean buildMaskingConfiguration,
            @JsonProperty("configuration") IdentificationConfiguration configuration
    ) {
        this.localization = localization;
        this.firstN = firstN;
        this.identifiers = identifiers;
        this.framework = framework;
        this.buildMaskingConfiguration = buildMaskingConfiguration;
        this.configuration = Objects.requireNonNullElse(configuration, IdentificationConfiguration.DEFAULT);
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

    public void setLocalization(String localization) {
        this.localization = localization;
    }

    public void setFirstN(int firstN) {
        this.firstN = firstN;
    }

    public void setFramework(ComplianceFramework framework) {
        this.framework = framework;
    }

    public void setBuildMaskingConfiguration(boolean buildMaskingConfiguration) {
        this.buildMaskingConfiguration = buildMaskingConfiguration;
    }

    public ComplianceFramework getFramework() {
        return framework;
    }

    public boolean getBuildMaskingConfiguration() {
        return buildMaskingConfiguration;
    }

    public IdentificationConfiguration getConfiguration() {
        return configuration;
    }
}
