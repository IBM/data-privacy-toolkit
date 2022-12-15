/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultColumnInformation implements ColumnInformation {
    private final boolean forLinking;

    public DefaultColumnInformation() {
        this(false);
    }

    @JsonCreator
    public DefaultColumnInformation(
            @JsonProperty("forLinking") boolean forLinking) {
        this.forLinking = forLinking;
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
        return forLinking;
    }

    @Override
    @JsonIgnore
    public ColumnType getColumnType() {
        return ColumnType.NORMAL;
    }

    @Override
    @JsonIgnore
    public String getRepresentation() {
        return null;
    }
}
