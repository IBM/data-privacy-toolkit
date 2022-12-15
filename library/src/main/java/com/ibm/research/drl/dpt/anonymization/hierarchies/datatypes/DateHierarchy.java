/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class DateHierarchy implements GeneralizationHierarchy {
    private static final String TOP_TERM = "*";
    private static final int TOP_LEVEL = 3;
    private static final int YEAR_LEVEL = 2;
    private static final int MONTH_LEVEL = 1;
    private static final int DAY_LEVEL = 0;

    private final SimpleDateFormat yearMonthDaySimpleDateFormat;
    private final SimpleDateFormat yearMonthSimpleDateFormat;
    private final SimpleDateFormat yearSimpleDateFormat;

    private static final SimpleDateFormat yearOnlySimpleDateFormat = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat monthOnlySimpleDateFormat = new SimpleDateFormat("MM");
    private static final SimpleDateFormat dayOnlySimpleDateFormat = new SimpleDateFormat("dd");

    public DateHierarchy() {
        this("yyyy-MM-dd");
    }

    public DateHierarchy(String pattern) {

        if (pattern.contains("y")) {
            if (pattern.contains("M")) {
                if (pattern.contains("d")) {
                    this.yearMonthDaySimpleDateFormat = new SimpleDateFormat(pattern);
                    this.yearMonthSimpleDateFormat = new SimpleDateFormat(pattern
                            .replaceAll("d+[\\/\\-., ]", "")
                            .replaceAll("[\\/\\-., ]d+", "")
                    );
                    this.yearSimpleDateFormat = new SimpleDateFormat(pattern
                            .replaceAll("d+[\\/\\-., ]", "")
                            .replaceAll("[\\/\\-., ]d+", "")
                            .replaceAll("M+[\\/\\-., ]", "")
                            .replaceAll("[\\/\\-., ]M+", "")
                    );
                } else {
                    this.yearMonthDaySimpleDateFormat = null;
                    this.yearMonthSimpleDateFormat = new SimpleDateFormat(pattern);
                    this.yearSimpleDateFormat = new SimpleDateFormat(pattern.replaceAll("M+[\\/\\-., ]?", ""));
                }
            } else {
                this.yearMonthDaySimpleDateFormat = null;
                this.yearMonthSimpleDateFormat = null;
                this.yearSimpleDateFormat = new SimpleDateFormat(pattern);
            }
        } else {
            throw new RuntimeException("Wrong date pattern supplied.");
        }
    }

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
        Date dateValue;

        try {
            dateValue = yearMonthDaySimpleDateFormat.parse(value);
            return Collections.singleton(value);
        } catch (ParseException e) {
        }

        try {
            dateValue = yearMonthSimpleDateFormat.parse(value);
            return enumerateDaysInYearMonth(yearOnlySimpleDateFormat.format(dateValue), monthOnlySimpleDateFormat.format(dateValue));
        } catch (ParseException e) {
        }

        try {
            dateValue = yearSimpleDateFormat.parse(value);
            return enumerateDaysInYear(yearOnlySimpleDateFormat.format(dateValue));
        } catch (ParseException e) {
        }

        throw new RuntimeException("Wrong date format. Expected: " + yearMonthDaySimpleDateFormat.toPattern() + " or " + yearMonthSimpleDateFormat.toPattern() + " or " + yearSimpleDateFormat.toPattern());
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

        try {
            yearMonthDaySimpleDateFormat.parse(value);
            return DAY_LEVEL;
        } catch (ParseException e) {
        }

        try {
            yearMonthSimpleDateFormat.parse(value);
            return MONTH_LEVEL;
        } catch (ParseException e) {
        }

        try {
            yearSimpleDateFormat.parse(value);
            return YEAR_LEVEL;
        } catch (ParseException e) {
        }

        throw new RuntimeException("Wrong date format. Expected: " + yearMonthDaySimpleDateFormat.toPattern() + " or " + yearMonthSimpleDateFormat.toPattern() + " or " + yearSimpleDateFormat.toPattern());
    }

    @Override
    public String getTopTerm() {
        return TOP_TERM;
    }

    @Override
    public String encode(String value, int level, boolean randomizeOnFail) {
        Date date;

        switch (level) {
            case TOP_LEVEL:
                return getTopTerm();

            case YEAR_LEVEL:
                try {
                    date = yearMonthDaySimpleDateFormat.parse(value);
                    return yearSimpleDateFormat.format(date);
                } catch (ParseException e) {
                }
                try {
                    date = yearMonthSimpleDateFormat.parse(value);
                    return yearSimpleDateFormat.format(date);
                } catch (ParseException e) {
                }
                try {
                    date = yearSimpleDateFormat.parse(value);
                    return yearSimpleDateFormat.format(date);
                } catch (ParseException e) {
                }
                if (randomizeOnFail)
                    return getTopTerm();
                throw new RuntimeException("Wrong date format, or wrong encoding level " + level + " for given value");

            case MONTH_LEVEL:
                try {
                    date = yearMonthDaySimpleDateFormat.parse(value);
                    return yearMonthSimpleDateFormat.format(date);
                } catch (ParseException e) {
                }
                try {
                    date = yearMonthSimpleDateFormat.parse(value);
                    return yearMonthSimpleDateFormat.format(date);
                } catch (ParseException e) {
                }
                if (randomizeOnFail)
                    return getTopTerm();
                throw new RuntimeException("Wrong date format, or wrong encoding level " + level + " for given value");

            case DAY_LEVEL:
                try {
                    date = yearMonthDaySimpleDateFormat.parse(value);
                    return yearMonthDaySimpleDateFormat.format(date);
                } catch (ParseException e) {
                }
                if (randomizeOnFail)
                    return getTopTerm();
                throw new RuntimeException("Wrong date format, or wrong encoding level " + level + " for given value");

            default:
                if (randomizeOnFail) {
                    return getTopTerm();
                }
                throw new RuntimeException("Illegal encoding level " + level);
        }
    }
}
