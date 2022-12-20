/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/
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
