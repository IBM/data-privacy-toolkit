/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Hospital;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HospitalManager extends ResourceBasedManager<Hospital> {
    private static final HospitalManager HOSPITAL_MANAGER = new HospitalManager();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static HospitalManager getInstance() {
        return HOSPITAL_MANAGER;
    }

    private HospitalManager() { super(); }

    @Override
    public void init() {}

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.HOSPITAL_NAMES);
    }

    @Override
    protected List<Tuple<String, Hospital>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0).trim();
        Hospital hospital = new Hospital(name, countryCode);

        return Arrays.asList(new Tuple<>(name.toUpperCase(), hospital));
    }

    @Override
    public Collection<Hospital> getItemList() {
        return getValues();
    }
}
