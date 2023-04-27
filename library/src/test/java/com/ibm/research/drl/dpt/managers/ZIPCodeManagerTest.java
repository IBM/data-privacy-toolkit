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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZIPCodeManagerTest {

    @Test
    public void test() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);
        assertTrue(zipCodeManager.isValidKey("US", "00601"));
    }

    @Test
    public void testPopulation() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        int population = zipCodeManager.getPopulation("US", "00601");
        assertEquals(18570, population);
    }

    @Test
    public void testPopulationThreeDigitPrefix() {

        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        int population = zipCodeManager.getPopulationByPrefix("US", "00601");
        assertEquals(1214568, population);
    }

    @Test
    public void testInvalidCountry() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        int population = zipCodeManager.getPopulationByPrefix("#$@", "00601");
        assertEquals(0, population);

        population = zipCodeManager.getPopulation("#$@", "00601");
        assertEquals(0, population);
    }

    @Test
    public void testInvalidCode() {
        ZIPCodeManager zipCodeManager = new ZIPCodeManager(3);

        int population = zipCodeManager.getPopulationByPrefix("US", "!@$@%%");
        assertEquals(0, population);

        population = zipCodeManager.getPopulation("US", "!#!#!@#!#!");
        assertEquals(0, population);
    }
}
