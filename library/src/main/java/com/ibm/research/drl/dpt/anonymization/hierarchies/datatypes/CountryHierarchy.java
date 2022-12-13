/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.Country;

import java.util.Collection;

public class CountryHierarchy extends MaterializedHierarchy {

    private final static CountryManager countryManager = CountryManager.getInstance();

    private final static CountryHierarchy instance = new CountryHierarchy();

    public static CountryHierarchy getInstance() {
        return instance;
    }

    private CountryHierarchy() {
        Collection<Country> countries = countryManager.getItemList();
        for (Country country : countries) {
            String[] terms = new String[3];
            terms[0] = country.getName();
            terms[1] = country.getContinent();
            terms[2] = "*";
            add(terms);
        }
    }
}

