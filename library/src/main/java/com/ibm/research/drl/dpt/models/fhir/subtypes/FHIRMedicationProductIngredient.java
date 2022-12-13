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
import com.ibm.research.drl.dpt.models.fhir.FHIRReference;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRRatio;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRMedicationProductIngredient {

    private FHIRReference item;
    private FHIRRatio amount;

    public FHIRRatio getAmount() {
        return amount;
    }

    public void setAmount(FHIRRatio amount) {
        this.amount = amount;
    }

    public FHIRReference getItem() {
        return item;
    }

    public void setItem(FHIRReference item) {
        this.item = item;
    }
}


