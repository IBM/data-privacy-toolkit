/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
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
