/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Country;
import com.ibm.research.drl.dpt.models.Location;
import com.ibm.research.drl.dpt.util.*;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.*;

/**
 * The type Country manager.
 */
public class CountryManager extends AbstractManager<Country> {

    private static final Logger logger = LogManager.getLogger(CountryManager.class);
    private static final Collection<ResourceEntry> resourceCountryList =
            LocalizationManager.getInstance().getResources(Resource.COUNTRY);
    private final SecureRandom random;

    private final Map<String, MapWithRandomPick<String, Country>[]> countryMap;
    private final Map<String, List<Location>> countryListMap;

    private static final String ALL_COUNTRIES_NAME = "__all__";

    private static final CountryManager instance = new CountryManager();

    public static CountryManager getInstance() {
        return instance;
    }

    /**
     * Gets all countries name.
     *
     * @return the all countries name
     */
    protected final String getAllCountriesName() {
        return CountryManager.ALL_COUNTRIES_NAME;
    }

    /**
     * Instantiates a new Country manager.
     */
    private CountryManager() {
        this.random = new SecureRandom();

        this.countryMap = new HashMap<>();
        this.countryListMap = new HashMap<>();

        readCountryList(resourceCountryList);

        for (MapWithRandomPick<String, Country>[] cntMap : countryMap.values()) {
            for (MapWithRandomPick map : cntMap) {
                map.setKeyList();
            }
        }

        precomputeNearest();
    }

    private MapWithRandomPick<String, Country>[] initCountryMap() {
        int specSize = CountryNameSpecification.values().length;
        MapWithRandomPick<String, Country>[] mapWithRandomPicks = new MapWithRandomPick[specSize];

        for (int i = 0; i < mapWithRandomPicks.length; i++) {
            mapWithRandomPicks[i] = new MapWithRandomPick<>(new HashMap<String, Country>());
        }

        return mapWithRandomPicks;
    }

    private Country[] getCountriesByDistance(final Country country, List<Location> countryList) {

        Country[] countryArray = new Country[countryList.size()];
        countryArray = countryList.toArray(countryArray);

        Arrays.sort(countryArray, (o1, o2) -> {
            Double d1 = GeoUtils.latitudeLongitudeDistance(o1.getLocation(), country.getLocation());
            Double d2 = GeoUtils.latitudeLongitudeDistance(o2.getLocation(), country.getLocation());
            return d1.compareTo(d2);
        });

        return countryArray;
    }

    private void precomputeNearest() {
        for (Map.Entry<String, List<Location>> entry : countryListMap.entrySet()) {
            List<Location> countryList = entry.getValue();

            for (Location location : countryList) {
                Country country = (Country) location;
                Country[] neighbors = getCountriesByDistance(country, countryList);
                country.setNeighbors(neighbors);
            }
        }
    }

    private void addToCountryListMap(Country country, String countryCode) {
        List<Location> countryList = countryListMap.get(countryCode);

        if (countryList == null) {
            countryList = new ArrayList<>();
            countryList.add(country);
            countryListMap.put(countryCode, countryList);
        } else {
            countryList.add(country);
        }
    }

    private String getPseudorandomElement(List<Location> keys, String key) {
        long hash = Math.abs(HashUtils.longFromHash(key));

        if (keys == null || keys.isEmpty()) {
            return Long.toString(hash);
        }

        int position = (int) (hash % keys.size());
        return ((Country) (keys.get(position))).getName();
    }

    public String getPseudorandom(String identifier) {
        String key = identifier.toUpperCase();
        Country country = lookupCountry(identifier);

        if (country == null) {
            return getPseudorandomElement(this.countryListMap.get(ALL_COUNTRIES_NAME), key);
        } else {
            String countryCode = country.getNameCountryCode();
            return getPseudorandomElement(this.countryListMap.get(countryCode), key);
        }
    }


    private void addToCountryMap(Country country, String key, CountryNameSpecification spec, String locale) {
        MapWithRandomPick<String, Country>[] maps = countryMap.get(locale);

        if (maps == null) {
            maps = initCountryMap();
            maps[spec.ordinal()].getMap().put(key, country);
            countryMap.put(locale, maps);
        } else {
            maps[spec.ordinal()].getMap().put(key, country);
        }
    }

    private void readCountryList(Collection<ResourceEntry> entries) {
        for (ResourceEntry entry : entries) {
            try (InputStream inputStream = entry.createStream();
                 CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                String locale = entry.getCountryCode();
                for (CSVRecord record : reader) {
                    String countryName = record.get(0).toUpperCase();
                    String iso2Letter = record.get(1).toUpperCase();
                    String iso3Letter = record.get(2).toUpperCase();
                    String friendlyName = record.get(3).toUpperCase();
                    String continent = record.get(4);
                    Double latitude = Double.parseDouble(record.get(5));
                    Double longitude = Double.parseDouble(record.get(6));

                    /* TODO: temp fix until data is finished */
                    if (continent.equals("Unknown")) {
                        logger.info(String.format("Unknown continent for %s", countryName));
                    }

                    Country country = new Country(countryName, iso2Letter, iso3Letter, continent, latitude, longitude, locale);

                    addToCountryListMap(country, locale);
                    addToCountryListMap(country, getAllCountriesName());

                    addToCountryMap(country, countryName, CountryNameSpecification.NAME, locale);
                    addToCountryMap(country, iso2Letter, CountryNameSpecification.ISO2, locale);
                    addToCountryMap(country, iso3Letter, CountryNameSpecification.ISO3, locale);

                    addToCountryMap(country, countryName, CountryNameSpecification.NAME, getAllCountriesName());
                    addToCountryMap(country, iso2Letter, CountryNameSpecification.ISO2, getAllCountriesName());
                    addToCountryMap(country, iso3Letter, CountryNameSpecification.ISO3, getAllCountriesName());

                    if (!friendlyName.equals("")) {
                        addToCountryMap(country, friendlyName, CountryNameSpecification.NAME, locale);
                        addToCountryMap(country, friendlyName, CountryNameSpecification.NAME, getAllCountriesName());
                    }

                }
            } catch (IOException | NullPointerException e) {
                logger.error("Error loading country list", e);
            }
        }
    }

