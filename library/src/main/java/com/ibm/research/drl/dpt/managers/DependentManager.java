/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Dependent;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DependentManager extends ResourceBasedManager<Dependent> {
    private static final Collection<ResourceEntry> resourceDependentList = LocalizationManager.getInstance().getResources(Resource.DEPENDENT);
    private final static DependentManager DEPENDENT_MANAGER = new DependentManager();

    public static DependentManager getInstance() {
        return DEPENDENT_MANAGER;
    }

    private DependentManager() {
        super();
    }


    @Override
    protected Collection<ResourceEntry> getResources() {
        return resourceDependentList;
    }

    @Override
    protected List<Tuple<String, Dependent>> parseResourceRecord(CSVRecord line, String countryCode) {
        String sname = line.get(0).trim();
        String key = sname.toUpperCase();

        Dependent dependent = new Dependent(sname);
        return List.of(new Tuple<>(key, dependent));
    }

    @Override
    public Collection<Dependent> getItemList() {
        return getValues();
    }
}
