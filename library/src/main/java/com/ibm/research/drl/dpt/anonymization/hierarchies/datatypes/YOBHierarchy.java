/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;


import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;

import java.time.Year;
import java.time.temporal.ChronoUnit;

public class YOBHierarchy extends MaterializedHierarchy {
    private static final YOBHierarchy instance = new YOBHierarchy();

    private static final int LEAF = 0;

    public static YOBHierarchy getInstance() {return instance;}

    private YOBHierarchy() {
        super();

        int[] intervals = new int[] {2, 4, 8};
        String topTerm = "*";

        final Year currentYear = Year.now();
        String[] terms = new String[intervals.length + 2];
        terms[terms.length - 1] = topTerm;

        for (
                Year year = currentYear.minus(160, ChronoUnit.YEARS);
                year.isBefore(currentYear);
                year = year.plus(1, ChronoUnit.YEARS)) {

            terms[LEAF] = year.toString();

            for(int i = 0; i < intervals.length; i++) {
                int interval = intervals[i];
                int start = year.minus( year.getValue() % interval, ChronoUnit.YEARS).getValue();
                int end = start + interval;

                String term = start + "-" + (end - 1);
                terms[1 + i] = term;
            }

            add(terms);
        }
    }
}
