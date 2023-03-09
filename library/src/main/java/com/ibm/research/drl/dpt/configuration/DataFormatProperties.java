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

import java.util.Set;

public class DataFormatProperties {

    private final boolean supportsIdentification;
    private final boolean supportsVulnerabilityAssessment;
    private final boolean supportsMasking;
    private final boolean supportsAnonymization;
    private final boolean supportsFreeText;
    private final Set<DataTypeFormat> validOutputFormats;

    public DataFormatProperties(boolean supportsIdentification, boolean supportsVulnerabilityAssessment, boolean supportsMasking,
                                boolean supportsAnonymization, boolean supportsFreeText, Set<DataTypeFormat> validOutputFormats) {
        this.supportsAnonymization = supportsAnonymization;
        this.supportsIdentification = supportsIdentification;
        this.supportsMasking = supportsMasking;
        this.supportsVulnerabilityAssessment = supportsVulnerabilityAssessment;
        this.supportsFreeText = supportsFreeText;
        this.validOutputFormats = validOutputFormats;
    }

    public boolean supportsFreeText() {
        return this.supportsFreeText;
    }

    public boolean supportsIdentification() {
        return supportsIdentification;
    }

    public boolean supportsVulnerabilityAssessment() {
        return supportsVulnerabilityAssessment;
    }

    public boolean supportsMasking() {
        return supportsMasking;
    }

    public boolean supportsAnonymization() {
        return supportsAnonymization;
    }

    public Set<DataTypeFormat> getValidOutputFormats() {
        return validOutputFormats;
    }
}
