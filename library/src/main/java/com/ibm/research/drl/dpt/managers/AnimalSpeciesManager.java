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

import com.ibm.research.drl.dpt.models.AnimalSpecies;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnimalSpeciesManager extends ResourceBasedManager<AnimalSpecies> {
    private static final long serialVersionUID = -910793653650184147L;
    private final static AnimalSpeciesManager ANIMAL_SPECIES_MANAGER = new AnimalSpeciesManager();

    public static AnimalSpeciesManager getInstance() {
        return ANIMAL_SPECIES_MANAGER;
    }

    private AnimalSpeciesManager() {
        super();
    }

    private List<AnimalSpecies> statusList;

    @Override
    public void init() {
        statusList = new ArrayList<>();
    }

    @Override
    public Collection<AnimalSpecies> getItemList() {
        return statusList;
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.ANIMAL);
    }

    @Override
    protected List<Tuple<String, AnimalSpecies>> parseResourceRecord(CSVRecord record, String countryCode) {
        String status = record.get(0);
        String key = status.toUpperCase();

        AnimalSpecies animalSpecies = new AnimalSpecies(status, countryCode);

        statusList.add(animalSpecies);

        return List.of(new Tuple<>(key, animalSpecies));
    }

}
