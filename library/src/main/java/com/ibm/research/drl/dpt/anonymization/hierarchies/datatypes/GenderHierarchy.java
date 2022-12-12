/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.GenderManager;
import com.ibm.research.drl.dpt.models.Sex;

import java.util.Collection;

public class GenderHierarchy extends MaterializedHierarchy {
    private final static GenderManager genderManager = GenderManager.getInstance();

    private static GenderHierarchy instance = new GenderHierarchy();
    public static GenderHierarchy getInstance() {return instance;}

    private GenderHierarchy() {
        super();

        Collection<Sex> genders = genderManager.getItemList();
        for(Sex gender: genders) {
            String[] terms = new String[2];
            terms[0] = gender.getName();
            terms[1] = "*";
            add(terms);
        }
    }
}

