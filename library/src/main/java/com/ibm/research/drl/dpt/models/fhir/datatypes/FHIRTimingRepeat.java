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


/** FHIRTimingRepeat FHIR datatype. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRTimingRepeat {
    /** Constructs a FHIRTimingRepeat. */
    public FHIRTimingRepeat() {}


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

    /**
     * Returns the frequency.
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency.
     * @param frequency the frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Returns the boundsQuantity.
     * @return the boundsQuantity
     */
    public FHIRQuantity getBoundsQuantity() {
        return boundsQuantity;
    }

    /**
     * Sets the boundsQuantity.
     * @param boundsQuantity the boundsQuantity
     */
    public void setBoundsQuantity(FHIRQuantity boundsQuantity) {
        this.boundsQuantity = boundsQuantity;
    }

    /**
     * Returns the boundsRange.
     * @return the boundsRange
     */
    public FHIRRange getBoundsRange() {
        return boundsRange;
    }

    /**
     * Sets the boundsRange.
     * @param boundsRange the boundsRange
     */
    public void setBoundsRange(FHIRRange boundsRange) {
        this.boundsRange = boundsRange;
    }

    /**
     * Returns the boundsPeriod.
     * @return the boundsPeriod
     */
    public FHIRPeriod getBoundsPeriod() {
        return boundsPeriod;
    }

    /**
     * Sets the boundsPeriod.
     * @param boundsPeriod the boundsPeriod
     */
    public void setBoundsPeriod(FHIRPeriod boundsPeriod) {
        this.boundsPeriod = boundsPeriod;
    }

    /**
     * Returns the count.
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the count.
     * @param count the count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Returns the duration.
     * @return the duration
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Sets the duration.
     * @param duration the duration
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Returns the durationMax.
     * @return the durationMax
     */
    public float getDurationMax() {
        return durationMax;
    }

    /**
     * Sets the durationMax.
     * @param durationMax the durationMax
     */
    public void setDurationMax(float durationMax) {
        this.durationMax = durationMax;
    }

    /**
     * Returns the durationUnits.
     * @return the durationUnits
     */
    public String getDurationUnits() {
        return durationUnits;
    }

    /**
     * Sets the durationUnits.
     * @param durationUnits the durationUnits
     */
    public void setDurationUnits(String durationUnits) {
        this.durationUnits = durationUnits;
    }

    /**
     * Returns the frequencyMax.
     * @return the frequencyMax
     */
    public int getFrequencyMax() {
        return frequencyMax;
    }

    /**
     * Sets the frequencyMax.
     * @param frequencyMax the frequencyMax
     */
    public void setFrequencyMax(int frequencyMax) {
        this.frequencyMax = frequencyMax;
    }

    /**
     * Returns the period.
     * @return the period
     */
    public float getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * @param period the period
     */
    public void setPeriod(float period) {
        this.period = period;
    }

    /**
     * Returns the periodMax.
     * @return the periodMax
     */
    public float getPeriodMax() {
        return periodMax;
    }

    /**
     * Sets the periodMax.
     * @param periodMax the periodMax
     */
    public void setPeriodMax(float periodMax) {
        this.periodMax = periodMax;
    }

    /**
     * Returns the periodUnits.
     * @return the periodUnits
     */
    public String getPeriodUnits() {
        return periodUnits;
    }

    /**
     * Sets the periodUnits.
     * @param periodUnits the periodUnits
     */
    public void setPeriodUnits(String periodUnits) {
        this.periodUnits = periodUnits;
    }

    /**
     * Returns the when.
     * @return the when
     */
    public String getWhen() {
        return when;
    }

    /**
     * Sets the when.
     * @param when the when
     */
    public void setWhen(String when) {
        this.when = when;
    }

}
