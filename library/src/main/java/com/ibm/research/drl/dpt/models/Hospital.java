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

public class Hospital implements LocalizedEntity {
    private final String name;
    private final String countryCode;

    /**
     * Gets country code.
     *
     * @return the country code
     */
    @Override
    public String getNameCountryCode() {
        return countryCode;
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
     * Instantiates a new Hospital.
     *
     * @param name        the name
     * @param countryCode the country code
     */
    public Hospital(String name, String countryCode) {
        this.name = name;
        this.countryCode = countryCode;
    }
}
