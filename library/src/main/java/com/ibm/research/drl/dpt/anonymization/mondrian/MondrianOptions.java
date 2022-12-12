/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.mondrian;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MondrianOptions implements AnonymizationAlgorithmOptions {
    private final Map<String, String> values = new HashMap<>();
    private final CategoricalSplitStrategy categoricalSplitStrategy;

    public CategoricalSplitStrategy getCategoricalSplitStrategy() {
        return categoricalSplitStrategy;
    }

    @Override
    public int getIntValue(String optionName) {
        return Integer.parseInt(values.get(optionName));
    }

    @Override
    public String getStringValue(String optionName) {
        return values.get(optionName);
    }

    /**
     * Instantiates a new Mondrian options.
     */
    public MondrianOptions() {
        this.categoricalSplitStrategy = CategoricalSplitStrategy.ORDER_BASED;
    }
    
    public MondrianOptions(CategoricalSplitStrategy categoricalSplitStrategy) {
        this.categoricalSplitStrategy = categoricalSplitStrategy;
    }
}

