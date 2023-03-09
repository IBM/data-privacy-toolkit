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
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.generators.ItemSet;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class VerificationUtilsTest {

    @Test
    public void testBuildValueMap() throws Exception {
        try (InputStream is = this.getClass().getResourceAsStream("/adult-10-30000.data.csv")) {
            IPVDataset dataset = IPVDataset.load(is, false, ',', '"', false);
            assertEquals(30000, dataset.getNumberOfRows());

            ItemSet itemSet = new ItemSet(0);
            Map<String, Set<Integer>> map = VerificationUtils.buildValueMap(itemSet, dataset);

            assertEquals(72, map.keySet().size());
            assertTrue(map.get(",39").contains(0));
            assertEquals(752, map.get(",39").size());
        }
    }

    @Test
    public void testIsQuasiIdentifier() throws Exception {
        int k = 2;

        List<List<String>> values = new ArrayList<>();
        values.add(new ArrayList<>(Arrays.asList("a", "b")));
        values.add(new ArrayList<>(Arrays.asList("a", "b")));
        values.add(new ArrayList<>(Arrays.asList("b", "c")));

        IPVDataset IPVDataset = new IPVDataset(values, null, false);

        ItemSet itemSet = new ItemSet(0, 1);

        assertTrue(VerificationUtils.isQuasiIdentifier(itemSet, IPVDataset, k));

        values.add(new ArrayList<>(Arrays.asList("b", "c")));
        IPVDataset = new IPVDataset(values, null, false);
        assertFalse(VerificationUtils.isQuasiIdentifier(itemSet, IPVDataset, k));
    }

}

