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

import com.ibm.research.drl.dpt.models.Occupation;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OccupationManager extends ResourceBasedManager<Occupation> {

    private static final OccupationManager OCCUPATION_MANAGER = new OccupationManager();

    public static OccupationManager getInstance() {
        return OCCUPATION_MANAGER;
    }

    private OccupationManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.OCCUPATION);
    }

    @Override
    protected List<Tuple<String, Occupation>> parseResourceRecord(CSVRecord line, String countryCode) {
        String occupationName = line.get(0);
        List<String> categories = new ArrayList<>();
        for (int i = 1; i < line.size(); i++) {
            String category = line.get(i);
            categories.add(category);
        }

        Occupation occupation = new Occupation(occupationName, countryCode, categories);
        String key = occupationName.toUpperCase();

        return List.of(new Tuple<>(key, occupation));
    }

    @Override
    public Collection<Occupation> getItemList() {
        return getValues();
    }
}
