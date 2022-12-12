/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

import java.util.HashMap;
import java.util.Map;

public class OLAOptions implements AnonymizationAlgorithmOptions {
    /**
     * Gets suppression rate.
     *
     * @return the suppression rate
     */
    public double getSuppressionRate() {
        return suppressionRate;
    }

    private final double suppressionRate;

    private final Map<String, Integer> values = new HashMap<>();

    @Override
    public int getIntValue(String optionName) {
        return values.get(optionName);
    }

    @Override
    public String getStringValue(String optionName) {
        return null;
    }

    /**
     * Instantiates a new Ola options.
     *
     * @param suppressionRate
     */
    @JsonCreator
    public OLAOptions(@JsonProperty("suppressionRate") double suppressionRate) {
        this.suppressionRate = suppressionRate;
    }
}

