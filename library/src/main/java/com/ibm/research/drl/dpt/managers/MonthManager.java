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

    public static MonthManager getInstance() {
        return MONTH_MANAGER;
    }

    private MonthManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.MONTH);
    }

    @Override
    protected List<Tuple<String, Month>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        String key = name.toUpperCase();

        Month month = new Month(name, countryCode);

        return List.of(new Tuple<>(key, month));
    }

    @Override
    public Collection<Month> getItemList() {
        return getValues();
    }
}
