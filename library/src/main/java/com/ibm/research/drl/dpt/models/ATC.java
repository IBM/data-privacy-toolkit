/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class ATC {
    private final String code;

    public String getCode() {
        return code;
    }

    public ATC(String code) {
        this.code = code;
    }

    public String toString() {
        return code;
    }
}
