/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StreetNameManager extends ResourceBasedManager<String> {
    private static final Collection<ResourceEntry> resourceStreetNameList =
            LocalizationManager.getInstance().getResources(Resource.STREET_NAMES);

    private final static StreetNameManager STREET_NAME_MANAGER = new StreetNameManager();

    public static StreetNameManager getInstance() {
        return STREET_NAME_MANAGER;
    }

    private StreetNameManager() {
        super();
    }


    @Override
    protected Collection<ResourceEntry> getResources() {
        return resourceStreetNameList;
    }

    @Override
    protected List<Tuple<String, String>> parseResourceRecord(CSVRecord line, String countryCode) {
        String sname = line.get(0).trim();
        String key = sname.toUpperCase();

        return List.of(new Tuple<>(key, sname));
    }

    @Override
    public Collection<String> getItemList() {
        return getValues();
    }
}
