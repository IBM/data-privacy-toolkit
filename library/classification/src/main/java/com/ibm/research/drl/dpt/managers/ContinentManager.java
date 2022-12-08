/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.Continent;
import com.ibm.research.drl.dpt.models.LatitudeLongitude;
import com.ibm.research.drl.dpt.models.Location;
import com.ibm.research.drl.dpt.util.LatLonKDTree;
import com.ibm.research.drl.dpt.util.Tuple;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVRecord;

import java.security.SecureRandom;
import java.util.*;

public class ContinentManager extends ResourceBasedManager<Continent> {
    private static final ContinentManager CONTINENT_MANAGER = new ContinentManager();

    public static ContinentManager getInstance() {
        return CONTINENT_MANAGER;
    }

    private Map<String, List<Continent>> continentListMap;
    private Map<String, LatLonKDTree<Continent>> latLonTree = null;

    private final SecureRandom random = new SecureRandom();

    private ContinentManager() {super();}

    public Collection<Continent> getItemList() {
        return getValues();
    }

    @Override
    protected Collection<ResourceEntry> getResources() {
        return LocalizationManager.getInstance().getResources(Resource.CONTINENT);
    }

    private void addToContinentList(Continent continent, String countryCode) {
        List<Continent> list = continentListMap.get(countryCode);

        if (list == null) {
            list = new ArrayList<>();
            list.add(continent);
            continentListMap.put(countryCode, list);
        }
        else {
            list.add(continent);
        }
    }

    @Override
    protected List<Tuple<String, Continent>> parseResourceRecord(CSVRecord line, String countryCode) {
        String name = line.get(0);
        Double latitude = Double.parseDouble(line.get(1));
        Double longitude = Double.parseDouble(line.get(2));
        Continent continent = new Continent(name, countryCode, latitude, longitude);

        addToContinentList(continent, countryCode);

        return Arrays.asList(new Tuple<>(name.toUpperCase(), continent));
    }

    @Override
    public void init() {
        continentListMap = new HashMap<>();
    }

    /**
     * Gets closest continents.
     *
     * @param location the location
     * @param k        the k
     * @return the closest continents
     */
/* TODO : move up for re-usability */
    private List<Continent> getClosestContinents(Location location, String key, int k) {
        LatitudeLongitude latlon = location.getLocation();
        double[] latlonKey = new double[]{latlon.getLatitude(), latlon.getLongitude(), 0};

        try {
            return this.latLonTree.get(key).findNearestK(latlonKey, k);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void precomputeNearest() {
        this.latLonTree = new HashMap<>();

        for(String key: continentListMap.keySet()) {
            List<Continent> continentList = continentListMap.get(key);

            int numberOfContinents = continentList.size();

            try {
                this.latLonTree.put(key, new LatLonKDTree<Continent>(continentList));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("error building KD-tree");
            }

            for (Location location : continentList) {
                Continent continent = (Continent) location;
                continent.setNeighbors(getClosestContinents(continent, key, numberOfContinents));
            }
        }
    }

    public void postInit() {
        precomputeNearest();
    }

    /**
     * Gets closest continent.
     *
     * @param identifier the identifier
     * @param k          the k
     * @return the closest continent
     */
    public String getClosestContinent(String identifier, int k) {
        Continent continent = this.getKey(identifier);
        if (continent == null) {
            return getRandomKey();
        }

        List<Continent> neighbors = continent.getNeighbors();
        if (neighbors == null) {
            return getRandomKey();
        }

        if (k > neighbors.size()) {
            k = neighbors.size();
        }

        return neighbors.get(random.nextInt(k)).getName();
    }

}
