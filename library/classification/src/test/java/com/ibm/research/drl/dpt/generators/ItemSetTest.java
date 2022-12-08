/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemSetTest {

    @Test
    public void testCompareTo() {
        ItemSet is1 = new ItemSet(0, 1);
        ItemSet is2 = new ItemSet(0, 2);
        ItemSet is3 = new ItemSet(0, 1, 2);

        assertThat("Equals", is1.compareTo(is2), not(0));
        assertThat("Equals", is1.compareTo(is3), not(0));

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (j == i) {
                    assertThat("Not equals " + i + " " + j, new ItemSet(i).compareTo(new ItemSet(j)), is(0));
                } else {
                    assertThat("Equals " + i + " " + j, new ItemSet(i).compareTo(new ItemSet(j)), not(0));
                }
            }
        }


        assertThat("Order relationship not verified (asymmetric)", Math.signum(is1.compareTo(is2)), is(-Math.signum(is2.compareTo(is1))));

        assertThat("Order relationship not verified", is1.compareTo(is1), is(0));
        assertThat("Order relationship not verified", is1.compareTo(is2), is(lessThan(0)));
        assertThat("Order relationship not verified", is1.compareTo(is3), is(greaterThan(0)));
        assertThat("Order relationship not verified", is2.compareTo(is3), is(greaterThan(0)));

        assertThat("Order relationship not verified", is2.compareTo(is1), is(greaterThan(0)));
        assertThat("Order relationship not verified", is3.compareTo(is1), is(lessThan(0)));
        assertThat("Order relationship not verified", is3.compareTo(is2), is(lessThan(0)));
    }

    @Test
    public void testInPriorityQueue() {
        PriorityQueue<ItemSet> queue = new PriorityQueue<>();

        queue.add(new ItemSet(0, 0, 1));
        queue.add(new ItemSet(0, 0, 2));
        queue.add(new ItemSet(1, 1, 2, 3));

        while (!queue.isEmpty()) {
            ItemSet is = queue.poll();
        }
    }

    @Test
    public void testCanBeExtendWith() {
        ItemSet is = new ItemSet(0, 0, 1, 2);

        assertTrue(is.canBeExtendWith(3));
        assertFalse(is.canBeExtendWith(0));
    }

    @Test
    public void jsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ItemSet is = new ItemSet(0, 1, 10);
        String s = mapper.writeValueAsString(is);

        assertThat(mapper.readValue(s, ItemSet.class), is(is));
    }
}
