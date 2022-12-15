/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class Address {

    private boolean isPoBox;
    private String poBoxNumber;
    private String number;
    private String name;
    private String roadType;
    private String cityOrState;
    private String country;
    private String postalCode;

    /**
     * Instantiates a new Address.
     */
    public Address() {
    }

    /**
     * Instantiates a new Address.
     *
     * @param number      the number
     * @param name        the name
     * @param roadType    the road type
     * @param cityOrState the city or state
     * @param country     the country
     * @param postalCode  the postal code
     */
    public Address(String number, String name, String roadType, String cityOrState, String country, String postalCode) {
        this.number = number;
        this.name = name;
        this.roadType = roadType;
        this.cityOrState = cityOrState;
        this.country = country;
        this.postalCode = postalCode;
        this.isPoBox = false;
    }


    /**
     * Instantiates a new Address.
     *
     * @param other the other
     */
    public Address(Address other) {
        this.isPoBox = other.isPOBox();
        if (this.isPoBox) {
            this.poBoxNumber = other.getPoBoxNumber();
            return;
        }

        this.number = other.getNumber();
        this.name = other.getName();
        this.roadType = other.getRoadType();
        this.cityOrState = other.getCityOrState();
        this.country = other.getCountry();
        this.postalCode = other.getPostalCode();
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
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets number.
     *
     * @param number the number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets road type.
     *
     * @return the road type
     */
    public String getRoadType() {
        return roadType;
    }

    /**
     * Sets road type.
     *
     * @param roadType the road type
     */
    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets city or state.
     *
     * @return the city or state
     */
    public String getCityOrState() {
        return cityOrState;
    }

    /**
     * Sets city or state.
     *
     * @param cityOrState the city or state
     */
    public void setCityOrState(String cityOrState) {
        this.cityOrState = cityOrState;
    }

    /**
     * Is po box boolean.
     *
     * @return the boolean
     */
    public boolean isPOBox() {
        return isPoBox;
    }

    /**
     * Gets po box number.
     *
     * @return the po box number
     */
    public String getPoBoxNumber() {
        return poBoxNumber;
    }

    /**
     * Sets po box number.
     *
     * @param poBoxNumber the po box number
     */
    public void setPoBoxNumber(String poBoxNumber) {
        this.poBoxNumber = poBoxNumber;
    }

    /**
     * Sets po box.
     *
     * @param poBox the po box
     */
    public void setPoBox(boolean poBox) {
        isPoBox = poBox;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isPoBox) {
            builder.append("PO BOX ");
            builder.append(poBoxNumber);
            return builder.toString();
        }

        builder.append(number);
        builder.append(' ');
        builder.append(name);
        builder.append(' ');
        builder.append(roadType);
        builder.append(", ");
        builder.append(cityOrState);
        builder.append(' ');
        builder.append(postalCode);
        builder.append(", ");
        builder.append(country);
        return builder.toString();
    }
}
