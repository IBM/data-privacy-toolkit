/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Month;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MonthManager extends ResourceBasedManager<Month> {
    private final static MonthManager MONTH_MANAGER = new MonthManager();
    public static MonthManager getInstance() { return MONTH_MANAGER; }
    private MonthManager() {super();}
    
    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.MONTH);
    }

    @Override
    protected List<Tuple<String, Month>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String key = name.toUpperCase();

        Month month = new Month(name, countryCode);
        
        return Arrays.asList(new Tuple<>(key, month));
    }

    @Override
    public Collection<Month> getItemList() {
        return getValues();
    }
}
