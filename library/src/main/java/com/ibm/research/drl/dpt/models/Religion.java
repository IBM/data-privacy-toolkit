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
package com.ibm.research.drl.dpt.models;

/** Model representing a religion name with localization and probability support. */
public class Religion implements LocalizedEntity, ProbabilisticEntity {
    /** The religion name. */
    private final String name;
    /** The broad group this religion belongs to. */
    private final String group;
    /** The country code this name belongs to. */
    private final String nameCountryCode;
    /** The probability weight. */
    private final double probability;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    @Override
    public String getNameCountryCode() {
        return nameCountryCode;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Constructs a Religion.
     *
     * @param name            the religion name
     * @param group           the religion group
     * @param nameCountryCode the locale country code
     * @param probability     the probability of the religion in the locale
     */
    public Religion(String name, String group, String nameCountryCode, double probability) {
        this.name = name;
        this.group = group;
        this.nameCountryCode = nameCountryCode;
        this.probability = probability;
    }

    /**
     * Returns the religion group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    @Override
    public double getProbability() {
        return this.probability;
    }
}

