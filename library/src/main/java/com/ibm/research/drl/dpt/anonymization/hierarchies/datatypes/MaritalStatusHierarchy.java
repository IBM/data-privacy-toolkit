/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.MaritalStatusManager;
import com.ibm.research.drl.dpt.models.MaritalStatus;

import java.util.Collection;

public class MaritalStatusHierarchy extends MaterializedHierarchy {
    private final static MaritalStatusManager MARITAL_STATUS_MANAGER = MaritalStatusManager.getInstance();

    private static MaritalStatusHierarchy instance = new MaritalStatusHierarchy();
    public static MaritalStatusHierarchy getInstance() {return instance;}

    private MaritalStatusHierarchy() {
        super();

        Collection<MaritalStatus> maritalStatuses = MARITAL_STATUS_MANAGER.getItemList();
        for(MaritalStatus maritalStatus: maritalStatuses) {
            String[] terms = new String[3];
            terms[0] = maritalStatus.getName();
            terms[1] = maritalStatus.getCategory();
            terms[2] = "*";
            add(terms);
        }
    }
}

