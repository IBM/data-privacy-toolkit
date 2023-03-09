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
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.ZIPCodeCompBasedHierarchy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LatticeTest {

    @Test
    public void testLatticeConstruction() {
        MaterializedHierarchy dateHierarchy = new MaterializedHierarchy();
        dateHierarchy.add("01/01/2015", "Jan 2015", "2015");
        dateHierarchy.add("02/01/2015", "Jan 2015", "2015");
        dateHierarchy.add("03/01/2015", "Jan 2015", "2015");

        MaterializedHierarchy genderHierarchy = new MaterializedHierarchy();
        genderHierarchy.add("Male", "Person");
        genderHierarchy.add("Female", "Person");

        MaterializedHierarchy ageHierarchy = new MaterializedHierarchy();
        ageHierarchy.add("10", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("20", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));

        Lattice lattice = new Lattice(null, columnInformationList, 1.0d);

        assertEquals(7, lattice.getLatticeMaxLevel());

        Map<Integer, Set<LatticeNode>> latticeMap = lattice.getLattice();
        assertEquals(1, latticeMap.get(0).size());
        assertEquals(3, latticeMap.get(1).size());
        assertEquals(5, latticeMap.get(2).size());
        assertEquals(6, latticeMap.get(3).size());
        assertEquals(6, latticeMap.get(4).size());
        assertEquals(5, latticeMap.get(5).size());
        assertEquals(3, latticeMap.get(6).size());
        assertEquals(1, latticeMap.get(7).size());

        int height = lattice.getLatticeMaxLevel() + 1;

        for(int k = 0; k < height; k++) {
            Set<LatticeNode> nodes = latticeMap.get(k);
            for(LatticeNode n: nodes) {
                assertEquals(k, n.sum());
            }
        }
    }

    @Test
    public void testMatchesMaximumExplorationLevel() {
        LatticeNode node = new LatticeNode(new int[]{1,1,1,3});

        int[] maxLevels = new int[]{-1, -1, -1, -1};
        assertTrue(Lattice.matchesMaximumExplorationLevel(node, maxLevels));

        maxLevels = new int[]{-1, -1, -1, 2};
        assertFalse(Lattice.matchesMaximumExplorationLevel(node, maxLevels));

        maxLevels = new int[]{-1, -1, -1, 4};
        assertTrue(Lattice.matchesMaximumExplorationLevel(node, maxLevels));

        maxLevels = new int[]{1, 1, 1, 3};
        assertTrue(Lattice.matchesMaximumExplorationLevel(node, maxLevels));
    }

    @Test
    @Disabled
    public void testSucc() {
        MaterializedHierarchy dateHierarchy = new MaterializedHierarchy();
        dateHierarchy.add("01/01/2015", "Jan 2015", "2015");
        dateHierarchy.add("02/01/2015", "Jan 2015", "2015");
        dateHierarchy.add("03/01/2015", "Jan 2015", "2015");

        MaterializedHierarchy genderHierarchy = new MaterializedHierarchy();
        genderHierarchy.add("Male", "Person");
        genderHierarchy.add("Female", "Person");

        MaterializedHierarchy ageHierarchy = new MaterializedHierarchy();
        ageHierarchy.add("10", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("20", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(new ZIPCodeCompBasedHierarchy(), ColumnType.QUASI));

        Lattice lattice = new Lattice(null, columnInformationList, 1.0d);

        LatticeNode node = new LatticeNode(new int[]{1,1,1,3});
        Collection<LatticeNode> successors = lattice.getSuccessors(node, true);

        for(LatticeNode n: successors) {
            System.out.println(n);
        }

        System.out.println("-------");
        node = new LatticeNode(new int[]{1,1,1,1});
        successors = lattice.getSuccessors(node, false);

        for(LatticeNode n: successors) {
            System.out.println(n);
        }
    }

    @Test
    public void testSuccessors() {
        MaterializedHierarchy dateHierarchy = new MaterializedHierarchy();
        dateHierarchy.add("01/01/2015", "Jan 2015", "2015");
        dateHierarchy.add("02/01/2015", "Jan 2015", "2015");
        dateHierarchy.add("03/01/2015", "Jan 2015", "2015");

        MaterializedHierarchy genderHierarchy = new MaterializedHierarchy();
        genderHierarchy.add("Male", "Person");
        genderHierarchy.add("Female", "Person");

        MaterializedHierarchy ageHierarchy = new MaterializedHierarchy();
        ageHierarchy.add("10", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("20", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));

        Lattice lattice = new Lattice(null, columnInformationList, 1.0d);

        LatticeNode node = new LatticeNode(new int[]{0, 1, 0});

        Collection<LatticeNode> expectedSuccessors = new ArrayList<>();
        expectedSuccessors.add(new LatticeNode(new int[]{0, 1, 1}));
        expectedSuccessors.add(new LatticeNode(new int[]{1, 1, 0}));

        Collection<LatticeNode> successors = lattice.getSuccessors(node, true);

        assertEquals(2, successors.size());

        for(LatticeNode n: expectedSuccessors) {
            assertTrue(successors.contains(n));
        }
    }

}

