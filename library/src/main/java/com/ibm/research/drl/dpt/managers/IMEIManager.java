/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.IMEI;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IMEIManager extends ResourceBasedManager<IMEI> {
    private final static IMEIManager IMEI_MANAGER_INSTANCE = new IMEIManager();

    public static IMEIManager getInstance() {
        return IMEI_MANAGER_INSTANCE;
    }

    private IMEIManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.TACDB);
    }

    @Override
    protected List<Tuple<String, IMEI>> parseResourceRecord(CSVRecord line, String countryCode) {
        String tac = uniformTACValues(line.get(0).trim());

        IMEI imei = new IMEI(tac);

        return Collections.singletonList(new Tuple<>(tac.toUpperCase(), imei));
    }

    private String uniformTACValues(String tac) {
        if (tac.length() >= 8) return tac.substring(0, 8);

        StringBuilder builder = new StringBuilder();
        for (int i = 8 - tac.length(); i > 0; --i) {
            builder.append('0');
        }

        builder.append(tac);

        return builder.toString();
    }

    @Override
    public Collection<IMEI> getItemList() {
        return getValues();
    }
}
