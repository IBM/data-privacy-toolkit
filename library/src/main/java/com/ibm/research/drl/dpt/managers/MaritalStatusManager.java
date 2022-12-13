/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.MaritalStatus;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MaritalStatusManager extends ResourceBasedManager<MaritalStatus> {
    private final static MaritalStatusManager MARITAL_STATUS_MANAGER = new MaritalStatusManager();

    public static MaritalStatusManager getInstance() {
        return MARITAL_STATUS_MANAGER;
    }

    private MaritalStatusManager() {
        super();
    }

    private List<MaritalStatus> statusList;

    @Override
    public void init() {
        statusList = new ArrayList<>();
    }

    @Override
    public Collection<MaritalStatus> getItemList() {
        return statusList;
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.MARITAL_STATUS);
    }

    @Override
    protected List<Tuple<String, MaritalStatus>> parseResourceRecord(CSVRecord record, String countryCode) {
        String status = record.get(0);
        String category = record.get(1);
        String key = status.toUpperCase();

        MaritalStatus maritalStatus = new MaritalStatus(status, category, countryCode);

        statusList.add(maritalStatus);

        return List.of(new Tuple<>(key, maritalStatus));
    }

}