    private CountryNameSpecification getSpecificationFromName(String name) {
        String key = name.toUpperCase();
        for (CountryNameSpecification e : CountryNameSpecification.values()) {
            if (this.countryMap.get(getAllCountriesName())[e.ordinal()].getMap().containsKey(key)) {
                return e;
            }
        }

        return null;
    }

    @Override
    public String getRandomKey() {
        return getRandomKey(CountryNameSpecification.NAME);
    }

    /**
     * Gets random key.
     *
     * @param spec the spec
     * @return the random key
     */
    public String getRandomKey(CountryNameSpecification spec) {
        MapWithRandomPick<String, Country> map = getSpecificationMap(spec);
        return map.getRandomKey();
    }

    /**
     * Gets random key.
     *
     * @param spec   the spec
     * @param locale the locale
     * @return the random key
     */
    public String getRandomKey(CountryNameSpecification spec, String locale) {
        MapWithRandomPick<String, Country> map = countryMap.get(locale)[spec.ordinal()];
        return map.getRandomKey();
    }

    /**
     * Gets random key.
     *
     * @param identifier the exception country
     * @param locale     the locale
     * @return the random key
     */
    public String getRandomKey(String identifier, String locale) {
        CountryNameSpecification spec = getSpecificationFromName(identifier);
        if (spec == null) {
            spec = CountryNameSpecification.NAME;
        }

        return getRandomKey(spec, locale);
    }

    /**
     * Gets closest country.
     *
     * @param originalCountry the original country
     * @param k               the k
     * @return the closest country
     */
    public String getClosestCountry(String originalCountry, int k) {

        CountryNameSpecification spec = getSpecificationFromName(originalCountry);
        if (spec == null) {
            return getRandomKey(CountryNameSpecification.NAME);
        }

        MapWithRandomPick<String, Country> lookupMap = getSpecificationMap(spec);
        if (lookupMap == null) {
            return getRandomKey(spec);
        }

        String key = originalCountry.toUpperCase();
        Country lookupResult = lookupMap.getMap().get(key);
        if (lookupResult == null) {
            return getRandomKey(spec);
        }

        Country[] neighbors = lookupResult.getNeighbors();
        if (neighbors == null) {
            return getRandomKey(spec);
        }

        if (k > neighbors.length) {
            k = neighbors.length;
        }

        Country randomCountry = neighbors[random.nextInt(k)];
        return randomCountry.getName(spec);
    }

    private MapWithRandomPick<String, Country> getSpecificationMap(CountryNameSpecification spec) {
        return countryMap.get(getAllCountriesName())[spec.ordinal()];
    }

    /**
     * Is valid country boolean.
     *
     * @param countryName the country name
     * @param domain      the domain
     * @return the boolean
     */
    public boolean isValidCountry(String countryName, CountryNameSpecification domain) {
        String key = countryName.toUpperCase();
        MapWithRandomPick map = null;

        map = getSpecificationMap(domain);
        if (map == null) {
            return false;
        }

        return map.getMap().containsKey(key);
    }

    /**
     * Lookup country country.
     *
     * @param country the country
     * @param locale  the locale
     * @return the country
     */
    public Country lookupCountry(String country, String locale) {
        String key = country.toUpperCase();

        if (locale == null) {
            locale = getAllCountriesName();
        }

        MapWithRandomPick<String, Country>[] maps = countryMap.get(locale);
        if (maps == null) {
            return null;
        }

        for (CountryNameSpecification e : CountryNameSpecification.values()) {
            Country res = maps[e.ordinal()].getMap().get(key);
            if (res != null) {
                return res;
            }
        }

        return null;
    }

    /**
     * Lookup country country.
     *
     * @param country the country
     * @return the country
     */
    public Country lookupCountry(String country) {
        return lookupCountry(country, null);
    }

    @Override
    public boolean isValidKey(String country) {
        String key = country.toUpperCase();
        for (CountryNameSpecification e : CountryNameSpecification.values()) {
            if (countryMap.get(getAllCountriesName())[e.ordinal()].getMap().containsKey(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<Country> getItemList() {
        List<Country> list = new ArrayList<>();

        for (Location location : countryListMap.get(getAllCountriesName())) {
            list.add((Country) location);
        }

        return list;
    }
}
