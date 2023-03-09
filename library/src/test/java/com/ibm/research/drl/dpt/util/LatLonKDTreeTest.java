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

import com.ibm.research.drl.dpt.models.City;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LatLonKDTreeTest {

    @Test
    public void testLatLonKDTree() throws Exception {
        List<City> locationList = new ArrayList<>();

        City c1 = new City("IBM Campus", 53.4184439, -6.4165875, "IE", "en");
        City c2 = new City("The Mayne, Clonee", 53.422235, -6.426072, "IE", "en");
        City c3 = new City("Damastown Industrial Park", 53.414601, -6.412983, "IE", "en");
        City c4 = new City("Carlton Hotel Tyrrelstown", 53.419480, -6.379337, "IE", "en");

        locationList.add(c1);
        locationList.add(c2);
        locationList.add(c3);
        locationList.add(c4);

        LatLonKDTree<City> tree = new LatLonKDTree<>(locationList);

        double[] key = {53.416686, -6.416673, 0};
        List<City> neighbors = tree.findNearestK(key, 2);

        assertThat(neighbors.size(), is(2));

        neighbors.sort(Comparator.comparing(City::getName));

        assertEquals("Damastown Industrial Park", neighbors.get(0).getName());
        assertEquals("IBM Campus", neighbors.get(1).getName());
    }
}
