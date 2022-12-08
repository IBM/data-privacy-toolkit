/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class Religion implements LocalizedEntity, ProbabilisticEntity {
    private final String name;
    private final String group;
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
     * Instantiates a new Religion.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     */
    public Religion(String name, String group, String nameCountryCode, double probability) {
        this.name = name;
        this.group = group;
        this.nameCountryCode = nameCountryCode;
        this.probability = probability;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public double getProbability() {
        return this.probability;
    }
}

