/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ibm.research.drl.dpt.anonymization.constraints.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "KAnonymity", value = KAnonymity.class),
        @JsonSubTypes.Type(name = "DistinctLDiversity", value = DistinctLDiversity.class),
        @JsonSubTypes.Type(name = "EntropyLDiversity", value = EntropyLDiversity.class),
        @JsonSubTypes.Type(name = "RecursiveCLDiversity", value = RecursiveCLDiversity.class),
        @JsonSubTypes.Type(name = "TCloseness", value = TCloseness.class),
})
public interface
PrivacyConstraint extends Serializable {
    boolean check(PrivacyMetric metric);

    boolean check(Partition partition, List<Integer> sensitiveColumns);

    boolean requiresAnonymizedPartition();

    int contentRequirements();

    void sanityChecks(IPVDataset dataset);

    void initialize(IPVDataset dataset, List<ColumnInformation> columnInformationList);

    @JsonIgnore
    PrivacyMetric getMetricInstance();
}
