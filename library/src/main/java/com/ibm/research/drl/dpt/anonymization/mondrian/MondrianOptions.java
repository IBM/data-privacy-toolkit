/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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

