/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

import java.util.Collection;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationProduct {
    private FHIRCodeableConcept form;
    private Collection<FHIRMedicationProductBatch> batch;
    private Collection<FHIRMedicationProductIngredient> ingredient;

    public FHIRCodeableConcept getForm() {
        return form;
    }

    public void setForm(FHIRCodeableConcept form) {
        this.form = form;
    }

    public Collection<FHIRMedicationProductBatch> getBatch() {
        return batch;
    }

    public void setBatch(Collection<FHIRMedicationProductBatch> batch) {
        this.batch = batch;
    }

    public Collection<FHIRMedicationProductIngredient> getIngredient() {
        return ingredient;
    }

    public void setIngredient(Collection<FHIRMedicationProductIngredient> ingredient) {
        this.ingredient = ingredient;
    }

}


