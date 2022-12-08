/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class FirstName implements LocalizedEntity {
    private final String name;
    private final Gender gender;
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
     * Gets gender.
     *
     * @return the gender
     */
    public Gender getGender() {
        return this.gender;
    }

    /**
     * Instantiates a new First name.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     * @param gender          the gender
     */
    public FirstName(String name, String nameCountryCode, Gender gender) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.gender = gender;
    }
}
