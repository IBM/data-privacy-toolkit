/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.models.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LatLonKDTree<T extends Location> {
    private final KDTree<LatLonCartesianPoint<T>> tree;

    public List<T> findNearestK(double[] key, int k) {
        return tree.nearestNeighbourSearch(k, new LatLonCartesianPoint<>(key)).stream().map(point -> point.location).collect(Collectors.toList());
    }

    /**
     * Instantiates a new Lat lon kd tree.
     *
     * @param locations the location list
     * @throws Exception the exception
     */
    public LatLonKDTree(List<T> locations) throws Exception{
        List<LatLonCartesianPoint<T>> points = new ArrayList<>(locations.size());

        for (final T location : locations) {
            points.add(new LatLonCartesianPoint<>(location));
        }

        this.tree = new KDTree<>(points);
    }

    private class LatLonCartesianPoint<T extends Location> extends KDTree.CartesianPoint {
        private final T location;

        public LatLonCartesianPoint(T location) {
            super(location.getLocation().getLatitude(), location.getLocation().getLongitude());
            this.location = location;
        }

        public LatLonCartesianPoint(double[] key) {
            super(Double.valueOf(key[0]), Double.valueOf(key[1]));
            this.location = null;
        }

        public T getLocation() {
            return location;
        }
    }
}
