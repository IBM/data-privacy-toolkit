/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

import java.util.List;

public class Occupation implements LocalizedEntity {
    private final String nameCountryCode;
    private final List<String> categories;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    public String getNameCountryCode() {
        return nameCountryCode;
    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Instantiates a new Occupation.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     * @param categories      the categories
     */
    public Occupation(String name, String nameCountryCode, List<String> categories) {
        this.categories = categories;
        this.nameCountryCode = nameCountryCode;
    }
}
