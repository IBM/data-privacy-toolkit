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

package com.ibm.research.drl.dpt.anonymization.differentialprivacy;

import com.ibm.research.drl.dpt.anonymization.AnonymizationAlgorithmOptions;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Configuration options for differential privacy mechanisms. */
public class DifferentialPrivacyMechanismOptions implements AnonymizationAlgorithmOptions {
    private final Map<String, String> values = new HashMap<>();
    private boolean getBoundsFromData = true;
    private boolean DPPerEquivalenceClass = false;
    private boolean overrideDefaultDPPerEquivalenceClass = false;
    private DPMechanism mechanism;

    private double lowerBound = Double.NEGATIVE_INFINITY;
    private double upperBound = Double.POSITIVE_INFINITY;
    private GeneralizationHierarchy hierarchy;
    private Tuple<String, String> binaryValues;
    private double epsilon;
    private boolean autodetectBinaryValues;
    private boolean autodetectBounds;
    
    /**
     * Sets the two binary values for the binary mechanism.
     *
     * @param v1 the first binary value
     * @param v2 the second binary value
     */
    public void setBinaryValues(String v1, String v2) {
        this.binaryValues = new Tuple<>(v1, v2);
    }

    /**
     * Returns the binary values.
     *
     * @return a tuple containing the two binary values
     */
    public Tuple<String, String> getBinaryValues() {
        return this.binaryValues;
    }

    /**
     * Returns the privacy budget epsilon.
     *
     * @return the epsilon value
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * Sets the privacy budget epsilon.
     *
     * @param epsilon the epsilon value
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public int getIntValue(String optionName) {
        return Integer.parseInt(values.get(optionName));
    }

    @Override
    public String getStringValue(String optionName) {
        return values.get(optionName);
    }

    /** Sets the flag to derive bounds from data. */
    public void getBoundsFromData() {
        this.getBoundsFromData = true;
    }

    /**
     * Returns whether bounds should be derived from data.
     *
     * @return true if bounds are derived from data
     */
    public boolean isGetBoundsFromData() { return this.getBoundsFromData; }

    /**
     * Returns the lower and upper bounds as a two-element list.
     *
     * @return list containing lower bound then upper bound
     */
    public List<Double> getBounds() {
        List<Double> bounds = new ArrayList<>();
        bounds.add(this.lowerBound);
        bounds.add(this.upperBound);

        return bounds;
    }

    /**
     * Sets the lower and upper bounds for the noise range.
     *
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     */
    public void setBounds(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Sets the generalization hierarchy for the categorical mechanism.
     *
     * @param hierarchy the generalization hierarchy
     */
    public void setHierarchy(GeneralizationHierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    /**
     * Returns the generalization hierarchy.
     *
     * @return the generalization hierarchy
     */
    public GeneralizationHierarchy getHierarchy() {
        return this.hierarchy;
    }

    /**
     * Sets whether differential privacy should be applied per equivalence class.
     *
     * @param DPPerEquivalenceClass true to apply DP per equivalence class
     */
    public void DPPerEquivalenceClass(boolean DPPerEquivalenceClass) {
        this.overrideDefaultDPPerEquivalenceClass = true;
        this.DPPerEquivalenceClass = DPPerEquivalenceClass;
    }

    /**
     * Returns whether DP should be applied per equivalence class.
     *
     * @param defaultDPPerEquivalenceClass the default value if not overridden
     * @return true if DP should be applied per equivalence class
     */
    public boolean isDPPerEquivalenceClass(boolean defaultDPPerEquivalenceClass) {
        if (this.overrideDefaultDPPerEquivalenceClass) {
            return this.DPPerEquivalenceClass;
        } else {
            return defaultDPPerEquivalenceClass;
        }

    }

    /**
     * Returns whether binary values should be auto-detected.
     *
     * @return true if binary values are auto-detected
     */
    public boolean isAutodetectBinaryValues() {
        return autodetectBinaryValues;
    }

    /**
     * Sets whether binary values should be auto-detected.
     *
     * @param autodetectBinaryValues true to auto-detect binary values
     */
    public void setAutodetectBinaryValues(boolean autodetectBinaryValues) {
        this.autodetectBinaryValues = autodetectBinaryValues;
    }

    /**
     * Returns whether bounds should be auto-detected.
     *
     * @return true if bounds are auto-detected
     */
    public boolean isAutodetectBounds() {
        return autodetectBounds;
    }

    /**
     * Sets whether bounds should be auto-detected.
     *
     * @param autodetectBounds true to auto-detect bounds
     */
    public void setAutodetectBounds(boolean autodetectBounds) {
        this.autodetectBounds = autodetectBounds;
    }

    /** Constructs a DifferentialPrivacyMechanismOptions with default settings. */
    public DifferentialPrivacyMechanismOptions() {
        
    }

    /**
     * Constructs a DifferentialPrivacyMechanismOptions with the given mechanism.
     *
     * @param mechanism the DP mechanism to use
     */
    public DifferentialPrivacyMechanismOptions(DPMechanism mechanism) {
        this.mechanism = mechanism;
    }

    /**
     * Returns the DP mechanism.
     *
     * @return the DP mechanism
     */
    public DPMechanism getMechanism() {
        return this.mechanism;
    }

}

