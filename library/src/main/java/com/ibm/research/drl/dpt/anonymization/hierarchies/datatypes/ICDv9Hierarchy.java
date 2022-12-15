/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.ICDv9Manager;
import com.ibm.research.drl.dpt.models.ICD;

import java.util.Collection;

public class ICDv9Hierarchy extends MaterializedHierarchy {
    private final static ICDv9Manager ICD_V9_MANAGER = ICDv9Manager.getInstance();

    private static final ICDv9Hierarchy instance = new ICDv9Hierarchy();

    public static ICDv9Hierarchy getInstance() {
        return instance;
    }

    private ICDv9Hierarchy() {
        super();

        Collection<ICD> icdList = ICD_V9_MANAGER.getItemList();
        for (ICD icd : icdList) {
            String[] terms = new String[4];
            terms[0] = icd.getCode();
            terms[1] = icd.getCategoryCode();
            terms[2] = icd.getChapterCode();
            terms[3] = "*";
            add(terms);
        }
    }
}

