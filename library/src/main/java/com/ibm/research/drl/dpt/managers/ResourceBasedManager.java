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

import com.ibm.research.drl.dpt.models.LocalizedEntity;
import com.ibm.research.drl.dpt.models.ProbabilisticEntity;
import com.ibm.research.drl.dpt.util.HashUtils;
import com.ibm.research.drl.dpt.util.MapWithRandomPick;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Base class for managers that load localization resources from CSV files.
 *
 * @param <K> the type of value stored in this manager
 */
public abstract class ResourceBasedManager<K> extends AbstractManager<K> {
    private static final String allCountriesName = "__all__";
    /** Map from country code to the resource lookup map for that country. */
    private final Map<String, MapWithRandomPick<String, K>> resourceMap;
    /** Map from country code to the ordered list of keys for that country. */
    private final Map<String, List<String>> listMap;
    /** Map from country code to the probability-weighted pairs for that country. */
    private final Map<String, List<Pair<String, Double>>> probMap;
    /** Map from country code to the enumerated distribution for random selection. */
    private final Map<String, EnumeratedDistribution<String>> probDistMap;
    /** The minimum key length seen across loaded resources. */
    protected int minimumLength;
    /** The maximum key length seen across loaded resources. */
    protected int maximumLength;

    /**
     * Gets all countries name.
     *
     * @return the all countries name
     */
    protected final String getAllCountriesName() {
        return ResourceBasedManager.allCountriesName;
    }

    /**
     * Add to map by locale.
     *
     * @param perLocaleMap the per locale map
     * @param countryCode  the country code
     * @param key          the key
     * @param value        the value
     */
    protected void addToMapByLocale(Map<String, Map<String, K>> perLocaleMap, String countryCode, String key, K value) {
        Map<String, K> localMap = perLocaleMap.get(countryCode);

        if (localMap == null) {
            perLocaleMap.put(countryCode, new HashMap<>());
            listMap.put(countryCode, new ArrayList<>());
            probMap.put(countryCode, new ArrayList<>());
            localMap = perLocaleMap.get(countryCode);
        }

        localMap.put(key, value);
        listMap.get(countryCode).add(key);

        if (value instanceof ProbabilisticEntity pe) {
            probMap.get(countryCode).add(new Pair<>(key, pe.getProbability()));
        }
    }

    /**
     * Gets resource filenames.
     *
     * @return the resource filenames
     */
    protected abstract Collection<ResourceEntry> getResources();

    /**
     * Parses a single CSV record into one or more key-value pairs for the resource map.
     *
     * @param record      the CSV record to parse
     * @param countryCode the country code for the resource file being parsed
     * @return a list of key-value tuples to add to the resource map
     */
    protected abstract List<Tuple<String, K>> parseResourceRecord(CSVRecord record, String countryCode);


    /**
     * Returns whether this manager's resources apply to all countries only (no per-country mapping).
     *
     * @return true if resources apply globally only
     */
    protected boolean appliesToAllCountriesOnly() {
        return false;
    }

