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
