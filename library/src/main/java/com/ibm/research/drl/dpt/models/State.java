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

/** Model representing a US state with name, abbreviation, population and locale. */
public class State implements LocalizedEntity {
    /** The state name. */
    private final String name;
    /** The country code this name belongs to. */
    private final String nameCountryCode;
    /** The state population. */
    private final Long population;
    /** The two-letter state abbreviation. */
    private final String abbreviation;
    /** The name format for this state. */
    private final StateNameFormat nameFormat;

    @Override
    public String toString() {
        if (nameFormat == StateNameFormat.ABBREVIATION) {
            return abbreviation;
        }

        return name;
    }

    /**
     * Returns a string representation of this state using the given name format.
     *
     * @param nameFormat the desired name format
     * @return the full name or abbreviation of the state
     */
    public String toString(StateNameFormat nameFormat) {
        if (nameFormat == StateNameFormat.ABBREVIATION) {
            return abbreviation;
        }

        return name;
    }

    /**
     * Returns the name format associated with this state instance.
     *
     * @return the name format
     */
    public StateNameFormat getNameFormat() {
        return nameFormat;
    }

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
     * Constructs a State.
     *
     * @param name            the full state name
     * @param nameCountryCode the locale country code
     * @param abbreviation    the two-letter state abbreviation
     * @param population      the state population
     * @param nameFormat      the default name format
     */
    public State(String name, String nameCountryCode, String abbreviation, Long population, StateNameFormat nameFormat) {
        this.name = name;
        this.nameCountryCode = nameCountryCode;
        this.population = population;
        this.abbreviation = abbreviation;
        this.nameFormat = nameFormat;
    }
}
