/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.AnimalSpecies;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AnimalSpeciesManager extends ResourceBasedManager<AnimalSpecies> {
    private static final long serialVersionUID = -910793653650184147L;
    private final static AnimalSpeciesManager ANIMAL_SPECIES_MANAGER = new AnimalSpeciesManager();
    public static AnimalSpeciesManager getInstance() { return ANIMAL_SPECIES_MANAGER; }
    private AnimalSpeciesManager() {super();}

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

        return Arrays.asList(new Tuple<>(key, animalSpecies));
    }

}