    /**
     * Reads resources from the given entries and returns a map from country code to value map.
     *
     * @param entries the resource entries to load
     * @return a map from country code to a map of uppercased key to value
     */
    protected Map<String, Map<String, K>> readResources(Collection<ResourceEntry> entries) {
        Map<String, Map<String, K>> resources = new HashMap<>();

        this.minimumLength = Integer.MAX_VALUE;
        this.maximumLength = Integer.MIN_VALUE;

        for (ResourceEntry entry : entries) {
            try (InputStream inputStream = entry.createStream();
                 CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                String countryCode = entry.getCountryCode();

                for (CSVRecord line : reader) {
                    List<Tuple<String, K>> keyValuePairs = parseResourceRecord(line, countryCode);

                    for (Tuple<String, K> keyValue : keyValuePairs) {
                        String key = keyValue.getFirst();
                        K value = keyValue.getSecond();

                        if (!appliesToAllCountriesOnly()) {
                            addToMapByLocale(resources, countryCode, key, value);
                        }

                        addToMapByLocale(resources, getAllCountriesName(), key, value);

                        this.minimumLength = Math.min(minimumLength, key.length());
                        this.maximumLength = Math.max(maximumLength, key.length());
                    }
                }

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        return resources;
    }

    /**
     * Init.
     */
    protected void init() {
    }

    /**
     * Post init.
     */
    protected void postInit() {
    }

    /**
     * Instantiates a new Resource based manager.
     */
    public ResourceBasedManager() {
        init();

        this.resourceMap = new HashMap<>();
        this.listMap = new HashMap<>();
        this.probMap = new HashMap<>();
        this.probDistMap = new HashMap<>();

        Map<String, Map<String, K>> contents = readResources(getResources());

        for (final Map.Entry<String, Map<String, K>> entry : contents.entrySet()) {
            final String key = entry.getKey();
            final Map<String, K> value = entry.getValue();
            MapWithRandomPick<String, K> mapWithRandomPick = new MapWithRandomPick<>(value);
            this.resourceMap.put(key, mapWithRandomPick);
            this.resourceMap.get(key).setKeyList();
        }

        for (String key : this.probMap.keySet()) {
            List<Pair<String, Double>> pmf = probMap.get(key);

            if (!pmf.isEmpty()) {
                this.probDistMap.put(key, new EnumeratedDistribution<>(probMap.get(key)));
            }
        }

        postInit();
    }

    /**
     * Gets values.
     *
     * @return the values
     */
    public Collection<K> getValues() {
        return getValues(allCountriesName);
    }

    /**
     * Gets values.
     *
     * @param countryCode the country code
     * @return the values
     */
    public Collection<K> getValues(String countryCode) {
        MapWithRandomPick<String, K> map = resourceMap.get(countryCode);
        if (map != null) {
            return map.getMap().values();
        }

        return getValues(allCountriesName);
    }

    /**
     * Returns all keys across all locales.
     *
     * @return list of all keys
     */
    public List<String> getKeys() {
        return listMap.get(allCountriesName);
    }

    /**
     * Returns keys for the given locale, falling back to all-locales if not found.
     *
     * @param countryCode the locale country code
     * @return list of keys for the locale, or global keys if locale not found
     */
    public List<String> getKeys(String countryCode) {
        List<String> list = listMap.get(countryCode);
        if (list != null) {
            return list;
        }

        return listMap.get(allCountriesName);
    }

    private String getPseudorandomElement(List<String> keys, String key) {
        long hash = Math.abs(HashUtils.longFromHash(key));

        if (keys == null || keys.size() == 0) {
            return Long.toString(hash);
        }

        int position = (int) (hash % keys.size());
        return keys.get(position);
    }

    /**
     * Returns a pseudorandom value deterministically derived from the given identifier.
     *
     * @param identifier the seed identifier
     * @return a pseudorandom string value
     */
    public String getPseudorandom(String identifier) {
        String key = identifier.toUpperCase();
        K value = getKey(key);

        if (value == null) {
            return getPseudorandomElement(getKeys(), key);
        } else {
            if (value instanceof LocalizedEntity le) {
                return getPseudorandomElement(getKeys(le.getNameCountryCode()), key);
            }

            return getPseudorandomElement(getKeys(), key);
        }
    }

    @Override
    public String getRandomKey() {
        return getRandomKey(allCountriesName);
    }

    /**
     * Returns a random value from the global (all-locales) pool.
     *
     * @return a random value
     */
    public K getRandomValue() {
        return resourceMap.get(allCountriesName).getRandomValue();
    }

    /**
     * Returns a random value for the given locale.
     *
     * @param countryCode the locale country code
     * @return a random value for the locale
     */
    public K getRandomValue(String countryCode) {
        return resourceMap.get(countryCode).getRandomValue();
    }

    /**
     * Gets random key.
     *
     * @param countryCode the country code
     * @return the random key
     */
    public String getRandomKey(String countryCode) {
        MapWithRandomPick<String, K> map = resourceMap.get(countryCode);
        if (map != null) {
            return map.getRandomKey();
        }

        return null;
    }

    @Override
    public boolean isValidKey(String key) {
        MapWithRandomPick<String, K> map = resourceMap.get(allCountriesName);
        return map != null && map.getMap().containsKey(key.toUpperCase());
    }

    /**
     * Returns {@code true} if the given key is valid for the specified locale.
     *
     * @param countryCode the locale country code
     * @param key         the key to check
     * @return {@code true} if the key exists in the locale's resource map
     */
    public boolean isValidKey(String countryCode, String key) {
        MapWithRandomPick<String, K> map = resourceMap.get(countryCode.toLowerCase());

        return map != null && map.getMap().containsKey(key.toUpperCase());
    }

    /**
     * Gets key.
     *
     * @param key the key
     * @return the key
     */
    public K getKey(String key) {
        MapWithRandomPick<String, K> map = resourceMap.get(allCountriesName);

        if (map != null) {
            return map.getMap().get(key.toUpperCase());
        }

        return null;
    }

    /**
     * Returns the value for the given key in the specified locale.
     *
     * @param countryCode the locale country code
     * @param key         the lookup key
     * @return the value, or {@code null} if not found
     */
    public K getKey(String countryCode, String key) {
        MapWithRandomPick<String, K> map = resourceMap.get(countryCode.toLowerCase());

        if (map != null) {
            return map.getMap().get(key.toUpperCase());
        }

        return null;
    }

    /**
     * Returns a random key selected according to the global probability distribution.
     *
     * @return a randomly selected key
     */
    public String getRandomProbabilityBased() {
        return getRandomProbabilityBased(allCountriesName);
    }

    /**
     * Returns a random key selected according to the probability distribution for the given locale.
     *
     * @param countryCode the locale country code
     * @return a randomly selected key for the locale
     */
    public String getRandomProbabilityBased(String countryCode) {
        EnumeratedDistribution<String> distribution = this.probDistMap.get(countryCode);
        if (distribution == null) {
            return getRandomKey(countryCode);
        }

        return distribution.sample();
    }

    @Override
    public int getMaximumLength() {
        return this.maximumLength;
    }

    @Override
    public int getMinimumLength() {
        return this.minimumLength;
    }
}
