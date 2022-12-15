/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Sex;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GenderManager extends ResourceBasedManager<Sex> {
    private static final long serialVersionUID = -8322026980948568171L;
    private final static GenderManager GENDER_MANAGER = new GenderManager();

    public static GenderManager getInstance() {
        return GENDER_MANAGER;
    }

    private GenderManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.GENDER);
    }

    @Override
    protected List<Tuple<String, Sex>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String key = name.toUpperCase();

        Sex sex = new Sex(name, countryCode);

        return List.of(new Tuple<>(key, sex));
    }

    @Override
    public Collection<Sex> getItemList() {
        return getValues();
    }
}
