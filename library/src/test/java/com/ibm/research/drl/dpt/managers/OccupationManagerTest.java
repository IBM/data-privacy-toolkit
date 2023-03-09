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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OccupationManagerTest {

    @Test
    public void testLookup() {
        OccupationManager occupationManager = OccupationManager.getInstance();

        String occupation = "actor";
        assertTrue(occupationManager.isValidKey(occupation));
        assertTrue(occupationManager.isValidKey(occupation.toLowerCase()));
        assertTrue(occupationManager.isValidKey(occupation.toUpperCase()));
        occupation = "acTor";
        assertTrue(occupationManager.isValidKey(occupation));

        occupation = "official";
        assertFalse(occupationManager.isValidKey(occupation));
    }

    @Test
    public void testFalsePositives() {
        String[] values = {
               "C",
               "Z",
               "S",
               "P",
               "N",
               "G",
               "O",
               "-"
        };

        OccupationManager occupationManager = OccupationManager.getInstance();
        for(String value: values) {
            assertThat(occupationManager.isValidKey(value), is(false));
        }

    }
}
