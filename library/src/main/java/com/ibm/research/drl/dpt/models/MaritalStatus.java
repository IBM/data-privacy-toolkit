/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class MaritalStatus implements LocalizedEntity {
    private final String name;
    private final String category;
    private final String nameCountryCode;

    public String getCategory() {
        return category;
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
     * Instantiates a new Marital status.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     */
    public MaritalStatus(String name, String category, String nameCountryCode) {
        this.name = name;
        this.category = category;
        this.nameCountryCode = nameCountryCode;
    }
}
