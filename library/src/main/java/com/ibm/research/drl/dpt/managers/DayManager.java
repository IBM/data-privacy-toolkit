/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Day;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DayManager extends ResourceBasedManager<Day> {
    private static final long serialVersionUID = 422211328962854977L;
    private static final DayManager DAY_MANAGER = new DayManager();

    public static DayManager getInstance() {
        return DAY_MANAGER;
    }

    private DayManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.DAY);
    }

    @Override
    protected List<Tuple<String, Day>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String key = name.toUpperCase();

        Day day = new Day(name, countryCode);

        return List.of(new Tuple<>(key, day));
    }

    @Override
    public Collection<Day> getItemList() {
        return getValues();
    }
}
