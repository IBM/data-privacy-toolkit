/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class Medicine implements LocalizedEntity {
    private final String name;
    private final String nameCountryCode;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    public String getNameCountryCode() {
        return nameCountryCode;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Instantiates a new Medicine.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     */
    public Medicine(String name, String nameCountryCode) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
    }
}

