/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.RaceManager;
import com.ibm.research.drl.dpt.models.Race;

import java.util.Collection;

public class RaceHierarchy extends MaterializedHierarchy {
    private final static RaceManager RACE_MANAGER = RaceManager.getInstance();

    private static RaceHierarchy instance = new RaceHierarchy();
    public static RaceHierarchy getInstance() {return instance;}

    private RaceHierarchy() {
        super();

        Collection<Race> races = RACE_MANAGER.getItemList();
        for(Race race: races) {
            String[] terms = new String[2];
            terms[0] = race.getName();
            terms[1] = "*";
            add(terms);
        }
        
        String[] terms = new String[2];
        terms[0] = "Other";
        terms[1] = "*";
        add(terms);
    }
}

