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

import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.models.Location;
import com.ibm.research.drl.dpt.models.PostalCode;
import com.ibm.research.drl.dpt.util.HashUtils;
import com.ibm.research.drl.dpt.util.LatLonKDTree;
import com.ibm.research.drl.dpt.util.MapWithRandomPick;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.*;

public class PostalCodeManager implements Manager {
    private static final Collection<ResourceEntry> resourceList =
            LocalizationManager.getInstance().getResources(Resource.POSTAL_CODES);
    private final static int maxNeighborsLimit = 200;
    private static final PostalCodeManager instance = new PostalCodeManager();
    private final MapWithRandomPick<String, PostalCode> postalCodeMap;
    private final SecureRandom random;
    private final Map<String, List<PostalCode>> neighborsMap;
    private final List<PostalCode> postalCodeList;
    private LatLonKDTree<PostalCode> latLonTree = null;

    private PostalCodeManager() {
        this.random = new SecureRandom();
        this.postalCodeList = new ArrayList<>();

        this.postalCodeMap = new MapWithRandomPick<>(new HashMap<String, PostalCode>());
        this.postalCodeMap.getMap().putAll(readPostalCodeCodeList(resourceList));
        this.postalCodeMap.setKeyList();

        try {
            this.latLonTree = new LatLonKDTree<PostalCode>(postalCodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.neighborsMap = precomputeNeighbors();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PostalCodeManager getInstance() {
        return instance;
    }

    private Map<String, List<PostalCode>> precomputeNeighbors() {
        Map<String, List<PostalCode>> neighborsMap = new HashMap<>();

        int numberOfPostalCodes = postalCodeList.size();

        for (Location location : postalCodeList) {
            PostalCode postalCode = (PostalCode) location;
            String name = postalCode.getName();
            neighborsMap.put(name, getClosestPostalCodes(name, maxNeighborsLimit));
        }

        return neighborsMap;
    }

    private Map<? extends String, ? extends PostalCode> readPostalCodeCodeList(Collection<ResourceEntry> entries) {
        Map<String, PostalCode> postalCodes = new HashMap<>();

        for (ResourceEntry entry : entries) {
            InputStream inputStream = entry.createStream();
            String locale = entry.getCountryCode();

            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String countryName = line.get(0);
                    String code = line.get(1);
                    Double latitude = Double.parseDouble(line.get(2));
                    Double longitude = Double.parseDouble(line.get(3));
                    /* TODO : replace hardcoded locale */
                    PostalCode postalCode = new PostalCode(code, latitude, longitude);
                    this.postalCodeList.add(postalCode);
                    postalCodes.put(code.toUpperCase(), postalCode);
                }
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }

        return postalCodes;
    }

    public String getPseudorandom(String identifier) {
        int position = (int) (Math.abs(HashUtils.longFromHash(identifier)) % this.postalCodeList.size());
        return this.postalCodeList.get(position).getName();
    }

    /**
     * Gets closest postal codes.
     *
     * @param postalCode the postal code
     * @param k          the k
     * @return the closest postal codes
     */
    @SuppressWarnings("unchecked")
    public List<PostalCode> getClosestPostalCodes(String postalCode, int k) {
        String key = postalCode.toUpperCase();
        PostalCode lookup = this.postalCodeMap.getMap().get(key);

        if (lookup == null) {
            return new ArrayList<>();
        }

        LatitudeLongitude latitudeLongitude = lookup.getLocation();
        double[] latLonKey = new double[]{latitudeLongitude.getLatitude(), latitudeLongitude.getLongitude(), 0};

        return this.latLonTree.findNearestK(latLonKey, k);
    }

    /**
     * Gets closest postal code.
     *
     * @param postalCode the postal code
     * @param k          the k
     * @return the closest postal code
     */
    public String getClosestPostalCode(String postalCode, int k) {
        String key = postalCode.toUpperCase();
        List<PostalCode> neighbors = this.neighborsMap.get(key);

        if (neighbors == null) {
            return this.postalCodeMap.getRandomKey();
        }

        if (k > maxNeighborsLimit) {
            k = maxNeighborsLimit;
        }

        return neighbors.get(random.nextInt(k)).getName();
    }

    @Override
    public String getRandomKey() {
        return this.postalCodeMap.getRandomKey();
    }

    @Override
    public boolean isValidKey(String postalCode) {
        return postalCodeMap.getMap().containsKey(postalCode.toUpperCase());
    }
}
