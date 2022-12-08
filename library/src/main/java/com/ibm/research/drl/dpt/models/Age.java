/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class Age {

    private final AgePortion yearPortion;
    private final AgePortion monthPortion;
    private final AgePortion weeksPortion;
    private final AgePortion daysPortion;

    public Age(AgePortion yearPortion, AgePortion monthPortion, AgePortion weeksPortion, AgePortion daysPortion) {
        this.daysPortion = daysPortion;
        this.weeksPortion = weeksPortion;
        this.monthPortion = monthPortion;
        this.yearPortion = yearPortion;
    }

    public AgePortion getYearPortion() {
        return yearPortion;
    }

    public AgePortion getMonthPortion() {
        return monthPortion;
    }

    public AgePortion getWeeksPortion() {
        return weeksPortion;
    }

    public AgePortion getDaysPortion() {
        return daysPortion;
    }
}
