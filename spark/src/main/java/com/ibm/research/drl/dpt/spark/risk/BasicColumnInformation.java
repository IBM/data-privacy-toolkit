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
import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;

import java.io.Serializable;


public final class BasicColumnInformation implements Serializable {
    private final String name;
    private final String target;
    
    private final boolean isAnonymised;
    private final GeneralizationHierarchy hierarchy;

    @JsonCreator
    public BasicColumnInformation(
            @JsonProperty(value = "name", required = true) String name, 
            @JsonProperty(value = "target", required = true) String target, 
            @JsonProperty(value = "isAnonymised", required = true) boolean isAnonymised,
            @JsonProperty(value = "hierarchy") JsonNode hierarchy
    ) {
        this.name = name;
        this.target = target;
        this.isAnonymised = isAnonymised;
        
        if (hierarchy == null || hierarchy.isNull()) {
            this.hierarchy = null;
        }
        else {
            this.hierarchy = AnonymizationOptions.getHierarchyFromJsonNode(hierarchy);
        }
        
        if (this.isAnonymised && this.hierarchy == null) {
            throw new MisconfigurationException("column is marked as anonymised but hierarchy is missing");
        }
    }

    public BasicColumnInformation(
            String name,
            String target,
            boolean isAnonymised,
            GeneralizationHierarchy hierarchy
    ) {
        this.name = name;
        this.target = target;
        this.isAnonymised = isAnonymised;
        this.hierarchy = hierarchy; 
        
    }

    public boolean isAnonymised() {
        return isAnonymised;
    }

    public String getTarget() {
        return target;
    }

    public String getName() {
        return name;
    }

    public GeneralizationHierarchy getHierarchy() {
        return hierarchy;
    }
}
