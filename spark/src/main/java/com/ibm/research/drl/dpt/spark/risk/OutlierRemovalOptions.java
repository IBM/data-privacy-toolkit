/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
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
