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

import com.ibm.research.drl.dpt.models.State;
import com.ibm.research.drl.dpt.models.StateNameFormat;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StatesUSManager extends ResourceBasedManager<State> {
    private final static StatesUSManager STATES_US_MANAGER = new StatesUSManager();

    public static StatesUSManager getInstance() {
        return STATES_US_MANAGER;
    }

    private StatesUSManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.STATES_US);
    }

    @Override
    protected List<Tuple<String, State>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String abbreviation = line.get(1);
        Long population = Long.valueOf(line.get(5));

        String key = name.toUpperCase();
        State state = new State(name, countryCode, abbreviation, population, StateNameFormat.FULL_NAME);

        String abbrvKey = abbreviation.toUpperCase();
        State stateAbbrv = new State(name, countryCode, abbreviation, population, StateNameFormat.ABBREVIATION);

        return Arrays.asList(new Tuple<>(key, state), new Tuple<>(abbrvKey, stateAbbrv));
    }

    @Override
    public Collection<State> getItemList() {
        return getValues();
    }
}
