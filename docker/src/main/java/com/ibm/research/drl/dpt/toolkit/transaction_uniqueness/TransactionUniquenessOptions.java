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
package com.ibm.research.drl.dpt.toolkit.transaction_uniqueness;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.toolkit.task.TaskOptions;

import java.util.List;
import java.util.Optional;

public class TransactionUniquenessOptions extends TaskOptions {
    private final List<String> externallyObservableFields;
    private final List<String> identityFields;
    private final Integer factor;
    private final Integer threshold;
    private final boolean exploreExternallyObservableFields;

    @JsonCreator
    public TransactionUniquenessOptions(
            @JsonProperty("identityFields") List<String> identityFields,
            @JsonProperty("externallyObservableFields") List<String> externallyObservableFields,
            @JsonProperty("factor") Integer factor,
            @JsonProperty("threshold") Integer threshold,
            @JsonProperty("exploreExternallyObservableFields") boolean exploreExternallyObservableFields
    ) {
        this.externallyObservableFields = externallyObservableFields;
        this.identityFields = identityFields;
        this.factor = Optional.ofNullable(factor).orElse(1);
        this.threshold = Optional.ofNullable(threshold).orElse(1);
        this.exploreExternallyObservableFields = exploreExternallyObservableFields;
    }

    public List<String> getIdentityFields() {
        return identityFields;
    }

    public List<String> getExternallyObservableFields() {
        return externallyObservableFields;
    }

    public Integer getFactor() {
        return factor;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public boolean isExploreExternallyObservableFields() { return exploreExternallyObservableFields; }
}
