/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
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

