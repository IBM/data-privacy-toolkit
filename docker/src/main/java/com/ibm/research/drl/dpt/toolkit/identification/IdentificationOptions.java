/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.identification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;

import java.util.List;

public class IdentificationOptions extends TaskOptions {
    private String localization;
    private int firstN;
    private final List<JsonNode> identifiers;
    private ComplianceFramework framework;
    private boolean buildMaskingConfiguration;

    @JsonCreator
    public IdentificationOptions(
            @JsonProperty("localization") String localization,
            @JsonProperty("firstN") int firstN,
            @JsonProperty("identifiers") List<JsonNode> identifiers,
            @JsonProperty("complianceFramework") ComplianceFramework framework,
            @JsonProperty("buildMaskingConfiguration") boolean buildMaskingConfiguration
    ) {
        this.localization = localization;
        this.firstN = firstN;
        this.identifiers = identifiers;
        this.framework = framework;
        this.buildMaskingConfiguration = buildMaskingConfiguration;
    }

    public String getLocalization() {
        return localization;
    }

    public int getFirstN() {
        return firstN;
    }

    public List<JsonNode> getIdentifiers() {
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
}
