/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;


import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;

import java.time.Year;
import java.time.temporal.ChronoUnit;

public class YOBHierarchy extends MaterializedHierarchy {
    private static final YOBHierarchy instance = new YOBHierarchy();

    private static final int LEAF = 0;

    public static YOBHierarchy getInstance() {
        return instance;
    }

    private YOBHierarchy() {
        super();

        int[] intervals = new int[]{2, 4, 8};
        String topTerm = "*";

        final Year currentYear = Year.now();
        String[] terms = new String[intervals.length + 2];
        terms[terms.length - 1] = topTerm;

        for (
                Year year = currentYear.minus(160, ChronoUnit.YEARS);
                year.isBefore(currentYear);
                year = year.plus(1, ChronoUnit.YEARS)) {

            terms[LEAF] = year.toString();

            for (int i = 0; i < intervals.length; i++) {
                int interval = intervals[i];
                int start = year.minus(year.getValue() % interval, ChronoUnit.YEARS).getValue();
                int end = start + interval;

                String term = start + "-" + (end - 1);
                terms[1 + i] = term;
            }

            add(terms);
        }
    }
}
