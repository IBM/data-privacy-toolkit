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
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Default implementation of {@link ColumnInformation} for non-quasi-identifier columns. */
public class DefaultColumnInformation implements ColumnInformation {
    /** Whether this column is used for linking. */
    private final boolean forLinking;

    /** Constructs a DefaultColumnInformation with forLinking set to false. */
    public DefaultColumnInformation() {
        this(false);
    }

    /**
     * Constructs a DefaultColumnInformation.
     *
     * @param forLinking whether this column is used for linking
     */
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
