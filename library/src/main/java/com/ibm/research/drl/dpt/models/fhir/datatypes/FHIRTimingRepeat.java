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
package com.ibm.research.drl.dpt.models.fhir.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRTimingRepeat {

    private FHIRQuantity boundsQuantity;
    private FHIRRange boundsRange;
    private FHIRPeriod boundsPeriod;
    private int count;
    private float duration;
    private float durationMax;
    private String durationUnits;
    private int frequency;
    private int frequencyMax;
    private float period;
    private float periodMax;
    private String periodUnits;
    private String when;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public FHIRQuantity getBoundsQuantity() {
        return boundsQuantity;
    }

    public void setBoundsQuantity(FHIRQuantity boundsQuantity) {
        this.boundsQuantity = boundsQuantity;
    }

    public FHIRRange getBoundsRange() {
        return boundsRange;
    }

    public void setBoundsRange(FHIRRange boundsRange) {
        this.boundsRange = boundsRange;
    }

    public FHIRPeriod getBoundsPeriod() {
        return boundsPeriod;
    }

    public void setBoundsPeriod(FHIRPeriod boundsPeriod) {
        this.boundsPeriod = boundsPeriod;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getDurationMax() {
        return durationMax;
    }

    public void setDurationMax(float durationMax) {
        this.durationMax = durationMax;
    }

    public String getDurationUnits() {
        return durationUnits;
    }

    public void setDurationUnits(String durationUnits) {
        this.durationUnits = durationUnits;
    }

    public int getFrequencyMax() {
        return frequencyMax;
    }

    public void setFrequencyMax(int frequencyMax) {
        this.frequencyMax = frequencyMax;
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(float period) {
        this.period = period;
    }

    public float getPeriodMax() {
        return periodMax;
    }

    public void setPeriodMax(float periodMax) {
        this.periodMax = periodMax;
    }

    public String getPeriodUnits() {
        return periodUnits;
    }

    public void setPeriodUnits(String periodUnits) {
        this.periodUnits = periodUnits;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

}
