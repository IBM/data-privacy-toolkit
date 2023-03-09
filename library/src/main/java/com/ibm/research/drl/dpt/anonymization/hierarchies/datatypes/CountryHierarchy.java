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
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.Country;

import java.util.Collection;

public class CountryHierarchy extends MaterializedHierarchy {

    private final static CountryManager countryManager = CountryManager.getInstance();

    private final static CountryHierarchy instance = new CountryHierarchy();

    public static CountryHierarchy getInstance() {
        return instance;
    }

    private CountryHierarchy() {
        Collection<Country> countries = countryManager.getItemList();
        for (Country country : countries) {
            String[] terms = new String[3];
            terms[0] = country.getName();
            terms[1] = country.getContinent();
            terms[2] = "*";
            add(terms);
        }
    }
}

