/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class County implements LocalizedEntity {
    private final String name;
    private final String nameCountryCode;
    private final String shortName;
    private final String state;
    private final Integer population;

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
     * Instantiates a new Race.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     */
    public County(String name, String nameCountryCode, String shortName, String state, Integer population) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.population = population;
        this.shortName = shortName;
        this.state = state;
    }
}
