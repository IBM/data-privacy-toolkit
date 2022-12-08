/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class AgePortion {
    private final boolean exists;
    private final int start;
    private final int end;
    private final AgePortionFormat format;

    public AgePortion(boolean exists, int start, int end, AgePortionFormat format) {
        this.end = end;
        this.start = start;
        this.exists = exists;
        this.format = format;
    }

    public boolean exists() {
        return exists;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public AgePortionFormat getFormat() {
        return format;
    }
}
