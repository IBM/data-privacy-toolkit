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

import java.util.List;

public class Occupation implements LocalizedEntity {
    private final String nameCountryCode;
    private final List<String> categories;

    /**
     * Gets name country code.
     *
     * @return the name country code
     */
    public String getNameCountryCode() {
        return nameCountryCode;
    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Instantiates a new Occupation.
     *
     * @param name            the name
     * @param nameCountryCode the name country code
     * @param categories      the categories
     */
    public Occupation(String name, String nameCountryCode, List<String> categories) {
        this.categories = categories;
        this.nameCountryCode = nameCountryCode;
    }
}
