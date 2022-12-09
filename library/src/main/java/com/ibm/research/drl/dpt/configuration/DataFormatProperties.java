/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
