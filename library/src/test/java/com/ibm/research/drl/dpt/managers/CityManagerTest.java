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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityManagerTest {

    @Test
    public void testCityNeighbors() {
        CityManager manager = CityManager.getInstance();
        System.out.println("++++ " + manager.getClosestCity("Boston", 1));
        List<String> expected = Arrays.asList("Boston", "South Boston", "Worcester", "Providence", "Springfield");
        List<String> found = manager.getKey("Boston").getNeighbors().stream().limit(5).map(City::getName).collect(Collectors.toList());
        System.out.println("++++ Found: " + found);
        System.out.println("++++ Expected: " + expected);
        assertEquals(expected, found);
    }
}
