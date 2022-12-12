/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AECSOptions implements InformationMetricOptions {
    private final Map<String, Object> values = new HashMap<>();

    @Override
    public int getIntValue(String optionName) {
        return (int)values.get(optionName);
    }

    @Override
    public String getStringValue(String optionName) {
        return (String)values.get(optionName);
    }

    @Override
    public boolean getBooleanValue(String optionName) {
        return (boolean)values.get(optionName);
    }

    /**
     * Instantiates a new Aecs options.
     *
     * @param normalized the normalized
     */
    @JsonCreator
    public AECSOptions(@JsonProperty("normalized") boolean normalized, @JsonProperty("k") int k) {
        values.put("normalized", normalized);
        values.put("k", k);
    }
}

