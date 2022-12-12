/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensitiveColumnInformation implements ColumnInformation {
    private final boolean isForLinking;

    public SensitiveColumnInformation() {
        this(false);
    }

    @JsonCreator
    public SensitiveColumnInformation(
            @JsonProperty("forLinking") boolean forLinking) {
        this.isForLinking = forLinking;
    }

    @Override
    @JsonIgnore
    public boolean isCategorical() {
        return false;
    }

    @Override
    @JsonIgnore
    public double getWeight() {
        return 1.0;
    }

    @Override
    public boolean isForLinking() {
        return isForLinking;
    }

    @Override
    @JsonIgnore
    public ColumnType getColumnType() {
        return ColumnType.SENSITIVE;
    }

    @Override
    @JsonIgnore
    public String getRepresentation() {
        return null;
    }
}
