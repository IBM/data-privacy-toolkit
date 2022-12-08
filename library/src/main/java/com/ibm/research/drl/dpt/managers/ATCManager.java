/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.ATC;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ATCManager extends ResourceBasedManager<ATC> {
    private final static ATCManager ATC_MANAGER = new ATCManager();
    
    public static ATCManager getInstance() {return  ATC_MANAGER;}
    
    private ATCManager() {super();}
    
    private List<ATC> codeList;

    @Override
    public void init() {
        codeList = new ArrayList<>();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.ATC_CODES);
    }

    @Override
    protected boolean appliesToAllCountriesOnly() {
        return true; 
    }

    @Override
    protected List<Tuple<String, ATC>> parseResourceRecord(CSVRecord record, String countryCode) {
        String code = record.get(0).strip().toUpperCase();
        ATC atc = new ATC(code);
        
        codeList.add(atc);
        
        return Arrays.asList(new Tuple<>(code, atc));
    }

    @Override
    public Collection<ATC> getItemList() {
        return codeList;
    }
}
