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
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LatitudeLongitudeHierarchyTest {

    @Test
    public void testLatLonHierarchy() {
        LatitudeLongitudeHierarchy hierarchy = new LatitudeLongitudeHierarchy();

        //7 decimal places precision 
        String value = "53.3984136,-6.3862464";


        assertEquals(value, hierarchy.encode(value, 0, true));
        assertEquals(hierarchy.getTopTerm(), hierarchy.encode(value, 9, true));
        assertEquals(hierarchy.getTopTerm(), hierarchy.encode(value, 10, true));

        String level8 = hierarchy.encode(value, 8, true);
        String[] toks = level8.split(",");
        assertEquals(53.0, Double.parseDouble(toks[0]), 0.00000001);
        assertEquals(-6.0, Double.parseDouble(toks[1]), 0.00000001);

        //at level 1 we still get the same value, since we begin with precision = 8
        String level1 = hierarchy.encode(value, 1, true);
        toks = level1.split(",");
        assertEquals(53.3984136, Double.parseDouble(toks[0]), 0.00000001);
        assertEquals(-6.3862464, Double.parseDouble(toks[1]), 0.00000001);

        String level2 = hierarchy.encode(value, 2, true);
        toks = level2.split(",");
        assertEquals(53.398413, Double.parseDouble(toks[0]), 0.00000001);
        assertEquals(-6.386246, Double.parseDouble(toks[1]), 0.00000001);

        String level4 = hierarchy.encode(value, 4, true);
        toks = level4.split(",");
        assertEquals(53.3984, Double.parseDouble(toks[0]), 0.00000001);
        assertEquals(-6.3862, Double.parseDouble(toks[1]), 0.00000001);
        //assertEquals(value, ;
    }
}

