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
