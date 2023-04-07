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

import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.util.KDTree;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class CityManager extends ResourceBasedManager<City> {

    private static final class CityDistanceFinder implements Comparable<CityDistanceFinder> {
        private final City city;
        private final double distance;

        CityDistanceFinder(KDTree.CartesianPoint centroid, City city, KDTree.CartesianPoint cityPoint) {
            this.city = city;
            this.distance = centroid.euclideanDistance(cityPoint);
        }

        public City getCity() {
            return city;
        }

        @Override
        public int compareTo(CityDistanceFinder o) {
            return Double.compare(distance, o.distance);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CityDistanceFinder that = (CityDistanceFinder) o;
            return Double.compare(that.distance, distance) == 0 && Objects.equals(city, that.city);
        }

        @Override
        public int hashCode() {
            return Objects.hash(city, distance);
        }
    }

    private static final class CityDistanceComparator implements Comparator<City> {

        private final City centroid;

        CityDistanceComparator(City centroid) {
            this.centroid = centroid;
        }

        @Override
        public int compare(City c1, City c2) {
            double dist1 = cityDistance(centroid, c1);
            double dist2 = cityDistance(centroid, c2);

            return Double.compare(dist1, dist2);
        }

        private static double cityDistance(City city1, City city2) {
            return Math.sqrt(
                    Math.pow((city1.getLocation().getLatitude() - city2.getLocation().getLatitude()), 2) +
                            Math.pow((city1.getLocation().getLongitude() - city2.getLocation().getLongitude()), 2));
        }
    }

    private static final SecureRandom random = new SecureRandom();

    private Map<String, List<City>> cityListMap;

    private static final CityManager instance = new CityManager();

    public static CityManager getInstance() {
        return instance;
    }

    private CityManager() {
        super();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.CITY);
    }

    @Override
    protected List<Tuple<String, City>> parseResourceRecord(CSVRecord line, String locale) {
        String name = line.get(0);
        Double latitude = Double.parseDouble(line.get(2));
        Double longitude = Double.parseDouble(line.get(3));
        String countryCode = line.get(4);
        City city = new City(name, latitude, longitude, countryCode, locale);

        addToCityList(city, locale);

        return Collections.singletonList(new Tuple<>(name.toUpperCase(), city));
    }

    private void addToCityList(City city, String countryCode) {
        List<City> list = cityListMap.get(countryCode);

        if (list == null) {
            list = new ArrayList<>();
            list.add(city);
            cityListMap.put(countryCode, list);
        } else {
            list.add(city);
        }
    }

    @Override
    public void init() {
        this.cityListMap = new HashMap<>();
    }

    @Override
    public void postInit() {
        precomputeNearest1();
    }

    // NOTE: benchmarked before fix for cartesian. In repeat testing, consistently faster than Nearest v2.
    // Benchmark times: 6022, 5810, 6256
    private void precomputeNearest1() {
        for (String key : cityListMap.keySet()) {
            final List<City> cityList = cityListMap.get(key);
            final List<KDTree.CartesianPoint> cityListPoints = cityList.stream().map(
                    (c) -> new KDTree.CartesianPoint(c.getLocation().getLatitude(), c.getLocation().getLongitude())).collect(Collectors.toList());

            for (int i = 0; i < cityList.size(); i++) {
                final City city = cityList.get(i);
                final KDTree.CartesianPoint cityPoint = cityListPoints.get(i);

                final List<CityDistanceFinder> otherCities = new ArrayList<>(cityList.size());
                for (int j = 0; j < cityList.size(); j++) {
                    // Original includes self as well as other cities... not filtering self
                    otherCities.add(new CityDistanceFinder(cityPoint, cityList.get(j), cityListPoints.get(j)));
                }
                Collections.sort(otherCities);

                city.setNeighbors(otherCities.stream().map(CityDistanceFinder::getCity).collect(Collectors.toList()));
            }
        }
    }

    // Benchmark times: 7165, 6566, 6634
    // NOTE - incorrect behavior: not using cartesian coordinate conversion
    private void precomputeNearest2() {
        for (String key : cityListMap.keySet()) {
            final List<City> cityList = cityListMap.get(key);

            for (City city : cityList) {
                final List<City> otherCities = new ArrayList<>(cityList);
                otherCities.sort(new CityDistanceComparator(city));
                city.setNeighbors(otherCities);
            }
        }
    }

    /**
     * Gets the closest city.
     *
     * @param city the city
     * @param k    the k
     * @return the closest city
     */
    public String getClosestCity(String city, int k) {
        String key = city.toUpperCase();
        City lookup = getKey(key);

        if (lookup == null) {
            return getRandomKey();
        }

        List<City> neighbors = lookup.getNeighbors();
        if (neighbors == null) {
            return getRandomKey(lookup.getNameCountryCode());
        }

        if (k > neighbors.size()) {
            k = neighbors.size();
        }

        return (neighbors.get(random.nextInt(k))).getName();
    }

    @Override
    public Collection<City> getItemList() {
        return getValues();
    }
}
