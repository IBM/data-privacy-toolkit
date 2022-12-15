/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class PhoneNumber {

    private final String prefix;
    private final String countryCode;
    private String areaCode;
    private final String separator;
    private final String number;

    /**
     * Is has prefix boolean.
     *
     * @return the boolean
     */
    public boolean isHasPrefix() {
        return hasPrefix;
    }

    private final boolean hasPrefix;

    /**
     * Gets country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Gets prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets separator.
     *
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Gets number.
     *
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * Gets area code.
     *
     * @return the area code
     */
    public String getAreaCode() {
        return this.areaCode;
    }

    /**
     * Sets area code.
     *
     * @param areaCode the area code
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Instantiates a new Phone number.
     *
     * @param prefix      the prefix
     * @param countryCode the country code
     * @param separator   the separator
     * @param number      the number
     * @param hasPrefix   the has prefix
     */
    public PhoneNumber(String prefix, String countryCode, String separator,
                       String number, boolean hasPrefix) {
        this(prefix, countryCode, separator, number, "", hasPrefix);
    }

    /**
     * Instantiates a new Phone number.
     *
     * @param prefix      the prefix
     * @param countryCode the country code
     * @param separator   the separator
     * @param number      the number
     * @param areaCode    the area code
     * @param hasPrefix   the has prefix
     */
    public PhoneNumber(String prefix, String countryCode, String separator,
                       String number, String areaCode, boolean hasPrefix) {
        this.prefix = prefix;
        this.countryCode = countryCode;
        this.separator = separator;
        this.number = number;
        this.areaCode = areaCode;
        this.hasPrefix = hasPrefix;
    }

}

