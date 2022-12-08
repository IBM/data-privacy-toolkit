
/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/

package com.ibm.research.drl.dpt.models.fhir.subtypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCodeableConcept;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRPatientAnimal {
    public FHIRCodeableConcept getSpecies() {
        return species;
    }

    public void setSpecies(FHIRCodeableConcept species) {
        this.species = species;
    }

    public FHIRCodeableConcept getBreed() {
        return breed;
    }

    public void setBreed(FHIRCodeableConcept breed) {
        this.breed = breed;
    }

    public FHIRCodeableConcept getGenderStatus() {
        return genderStatus;
    }

    public void setGenderStatus(FHIRCodeableConcept genderStatus) {
        this.genderStatus = genderStatus;
    }

    private FHIRCodeableConcept species;
    private FHIRCodeableConcept breed;
    private FHIRCodeableConcept genderStatus;
}


