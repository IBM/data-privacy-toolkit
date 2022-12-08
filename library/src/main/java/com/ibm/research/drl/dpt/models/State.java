/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class State implements LocalizedEntity {
    private final String name;
    private final String nameCountryCode;
    private final Long population;
    private final String abbreviation;
    private final StateNameFormat nameFormat;

    @Override
    public String toString() {
        if (nameFormat == StateNameFormat.ABBREVIATION) {
            return abbreviation;
        }

        return name;
    }

    public String toString(StateNameFormat nameFormat) {
        if (nameFormat == StateNameFormat.ABBREVIATION) {
            return abbreviation;
        }

        return name;
    }

    public StateNameFormat getNameFormat() {
        return nameFormat;
    }

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
     * Instantiates a new State.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     * @param abbreviation    the abbreviation
     * @param population      the population
     */
    public State(String name, String nameCountryCode, String abbreviation, Long population, StateNameFormat nameFormat) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.population = population;
        this.abbreviation = abbreviation;
        this.nameFormat = nameFormat;
    }
}
