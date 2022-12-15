/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class IMEI {
    private final String value;

    public String getValue() {
        return value;
    }

    public IMEI(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}

