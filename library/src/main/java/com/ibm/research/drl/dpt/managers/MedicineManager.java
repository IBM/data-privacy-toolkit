/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Medicine;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MedicineManager extends ResourceBasedManager<Medicine> {

    private final static MedicineManager MEDICE_MANAGER_INSTANCE = new MedicineManager();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MedicineManager getInstance() { return MEDICE_MANAGER_INSTANCE; }

    private MedicineManager() {super();}

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.MEDICINES);
    }

    @Override
    protected List<Tuple<String, Medicine>> parseResourceRecord(CSVRecord record, String countryCode) {
        String name = record.get(0);
        String key = name.toUpperCase();
        Medicine medicine = new Medicine(name, countryCode);

        return Arrays.asList(new Tuple<>(key, medicine));
    }

    @Override
    public Collection<Medicine> getItemList() {
        return getValues();
    }
}
