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

import com.ibm.research.drl.dpt.models.ZIPCode;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

public class ZIPCodeManager extends ResourceBasedManager<ZIPCode> implements Serializable {

    private Map<String, Map<String, ZIPCode>> zipCodeMapThreeDigits;
    private final int prefixDigits;

    public ZIPCodeManager(int prefix) {
        super();
        this.prefixDigits = prefix;
        buildPrefixMap(getResources());
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.ZIPCODE);
    }


    private void addToPrefixMap(String code, String countryCode, Integer population) {
        Map<String, ZIPCode> map = zipCodeMapThreeDigits.get(countryCode);

        if (map == null) {
            zipCodeMapThreeDigits.put(countryCode, new HashMap<>());
            map = zipCodeMapThreeDigits.get(countryCode);
        }

        String key = code.toUpperCase();
        if (key.length() > this.prefixDigits) {
            key = key.substring(0, this.prefixDigits);
        }

        ZIPCode zipCode = map.get(key);
        if (zipCode == null) {
            map.put(key, new ZIPCode(key, population));
        } else {
            map.put(key, new ZIPCode(key, population + zipCode.getPopulation()));
        }
    }

    private void buildPrefixMap(Collection<ResourceEntry> entries) {
        Map<String, Map<String, ZIPCode>> codes = new HashMap<>();

        for (ResourceEntry entry : entries) {
            InputStream inputStream = entry.createStream();
            String countryCode = entry.getCountryCode();

            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String code = line.get(0);
                    String populationString = line.get(1);

                    Integer population = Integer.valueOf(populationString);
                    ZIPCode zipCode = new ZIPCode(code, population);

                    String key = code.toUpperCase();
                    addToPrefixMap(code, countryCode, population);
                }
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected List<Tuple<String, ZIPCode>> parseResourceRecord(CSVRecord line, String countryCode) {
        String code = line.get(0);
        String populationString = line.get(1);

        Integer population = Integer.valueOf(populationString);
        ZIPCode zipCode = new ZIPCode(code, population);

        String key = code.toUpperCase();

        return List.of(new Tuple<>(key, zipCode));
    }


    public void init() {
        this.zipCodeMapThreeDigits = new HashMap<String, Map<String, ZIPCode>>();
    }


    public Integer getPopulation(String countryCode, String key) {
        ZIPCode zipCode = getKey(countryCode, key);

        if (zipCode == null) {
            return 0;
        }

        return zipCode.getPopulation();
    }

    public Integer getPopulationByPrefix(String countryCode, String zipCode) {
        if (zipCode.length() < this.prefixDigits) {
            return 0;
        }

        Map<String, ZIPCode> map = zipCodeMapThreeDigits.get(countryCode.toLowerCase());
        if (map == null) {
            return 0;
        }

        String prefix = zipCode.substring(0, this.prefixDigits);

        ZIPCode results = map.get(prefix);
        if (results == null) {
            return 0;
        }

        Integer population = results.getPopulation();
        return population;
    }

    @Override
    public Collection<ZIPCode> getItemList() {
        return getValues();
    }
}
