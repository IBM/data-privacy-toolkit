/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.transaction_uniqueness;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionUniquenessReportColumnContribution {
    private final String column;
    private final int uniqueTransactions;
    private final int uniqueIDs;

    @JsonCreator
    public TransactionUniquenessReportColumnContribution(
            @JsonProperty("column") String column,
            @JsonProperty("uniqueTransactions") int uniqueTransactions,
            @JsonProperty("uniqueIDs") int uniqueIDs) {
        this.column = column;
        this.uniqueTransactions = uniqueTransactions;
        this.uniqueIDs = uniqueIDs;
    }

    public String getColumn() { return column; }

    public int getUniqueTransactions() {
        return uniqueTransactions;
    }

    public int getUniqueIDs() {
        return uniqueIDs;
    }
}
