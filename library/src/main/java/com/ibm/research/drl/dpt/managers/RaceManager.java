/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Race;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RaceManager extends ResourceBasedManager<Race> {
    private final static RaceManager RACE_MANAGER = new RaceManager();
    public static RaceManager getInstance() { return RACE_MANAGER; }
    private RaceManager() {super();}
    
    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.RACE_ETHNICITY);
    }

    @Override
    protected List<Tuple<String, Race>> parseResourceRecord(CSVRecord record, String countryCode) {
        String name = record.get(0);
        double probability = Double.parseDouble(record.get(1));

        String key = name.toUpperCase();
        Race race = new Race(name, countryCode, probability);
        
        return Arrays.asList(new Tuple<>(key, race));
    }

    @Override
    public Collection<Race> getItemList() {
        return getValues();
    }
}
