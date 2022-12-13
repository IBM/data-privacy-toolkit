/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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

public abstract class ResourceBasedManager<K> extends AbstractManager<K> {
    private static final String allCountriesName = "__all__";
    private final Map<String, MapWithRandomPick<String, K>> resourceMap;
    private final Map<String, List<String>> listMap;
    private final Map<String, List<Pair<String, Double>>> probMap;
    private final Map<String, EnumeratedDistribution<String>> probDistMap;
    protected int minimumLength;
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

        if (value instanceof ProbabilisticEntity) {
            double probability = ((ProbabilisticEntity) value).getProbability();
            probMap.get(countryCode).add(new Pair<>(key, probability));
        }
    }

    /**
     * Gets resource filenames.
     *
     * @return the resource filenames
     */
    protected abstract Collection<ResourceEntry> getResources();

    protected abstract List<Tuple<String, K>> parseResourceRecord(CSVRecord record, String countryCode);


    protected boolean appliesToAllCountriesOnly() {
        return false;
    }

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

    public List<String> getKeys() {
        return listMap.get(allCountriesName);
    }

    public List<String> getKeys(String countryCode) {
        List<String> list = listMap.get(countryCode);
        if (list != null) {
            return list;
        }

        return listMap.get(allCountriesName);
    }

    private String getPseudorandomElement(List<String> keys, String key) {
        Long hash = Math.abs(HashUtils.longFromHash(key, "SHA-1"));

        if (keys == null || keys.size() == 0) {
            return hash.toString();
        }

        int position = (int) (hash % keys.size());
        return keys.get(position);
    }

    public String getPseudorandom(String identifier) {
        String key = identifier.toUpperCase();
        K value = getKey(key);

        if (value == null) {
            return getPseudorandomElement(getKeys(), key);
        } else {
            if (value instanceof LocalizedEntity) {
                String countryCode = ((LocalizedEntity) value).getNameCountryCode();
                return getPseudorandomElement(getKeys(countryCode), key);
            }

            return getPseudorandomElement(getKeys(), key);
        }
    }

    public String getRandomKey() {
        return getRandomKey(allCountriesName);
    }

    public K getRandomValue() {
        return resourceMap.get(allCountriesName).getRandomValue();
    }

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

    public boolean isValidKey(String key) {
        MapWithRandomPick<String, K> map = resourceMap.get(allCountriesName);
        return map != null && map.getMap().containsKey(key.toUpperCase());
    }

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

    public K getKey(String countryCode, String key) {
        MapWithRandomPick<String, K> map = resourceMap.get(countryCode.toLowerCase());

        if (map != null) {
            return map.getMap().get(key.toUpperCase());
        }

        return null;
    }

    public String getRandomProbabilityBased() {
        return getRandomProbabilityBased(allCountriesName);
    }

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
