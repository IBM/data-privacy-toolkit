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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LatticeNodeTest {

    @Test
    public void testSum() {
        int[] values = new int[]{1, 2, 0};
        LatticeNode node = new LatticeNode(values);
        assertEquals(3, node.sum());
    }

    @Test
    public void testEquals() {
        int[] values = new int[]{1, 2, 0};
        LatticeNode node = new LatticeNode(values);

        int[] otherValues = new int[]{1, 2, 0};
        LatticeNode otherNode = new LatticeNode(otherValues);
        assertTrue(node.equals(otherNode));

        otherValues = new int[]{2, 1, 0};
        otherNode = new LatticeNode(otherValues);
        assertFalse(node.equals(otherNode));
    }

    @Test
    public void testToString() {
        int[] values = new int[]{1, 2, 0};
        LatticeNode node = new LatticeNode(values);
        assertEquals("1:2:0", node.toString());
    }

    @Test
    public void testIsDescendent() {
        int[] values = new int[]{1, 2, 0};
        LatticeNode node = new LatticeNode(values);

        int[] otherValues = new int[]{1, 2, 0};
        LatticeNode otherNode = new LatticeNode(otherValues);

        //the nodes are equal so we should get false
        assertFalse(node.isDescendent(otherNode));

        otherValues = new int[]{1, 3, 0};
        otherNode = new LatticeNode(otherValues);
        //node at index 1 has lesser value
        assertFalse(node.isDescendent(otherNode));

        otherValues = new int[]{1, 1, 0};
        otherNode = new LatticeNode(otherValues);
        assertTrue(node.isDescendent(otherNode));
    }
}

