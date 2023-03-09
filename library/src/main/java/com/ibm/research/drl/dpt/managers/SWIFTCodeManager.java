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

import com.ibm.research.drl.dpt.models.Country;
import com.ibm.research.drl.dpt.models.SWIFTCode;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.security.SecureRandom;
import java.util.*;

public class SWIFTCodeManager extends ResourceBasedManager<SWIFTCode> {
    private static final CountryManager countryManager = CountryManager.getInstance();
    private Map<String, List<SWIFTCode>> codeByCountryMap;
    private final SecureRandom random = new SecureRandom();

    private final static SWIFTCodeManager SWIFT_CODE_MANAGER = new SWIFTCodeManager();

    public static SWIFTCodeManager getInstance() {
        return SWIFT_CODE_MANAGER;
    }

    private SWIFTCodeManager() {
        super();
    }

    @Override
    public void init() {
        this.codeByCountryMap = new HashMap<>();
    }

    /**
     * Gets code from country.
     *
     * @param code the code
     * @return the code from country
     */
    public String getCodeFromCountry(String code) {
        SWIFTCode swiftCode = getKey(code);
        if (swiftCode == null) {
            return getRandomKey();
        }

        String countryCode = code.substring(4, 6);
        List<SWIFTCode> list = codeByCountryMap.get(countryCode.toUpperCase());

        SWIFTCode randomCode = list.get(random.nextInt(list.size()));
        return randomCode.getCode();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.SWIFT);
    }

    @Override
    protected List<Tuple<String, SWIFTCode>> parseResourceRecord(CSVRecord line, String locale) {
        String code = line.get(0);
        String countryCode = code.substring(4, 6);

        /* TODO: fix this hardcoded locale */
        Country country = countryManager.lookupCountry(countryCode, "en");
        if (country == null) {
            return Collections.emptyList();
        }

        SWIFTCode swiftCode = new SWIFTCode(code, country);

        String ccKey = countryCode.toUpperCase();
        if (!codeByCountryMap.containsKey(ccKey)) {
            codeByCountryMap.put(ccKey, new ArrayList<SWIFTCode>());
        }

        codeByCountryMap.get(ccKey).add(swiftCode);

        return List.of(new Tuple<>(code.toUpperCase(), swiftCode));

    }

    @Override
    public Collection<SWIFTCode> getItemList() {
        return getValues();
    }
}
