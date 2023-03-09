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

public class State implements LocalizedEntity {
    private final String name;
    private final String nameCountryCode;
    private final Long population;
    private final String abbreviation;
    private final StateNameFormat nameFormat;

    @Override
    public String toString() {
        if (nameFormat == StateNameFormat.ABBREVIATION) {
            return abbreviation;
        }

        return name;
    }

    public String toString(StateNameFormat nameFormat) {
        if (nameFormat == StateNameFormat.ABBREVIATION) {
            return abbreviation;
        }

        return name;
    }

    public StateNameFormat getNameFormat() {
        return nameFormat;
    }

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
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
     * Instantiates a new State.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     * @param abbreviation    the abbreviation
     * @param population      the population
     */
    public State(String name, String nameCountryCode, String abbreviation, Long population, StateNameFormat nameFormat) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.population = population;
        this.abbreviation = abbreviation;
        this.nameFormat = nameFormat;
    }
}
