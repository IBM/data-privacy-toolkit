/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;


public class IMSI {
    private final String mcc;
    private final String mnc;

    public IMSI(String mcc, String mnc) {
        this.mcc = mcc;
        this.mnc = mnc;
    }

    public String toString() {
        return mcc + mnc;
    }
}
