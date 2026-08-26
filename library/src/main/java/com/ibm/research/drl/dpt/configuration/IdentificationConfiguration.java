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
package com.ibm.research.drl.dpt.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/** Configuration parameters controlling the type-identification process. */
public class IdentificationConfiguration implements Serializable {
    /**
     * The constant DEFAULT.
     */
    public static final IdentificationConfiguration DEFAULT = new IdentificationConfiguration(
            1,
            .5,
            false,
            IdentificationStrategy.FREQUENCY_BASED,
            Collections.emptyMap(),
            Collections.emptyMap()
    );

    /** Default minimum frequency threshold for identification. */
    private final double defaultFrequencyThreshold;
    /** Default identifier priority. */
    private final int defaultPriority;
    /** Whether empty values contribute to frequency counts. */
    private final boolean considerEmptyForFrequency;

    /** The strategy used to combine identifier results. */
    private final IdentificationStrategy identificationStrategy;

    /** Per-identifier priority overrides. */
    private final Map<String, Integer> priorities;
    /** Per-identifier frequency-threshold overrides. */
    private final Map<String, Double> frequencyThresholds;


    /**
     * Constructs an IdentificationConfiguration.
     *
     * @param defaultPriority            the default identifier priority
     * @param defaultFrequencyThreshold  the default frequency threshold (0–100)
     * @param considerEmptyForFrequency  whether empty values are counted in frequency
     * @param identificationStrategy     the identification strategy to use
     * @param priorities                 per-type priority overrides
     * @param frequencyThresholds        per-type frequency-threshold overrides
     */
    @JsonCreator
    public IdentificationConfiguration(
            @JsonProperty("defaultPriority") int defaultPriority,
            @JsonProperty("defaultFrequencyThreshold") double defaultFrequencyThreshold,
            @JsonProperty("considerEmptyForFrequency") boolean considerEmptyForFrequency,
            @JsonProperty("identificationStrategy") IdentificationStrategy identificationStrategy,
            @JsonProperty("priorities") Map<String, Integer> priorities,
            @JsonProperty("frequencyThresholds") Map<String, Double> frequencyThresholds

    ) {
        this.defaultPriority = defaultPriority;
        this.defaultFrequencyThreshold = defaultFrequencyThreshold;
        this.considerEmptyForFrequency = considerEmptyForFrequency;

        this.identificationStrategy = identificationStrategy;
        this.priorities = priorities;
        this.frequencyThresholds = frequencyThresholds;
    }

    /**
     * Returns the identification strategy.
     *
     * @return the identification strategy
     */
    public IdentificationStrategy getIdentificationStrategy() {
        return identificationStrategy;
    }

    /**
     * Gets confidence threshold for type.
     *
     * @param typeName the type name
     * @return the confidence threshold for type
     */
    public synchronized double getFrequencyThresholdForType(String typeName) {
        return this.frequencyThresholds.getOrDefault(typeName, defaultFrequencyThreshold);
    }

    /**
     * Gets priority for type.
     *
     * @param typeName the type name
     * @return the priority for type
     */
    public synchronized int getPriorityForType(String typeName) {
        return this.priorities.getOrDefault(typeName, this.defaultPriority);
    }

    /**
     * Gets consider empty for confidence.
     *
     * @return to consider empty for confidence
     */
    public boolean getConsiderEmptyForFrequency() {
        return considerEmptyForFrequency;
    }
}
