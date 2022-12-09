/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.io.Serializable;

public class DataMaskingTarget implements Serializable {
    
    private final ProviderType providerType;
    private final String targetPath;

    public ProviderType getProviderType() {
        return providerType;
    }

    public String getTargetPath() {
        return targetPath;
    }

    @JsonCreator
    private DataMaskingTarget(
            @JsonProperty(value = "providerType", required = true)
            String providerType,
            @JsonProperty(value = "targetPath", required = true)
            String targetPath) {
        this.providerType = ProviderType.valueOf(providerType);
        this.targetPath = targetPath;
    }

    public DataMaskingTarget(ProviderType providerType, String targetPath) {
        this.providerType = providerType;
        this.targetPath = targetPath;
    }
}
