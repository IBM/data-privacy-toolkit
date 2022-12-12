/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

public interface InformationMetricOptions {
    /**
     * Gets int value.
     *
     * @param key the key
     * @return the int value
     */
    int getIntValue(String key);

    /**
     * Gets string value.
     *
     * @param key the key
     * @return the string value
     */
    String getStringValue(String key);

    /**
     * Gets boolean value.
     *
     * @param key the key
     * @return the boolean value
     */
    boolean getBooleanValue(String key);
}
