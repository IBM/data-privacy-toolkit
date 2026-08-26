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

import java.io.Serializable;

/** Represents a US ZIP code and its associated population. */
public class ZIPCode implements Serializable {
    /**
     * Returns the ZIP code string.
     *
     * @return the ZIP code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the population associated with this ZIP code.
     *
     * @return the population count
     */
    public Integer getPopulation() {
        return population;
    }

    /** The ZIP code string. */
    private final String code;
    /** The population for this ZIP code area. */
    private final Integer population;

    /**
     * Constructs a ZIPCode.
     *
     * @param code       the ZIP code string
     * @param population the population associated with this ZIP code
     */
    public ZIPCode(String code, Integer population) {
        this.code = code;
        this.population = population;
    }
}


