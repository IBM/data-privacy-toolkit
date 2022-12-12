/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import java.io.Serializable;

public interface AnonymizationAlgorithmOptions extends Serializable {
    /**
     * Gets int value.
     *
     * @param optionName the option name
     * @return the int value
     */
    int getIntValue(String optionName);

    /**
     * Gets string value.
     *
     * @param optionName the option name
     * @return the string value
     */
    String getStringValue(String optionName);
}
