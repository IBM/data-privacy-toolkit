/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.util.MapWithRandomPick;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MSISDNManager {

    private static final Collection<ResourceEntry> resourceList =
            LocalizationManager.getInstance().getResources(Resource.PHONE_CALLING_CODES);
    private static final Collection<ResourceEntry> areaCodeResourceList =
            LocalizationManager.getInstance().getResources(Resource.PHONE_AREA_CODES);

    private final MapWithRandomPick<String, String> countryCodeMap;
    private final Map<String, Set<String>> areaCodeMapByCountry;

    private final static MSISDNManager MSISDN_MANAGER = new MSISDNManager();

    public static MSISDNManager getInstance() {
        return MSISDN_MANAGER;
    }

    /**
     * Instantiates a new Msisdn manager.
     */
    private MSISDNManager() {
        this.countryCodeMap = new MapWithRandomPick<>(new HashMap<String, String>());
        this.countryCodeMap.getMap().putAll(readCountryCodeList(resourceList));
        this.countryCodeMap.setKeyList();

        this.areaCodeMapByCountry = new HashMap<>();
        this.areaCodeMapByCountry.putAll(readAreaCodeList(areaCodeResourceList));
    }

    private Map<? extends String, ? extends Set<String>> readAreaCodeList(Collection<ResourceEntry> entries) {
        Map<String, Set<String>> codes = new HashMap<>();

        for (ResourceEntry entry : entries) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String country = line.get(0);
                    String areaCode = line.get(1);

                    if (!codes.containsKey(country)) {
                        codes.put(country, new HashSet<String>());
                    }

                    codes.get(country).add(areaCode);
                }
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        return codes;
    }

    /**
     * Is valid us number boolean.
     *
     * @param data the data
     * @return the boolean
     */
    public boolean isValidUSNumber(String data) {

        Set<String> areaCodeMap = areaCodeMapByCountry.get("USA");
        if (areaCodeMap == null) {
            return false;
        }

        if (data.length() == 10) {
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                if (c < '0' || c > '9') {
                    return false;
                }
            }

            String areaCode = data.substring(0, 3);
            return areaCodeMap.contains(areaCode);
        }

        return false;
    }

    private Map<? extends String, ? extends String> readCountryCodeList(Collection<ResourceEntry> entries) {
        Map<String, String> names = new HashMap<>();

        for (ResourceEntry entry : entries) {
            try (CSVParser reader = Readers.createCSVReaderFromStream(entry.createStream())) {
                for (CSVRecord line : reader) {
                    names.put(line.get(0), line.get(1));
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        return names;
    }

    /**
     * Gets random country code.
     *
     * @return the random country code
     */
    public String getRandomCountryCode() {
        return this.countryCodeMap.getRandomKey();
    }

    /**
     * Is valid country code boolean.
     *
     * @param country the country
     * @return the boolean
     */
    public boolean isValidCountryCode(String country) {
        if (country.length() > 3) {
            return false;
        }

        return countryCodeMap.getMap().containsKey(country.toUpperCase());
    }
}
