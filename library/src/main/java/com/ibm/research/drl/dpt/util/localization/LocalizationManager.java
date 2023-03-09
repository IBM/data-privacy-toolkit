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
package com.ibm.research.drl.dpt.util.localization;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class LocalizationManager {
    private static final String COMMON = "_common";

    private static final Logger logger = LogManager.getLogger(LocalizationManager.class);

    private final Collection<String> enabledCountries = new HashSet<>();

    private final Map<Resource, Map<String, ResourceEntry>> registeredResources;
    private final Map<String, String> countryCommonMap;

    private final static LocalizationManager instance = new LocalizationManager();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static LocalizationManager getInstance() {
        return instance;
    }


    /**
     * Instantiates a new Localization manager.
     */
    private LocalizationManager() {
        this.registeredResources = new HashMap<>();
        this.countryCommonMap = new HashMap<>();

        try (InputStream is = getClass().getResourceAsStream("/localization.properties")) {
            if (null != is) {
                Properties properties = new Properties();
                properties.load(is);

                // load enabledCountries
                for (final String country : properties.getProperty("country").split(",")) {
                    registerCountryCode(country);
                }

                // common map
                for (final String country : enabledCountries) {
                    logger.debug("Verifying mapping for {}", country);
                    final String language = properties.getProperty(country);

                    if (null != language) {
                        logger.info("Adding mapping for {}, {}", country, language);
                        countryCommonMap.put(country, language);
                    }
                }

                for (final Resource resource : Resource.values()) {
                    logger.debug("Adding localization for {}", resource);
                    // initialize resources
                    for (final String country : enabledCountries) {
                        final String path = properties.getProperty(country + '.' + resource.name());
                        if (null != path) {
                            logger.debug("Creating resources for country {}", country);
                            registerResource(resource, new ResourceEntry(path, country, ResourceEntryType.INTERNAL_RESOURCE));
                        }
                    }

                    for (final String country : new HashSet<>(countryCommonMap.values())) {
                        final String path = properties.getProperty(country + '.' + resource.name());
                        if (null != path) {
                            registerResource(resource, new ResourceEntry(path, country, ResourceEntryType.INTERNAL_RESOURCE));
                        }
                    }

                    final String path = properties.getProperty(COMMON + '.' + resource.name());
                    if (null != path) {
                        registerResource(resource, new ResourceEntry(path, COMMON, ResourceEntryType.INTERNAL_RESOURCE));
                    }

                }
            }

            logger.info("Loaded {} countries, {} languages, {} resources", enabledCountries.size(), countryCommonMap.size(), registeredResources.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerResource(Resource resource, ResourceEntry entry) {
        final String countryCode = entry.getCountryCode();

        if (!enabledCountries.contains(countryCode) && !countryCommonMap.containsValue(countryCode) && !COMMON.equals(countryCode))
            throw new IllegalArgumentException(countryCode + " is not a known locale");

        Map<String, ResourceEntry> entries = this.registeredResources.computeIfAbsent(resource, k -> new HashMap<>());

        entries.put(countryCode, entry);
    }

    /**
     * Register resource boolean.
     *
     * @param resource    the resource
     * @param countryCode the country code
     * @param filename    the filename
     * @return the boolean
     */
    public synchronized void registerResource(Resource resource, String countryCode, String filename) {
        registerResource(resource, new ResourceEntry(filename, countryCode, ResourceEntryType.EXTERNAL_FILENAME));
    }

    /**
     * Gets resources.
     *
     * @param resource  the resource
     * @param countries the countries
     * @return the resources
     */
    public synchronized Collection<ResourceEntry> getResources(Resource resource, Collection<String> countries) {
        logger.debug("Requesting {} for {}", resource, Arrays.toString(countries.toArray()));

        final List<ResourceEntry> entries = new ArrayList<>();
        final Set<String> countriesLoaded = new HashSet<>();

        final Map<String, ResourceEntry> knownEntries = registeredResources.get(resource);

        if (null == knownEntries) return entries;

        switch (resource) {
            case IMSI:
            case ATC_CODES:
            case TACDB:
            case PUBLIC_SUFFIX_LIST:
                return Collections.singletonList(knownEntries.get(COMMON));
            default:
                logger.warn("Unexpected value: {}", resource);
        }

        for (String country : countries) {
            logger.debug("Retrieving country {}", country);

            if (!knownEntries.containsKey(country)) {
                final String mapped = countryCommonMap.get(country);

                if (mapped != null) {
                    country = mapped;
                } else {
                    continue;
                }
            }

            if (countriesLoaded.contains(country)) {
                continue;
            }
            final ResourceEntry entry = knownEntries.get(country);

            if (null == entry) {
                continue;
            }
            entries.add(entry);
            countriesLoaded.add(country);
        }

        return entries;
    }

    /**
     * Gets resources.
     *
     * @param resource the resource
     * @return the resources
     */
    public synchronized Collection<ResourceEntry> getResources(Resource resource) {
        return getResources(resource, enabledCountries);
    }

    public synchronized void registerCountryCode(String country) {
        enabledCountries.add(country.trim());
    }
}
