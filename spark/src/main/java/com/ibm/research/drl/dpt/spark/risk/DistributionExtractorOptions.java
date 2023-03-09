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
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class DistributionExtractorOptions {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ProjectedExpression {
        private AggregationType aggregationType;
        private String columnName;
        private String tableName;
        private String query;

        public void setAggregationType(AggregationType aggregationType) {
            this.aggregationType = aggregationType;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public AggregationType getAggregationType() {
            return aggregationType;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public static final class BinningCondition {
        private BinningType type;
        private double binSize;
        private long binNumber;
        private boolean returnMidPoint;

        public BinningType getType() {
            return type;
        }

        public void setType(BinningType type) {
            this.type = type;
        }

        public double getBinSize() {
            return binSize;
        }

        public void setBinSize(double binSize) {
            this.binSize = binSize;
        }

        public long getBinNumber() {
            return binNumber;
        }

        public void setBinNumber(long binNumber) {
            this.binNumber = binNumber;
        }

        public boolean isReturnMidPoint() {
            return returnMidPoint;
        }

        public void setReturnMidPoint(boolean returnMidPoint) {
            this.returnMidPoint = returnMidPoint;
        }

        public enum BinningType {
            SIZE,
            NUMBER,
            NONE
        }
    }

    private final List<String> identityFields;
    private final List<ProjectedExpression> thresholds;
    private final String dimensionDescription;
    private final String principalDescription;
    private final BinningCondition binningCondition;
    
    @JsonCreator
    public DistributionExtractorOptions(
            @JsonProperty(value="identityFields", required = true) final List<String> identityFields,
            @JsonProperty(value = "thresholds", required = true) final List<ProjectedExpression> thresholds,
            @JsonProperty(value = "principalDescription", required = true) final String principalDescription,
            @JsonProperty(value = "dimensionDescription", required = true) final String dimensionDescription,
            @JsonProperty(value = "binningCondition") final BinningCondition binningCondition ) {
        this.identityFields = identityFields;
        this.thresholds = thresholds;
        this.principalDescription = principalDescription;
        this.dimensionDescription = dimensionDescription;
        this.binningCondition = binningCondition;
    }

    public List<String> getIdentityFields() {
        return identityFields;
    }

    public List<ProjectedExpression> getThresholds() {
        return thresholds;
    }

    public String getDimensionDescription() {
        return dimensionDescription;
    }

    public String getPrincipalDescription() {
        return principalDescription;
    }

    public BinningCondition getBinningCondition() {
        return binningCondition;
    }
}
