/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.models;

public class SSNUS {
    private final String areaNumber;
    private final String group;
    private final String serialNumber;

    /**
     * Instantiates a new Ssnus.
     *
     * @param areaNumber   the area number
     * @param group        the group
     * @param serialNumber the serial number
     */
    public SSNUS(String areaNumber, String group, String serialNumber) {
        this.areaNumber = areaNumber;
        this.group = group;
        this.serialNumber = serialNumber;
    }

    /**
     * Gets area number.
     *
     * @return the area number
     */
    public String getAreaNumber() {
        return areaNumber;
    }

    /**
     * Gets group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Gets serial number.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public String toString() {
        String builder = areaNumber + "-" +
                group +
                "-" +
                serialNumber;
        return builder;
    }
}
