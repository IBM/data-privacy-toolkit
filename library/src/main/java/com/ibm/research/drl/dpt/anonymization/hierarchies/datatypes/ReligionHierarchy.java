/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.ReligionManager;
import com.ibm.research.drl.dpt.models.Religion;

import java.io.Serializable;
import java.util.Collection;

public class ReligionHierarchy extends MaterializedHierarchy implements Serializable {
    private static final ReligionManager manager = ReligionManager.getInstance();
    private static final ReligionHierarchy instance = new ReligionHierarchy();

    public static ReligionHierarchy getInstance() {
        return instance;
    }

    private ReligionHierarchy() {
        super();

        Collection<Religion> religions = manager.getItemList();

        for (final Religion religion : religions) {
            String[] terms = new String[3];
            terms[0] = religion.getName();
            terms[1] = religion.getGroup();
            terms[2] = "*";
            add(terms);
        }

        String[] terms = new String[3];
        terms[0] = "Other";
        terms[1] = "Other-Grp";
        terms[2] = "*";
        add(terms);
    }
}