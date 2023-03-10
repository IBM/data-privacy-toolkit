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
    
    public void setBinaryValues(String v1, String v2) {
        this.binaryValues = new Tuple<>(v1, v2);
    }

    public Tuple<String, String> getBinaryValues() {
        return this.binaryValues;
    }

    public double getEpsilon() {
        return epsilon;
    }

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

    public void getBoundsFromData() {
        this.getBoundsFromData = true;
    }

    public boolean isGetBoundsFromData() { return this.getBoundsFromData; }

    public List<Double> getBounds() {
        List<Double> bounds = new ArrayList<>();
        bounds.add(this.lowerBound);
        bounds.add(this.upperBound);

        return bounds;
    }

    public void setBounds(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public void setHierarchy(GeneralizationHierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public GeneralizationHierarchy getHierarchy() {
        return this.hierarchy;
    }

    public void DPPerEquivalenceClass(boolean DPPerEquivalenceClass) {
        this.overrideDefaultDPPerEquivalenceClass = true;
        this.DPPerEquivalenceClass = DPPerEquivalenceClass;
    }

    public boolean isDPPerEquivalenceClass(boolean defaultDPPerEquivalenceClass) {
        if (this.overrideDefaultDPPerEquivalenceClass) {
            return this.DPPerEquivalenceClass;
        } else {
            return defaultDPPerEquivalenceClass;
        }

    }

    public boolean isAutodetectBinaryValues() {
        return autodetectBinaryValues;
    }

    public void setAutodetectBinaryValues(boolean autodetectBinaryValues) {
        this.autodetectBinaryValues = autodetectBinaryValues;
    }

    public boolean isAutodetectBounds() {
        return autodetectBounds;
    }

    public void setAutodetectBounds(boolean autodetectBounds) {
        this.autodetectBounds = autodetectBounds;
    }

    public DifferentialPrivacyMechanismOptions() {
        
    }

    public DifferentialPrivacyMechanismOptions(DPMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public DPMechanism getMechanism() {
        return this.mechanism;
    }

}

