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
package com.ibm.research.drl.dpt.spark.dataset.reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.DatasetOptions;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DatabaseDatasetReference extends DatasetReference {
    private final String datasourceURL;
    private final AuthenticationCredential credentials;
    private final String tableReference;

    @JsonCreator
    public DatabaseDatasetReference(
            @JsonProperty("datasourceURL") String datasourceURL,
            @JsonProperty("credentials") AuthenticationCredential credentials,
            @JsonProperty("tableReference") String tableReference) {
        this.datasourceURL = datasourceURL;
        this.credentials = credentials;
        this.tableReference = tableReference;
    }

    public String getDatasourceURL() {
        return datasourceURL;
    }

    public AuthenticationCredential getCredentials() {
        return credentials;
    }

    public String getTableReference() {
        return tableReference;
    }

    @JsonIgnore
    public DataTypeFormat getFormat() { throw new NotImplementedException(); }

    @JsonIgnore
    public DatasetOptions getOptions() { throw new NotImplementedException(); }

    @Override
    public Dataset<Row> readDataset(SparkSession sparkSession, String inputReference) {
        throw new NotImplementedException();
    }

    public void writeDataset(Dataset<Row> outputDataset, String path) {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("datasourceURL", datasourceURL)
                .append("credentials", credentials)
                .append("tableReference", tableReference)
                .toString();
    }
}
