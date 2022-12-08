/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class SWIFTCode {
    private final String code;
    private final Country country;

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Instantiates a new Swift code.
     *
     * @param code    the code
     * @param country the country
     */
    public SWIFTCode(String code, Country country) {
        this.code = code;
        this.country = country;
    }

}
