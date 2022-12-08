/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class CreditCardType {
    private final String value;

    public String getValue() {
        return value;
    }

    public CreditCardType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
