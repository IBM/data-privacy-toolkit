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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OutlierRemovalOptions {
    private final String filterColumnName;
    private final List<OutlierRemovalFilter> filters;
    
    @JsonCreator
    public OutlierRemovalOptions(
            @JsonProperty(value = "filters", required = true) final List<OutlierRemovalFilter> filters,
            @JsonProperty("filterColumnName") final String filterColumnName) 
    {
        this.filters = filters;
        this.filterColumnName = filterColumnName;
    }

    public List<OutlierRemovalFilter> getFilters() {
        return filters;
    }

    public String getFilterColumnName() {
        return filterColumnName;
    }
}
