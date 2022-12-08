/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Religion;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReligionManager extends ResourceBasedManager<Religion> {
    private final static ReligionManager RELIGION_MANAGER = new ReligionManager();
    public static ReligionManager getInstance() { return RELIGION_MANAGER; }
    private ReligionManager() {super();}

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.RELIGION);
    }

    @Override
    protected List<Tuple<String, Religion>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String group = line.get(1);
        double probability = Double.parseDouble(line.get(2));

        String key = name.toUpperCase();

        Religion religion = new Religion(name, group, countryCode, probability);

        return Arrays.asList(new Tuple<>(key, religion));
    }

    @Override
    public Collection<Religion> getItemList() {
        return getValues();
    }
}
