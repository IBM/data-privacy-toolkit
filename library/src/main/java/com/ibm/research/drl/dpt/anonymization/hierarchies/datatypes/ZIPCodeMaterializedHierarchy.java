/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;

public class ZIPCodeMaterializedHierarchy extends MaterializedHierarchy {
    private static final ZIPCodeMaterializedHierarchy instance = new ZIPCodeMaterializedHierarchy();

    public static ZIPCodeMaterializedHierarchy getInstance() {
        return instance;
    }

    private ZIPCodeMaterializedHierarchy() {
        super();

        for (int i = 0; i < 100_000; i++) {
            String[] terms = new String[6];

            String zipcode = String.format("%05d", i);
            terms[0] = zipcode;
            terms[1] = zipcode.substring(0, 4) + "*";
            terms[2] = zipcode.substring(0, 3) + "**";
            terms[3] = zipcode.substring(0, 2) + "***";
            terms[4] = zipcode.charAt(0) + "****";
            terms[5] = "*";
            add(terms);
        }
    }
}

