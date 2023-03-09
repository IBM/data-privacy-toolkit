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
    public LatLonKDTree(List<T> locations) throws Exception {
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
