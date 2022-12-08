/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class Race implements LocalizedEntity, ProbabilisticEntity {
    private final String name;
    private final String nameCountryCode;
    private final double probability;

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
    public Race(String name, String nameCountryCode, double probability) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.probability = probability;
    }

    @Override
    public double getProbability() {
        return this.probability;
    }
}
