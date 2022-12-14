/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRIdentifier;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRDeviceComponentProductionSpecification {

    private FHIRCodeableConcept specType;
    private FHIRIdentifier componentId;
    private String productionSpec;

    public FHIRCodeableConcept getSpecType() {
        return specType;
    }

    public void setSpecType(FHIRCodeableConcept specType) {
        this.specType = specType;
    }

    public FHIRIdentifier getComponentId() {
        return componentId;
    }

    public void setComponentId(FHIRIdentifier componentId) {
        this.componentId = componentId;
    }

    public String getProductionSpec() {
        return productionSpec;
    }

    public void setProductionSpec(String productionSpec) {
        this.productionSpec = productionSpec;
    }
}


