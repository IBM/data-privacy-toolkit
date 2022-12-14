/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.transaction_uniqueness;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TransactionUniquenessReport {
    private final int totalIDs;
    private final int totalTransactions;
    private final int uniqueTransactions;
    private final int uniqueIDs;
    private final List<TransactionUniquenessReportColumnContribution> columnsContributions;

    @JsonCreator
    public TransactionUniquenessReport(
            @JsonProperty("totalIDs") int totalIDs,
            @JsonProperty("totalTransactions") int totalTransactions,
            @JsonProperty("uniqueTransactions") int uniqueTransactions,
            @JsonProperty("uniqueIDs") int uniqueIDs,
            @JsonProperty("columnsContributions") List<TransactionUniquenessReportColumnContribution> columnsContributions) {
        this.totalIDs = totalIDs;
        this.totalTransactions = totalTransactions;
        this.uniqueTransactions = uniqueTransactions;
        this.uniqueIDs = uniqueIDs;
        this.columnsContributions = columnsContributions;
    }

    public int getTotalIDs() {
        return totalIDs;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public int getUniqueTransactions() {
        return uniqueTransactions;
    }

    public int getUniqueIDs() {
        return uniqueIDs;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<TransactionUniquenessReportColumnContribution> getColumnsContributions() { return columnsContributions; }
}
