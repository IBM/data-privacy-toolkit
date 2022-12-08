/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class Hospital implements LocalizedEntity {
    private final String name;
    private final String countryCode;

    /**
     * Gets country code.
     *
     * @return the country code
     */
    public String getNameCountryCode() {
        return countryCode;
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
     * Instantiates a new Hospital.
     *
     * @param name        the name
     * @param countryCode the country code
     */
    public Hospital(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
    }
}
