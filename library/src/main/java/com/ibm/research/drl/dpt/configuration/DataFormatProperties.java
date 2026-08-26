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

/**
 * Describes the capabilities of a data format (identification, masking, anonymization, etc.).
 */
public class DataFormatProperties {

    private final boolean supportsIdentification;
    private final boolean supportsVulnerabilityAssessment;
    private final boolean supportsMasking;
    private final boolean supportsAnonymization;
    private final boolean supportsFreeText;
    private final Set<DataTypeFormat> validOutputFormats;

    /**
     * Constructs a DataFormatProperties.
     *
     * @param supportsIdentification         whether identification is supported
     * @param supportsVulnerabilityAssessment whether vulnerability assessment is supported
     * @param supportsMasking                whether masking is supported
     * @param supportsAnonymization          whether anonymization is supported
     * @param supportsFreeText               whether free-text processing is supported
     * @param validOutputFormats             the set of valid output formats
     */
    public DataFormatProperties(boolean supportsIdentification, boolean supportsVulnerabilityAssessment, boolean supportsMasking,
                                boolean supportsAnonymization, boolean supportsFreeText, Set<DataTypeFormat> validOutputFormats) {
        this.supportsAnonymization = supportsAnonymization;
        this.supportsIdentification = supportsIdentification;
        this.supportsMasking = supportsMasking;
        this.supportsVulnerabilityAssessment = supportsVulnerabilityAssessment;
        this.supportsFreeText = supportsFreeText;
        this.validOutputFormats = validOutputFormats;
    }

    /**
     * Returns whether free-text processing is supported.
     *
     * @return true if free-text is supported
     */
    public boolean supportsFreeText() {
        return this.supportsFreeText;
    }

    /**
     * Returns whether identification is supported.
     *
     * @return true if identification is supported
     */
    public boolean supportsIdentification() {
        return supportsIdentification;
    }

    /**
     * Returns whether vulnerability assessment is supported.
     *
     * @return true if vulnerability assessment is supported
     */
    public boolean supportsVulnerabilityAssessment() {
        return supportsVulnerabilityAssessment;
    }

    /**
     * Returns whether masking is supported.
     *
     * @return true if masking is supported
     */
    public boolean supportsMasking() {
        return supportsMasking;
    }

    /**
     * Returns whether anonymization is supported.
     *
     * @return true if anonymization is supported
     */
    public boolean supportsAnonymization() {
        return supportsAnonymization;
    }

    /**
     * Returns the set of valid output formats.
     *
     * @return the valid output formats
     */
    public Set<DataTypeFormat> getValidOutputFormats() {
        return validOutputFormats;
    }
}
