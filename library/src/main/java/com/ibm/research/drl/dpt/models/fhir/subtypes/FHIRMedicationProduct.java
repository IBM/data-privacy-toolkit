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
package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;

import java.util.Collection;


/** FHIRMedicationProduct FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationProduct {
    /** Constructs a FHIRMedicationProduct. */
    public FHIRMedicationProduct() {}

    private FHIRCodeableConcept form;
    private Collection<FHIRMedicationProductBatch> batch;
    private Collection<FHIRMedicationProductIngredient> ingredient;

    /**
     * Returns the form.
     * @return the form
     */
    public FHIRCodeableConcept getForm() {
        return form;
    }

    /**
     * Sets the form.
     * @param form the form
     */
    public void setForm(FHIRCodeableConcept form) {
        this.form = form;
    }

    /**
     * Returns the batch.
     * @return the batch
     */
    public Collection<FHIRMedicationProductBatch> getBatch() {
        return batch;
    }

    /**
     * Sets the batch.
     * @param batch the batch
     */
    public void setBatch(Collection<FHIRMedicationProductBatch> batch) {
        this.batch = batch;
    }

    /**
     * Returns the ingredient.
     * @return the ingredient
     */
    public Collection<FHIRMedicationProductIngredient> getIngredient() {
        return ingredient;
    }

    /**
     * Sets the ingredient.
     * @param ingredient the ingredient
     */
    public void setIngredient(Collection<FHIRMedicationProductIngredient> ingredient) {
        this.ingredient = ingredient;
    }

}


