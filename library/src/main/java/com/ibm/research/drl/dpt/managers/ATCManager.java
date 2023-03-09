/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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

    public static ATCManager getInstance() {
        return ATC_MANAGER;
    }

    private ATCManager() {
        super();
    }

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

        return List.of(new Tuple<>(code, atc));
    }

    @Override
    public Collection<ATC> getItemList() {
        return codeList;
    }
}
