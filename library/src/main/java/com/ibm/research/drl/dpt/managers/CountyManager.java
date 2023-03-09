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
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.County;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CountyManager extends ResourceBasedManager<County> {
    private static final CountyManager COUNTY_MANAGER = new CountyManager();

    public static CountyManager getInstance() {
        return COUNTY_MANAGER;
    }

    private CountyManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.COUNTY);
    }

    @Override
    protected List<Tuple<String, County>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String shortName = line.get(1);
        String state = line.get(2);
        Integer population = Integer.valueOf(line.get(3));

        County county = new County(name, countryCode, shortName, state, population);

        String key = name.toUpperCase();
        String shortNameKey = shortName.toUpperCase();

        return Arrays.asList(new Tuple<>(key, county), new Tuple<>(shortNameKey, county));
    }

    @Override
    public Collection<County> getItemList() {
        return getValues();
    }
}
