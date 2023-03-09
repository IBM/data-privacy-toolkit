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

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DateYYYYMMDDHierarchy implements GeneralizationHierarchy {
    private static final String TOP_TERM = "*";
    private static final int TOP_LEVEL = 3;
    private static final int YEAR_LEVEL = 2;
    private static final int MONTH_LEVEL = 1;
    private static final int DAY_LEVEL = 0;

    @Override
    public int getHeight() {
        return TOP_LEVEL + 1;
    }

    @Override
    public long getTotalLeaves() {
        return (
                10L * 10L * 10L * 10L // YEARS
                        * (12L // MONTHS
                        * 31L) // DAYS (UPPER BOUND)
        );
    }

    @Override
    public int leavesForNode(String value) {
        return getNodeLeaves(value).size();
    }

    @Override
    public Set<String> getNodeLeaves(String value) {
        String[] parts = value.split("-");

        switch (parts.length) {
            case 1:
                return enumerateDaysInYear(parts[0]);
            case 2:
                return enumerateDaysInYearMonth(parts[0], parts[1]);
            case 3:
                return Collections.singleton(value);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Set<String> enumerateDaysInYearMonth(String year, String month) {
        LocalDate from = LocalDate.of(
                Year.parse(year).getValue(),
                Month.of(Integer.parseInt(month, 10)),
                1
        );

        return enumerateDays(from, from.plus(1, ChronoUnit.MONTHS));
    }

    private Set<String> enumerateDaysInYear(String year) {
        LocalDate from = LocalDate.of(
                Year.parse(year).getValue(),
                Month.JANUARY.getValue(),
                1
        );

        return enumerateDays(from, from.plus(1, ChronoUnit.YEARS));
    }

    private Set<String> enumerateDays(LocalDate from, LocalDate to) {
        Set<String> days = new HashSet<>();

        for (; from.isBefore(to); from = from.plus(1, ChronoUnit.DAYS)) {
            days.add(from.toString());
        }

        return days;
    }

    @Override
    public int getNodeLevel(String value) {
        if (value.equals(TOP_TERM)) return TOP_LEVEL;

        String[] parts = value.split("-");

        switch (parts.length) {
            case 1:
                return YEAR_LEVEL;
            case 2:
                return MONTH_LEVEL;
            case 3:
                return DAY_LEVEL;

            default:
                throw new RuntimeException("Unknown: " + value);
        }
    }

    @Override
    public String getTopTerm() {
        return TOP_TERM;
    }

    @Override
    public String encode(String value, int level, boolean randomizeOnFail) {
        if (TOP_LEVEL == level) return getTopTerm();
        if (DAY_LEVEL == level) return value;

        String[] parts = value.split("-");

        if (3 < parts.length) {
            if (randomizeOnFail) {
                return getTopTerm();
            }
        }

        switch (level) {
            case MONTH_LEVEL:
                if (2 <= parts.length) {
                    return parts[0] + '-' + parts[1];
                }
            case YEAR_LEVEL:
                if (1 <= parts.length) {
                    return parts[0];
                }
            default:
                if (randomizeOnFail) {
                    return getTopTerm();
                }
                throw new IllegalArgumentException();
        }
    }
}
