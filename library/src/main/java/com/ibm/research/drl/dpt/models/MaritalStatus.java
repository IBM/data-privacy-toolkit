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

/**
 * Model representing a marital status value with an optional category and country code.
 */
public class MaritalStatus implements LocalizedEntity {
    /** The marital status name. */
    private final String name;
    /** The broad category (e.g. "Coupled" or "Alone"). */
    private final String category;
    /** The country code this name belongs to. */
    private final String nameCountryCode;

    /**
     * Returns the category of this marital status (e.g. "Coupled" vs "Alone").
     *
     * @return the category string
     */
    public String getCategory() {
        return category;
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
     * Constructs a MaritalStatus.
     *
     * @param name            the status name
     * @param category        the category
     * @param nameCountryCode the country code
     */
    public MaritalStatus(String name, String category, String nameCountryCode) {
        this.name = name;
        this.category = category;
        this.nameCountryCode = nameCountryCode;
    }
}
