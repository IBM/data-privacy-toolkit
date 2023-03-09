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
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.models.Country;

import java.util.Collection;

public class CityHierarchy extends MaterializedHierarchy {
    private final static CityManager cityManager = CityManager.getInstance();
    private final static CountryManager countryManager = CountryManager.getInstance();

    private static final CityHierarchy instance = new CityHierarchy();

    public static CityHierarchy getInstance() {
        return instance;
    }

    private CityHierarchy() {
        super();

        Collection<City> cities = cityManager.getItemList();
        for (final City city : cities) {
            String[] terms = new String[4];
            terms[0] = city.getName();
            /* TODO: fix the model to include Country object */
            String countryCode = city.getCountryCode();
            Country country = countryManager.lookupCountry(countryCode, city.getNameCountryCode());
            String continent;
            if (country != null) {
                terms[1] = country.getName();
                continent = country.getContinent();
            } else {
                terms[1] = "N/A";
                continent = "N/A";
            }

            terms[2] = continent;
            terms[3] = "*";
            add(terms);
        }
    }
}
