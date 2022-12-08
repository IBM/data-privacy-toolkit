/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

public interface Manager {

    /**
     * Is valid key boolean.
     *
     * @param identifier the identifier
     * @return the boolean
     */
    boolean isValidKey(String identifier);

    /**
     * Gets random key.
     *
     * @return the random key
     */
    String getRandomKey();

    default int getMaximumLength() {
        return Integer.MAX_VALUE;
    }

    default int getMinimumLength() {
        return 0;
    }
}
