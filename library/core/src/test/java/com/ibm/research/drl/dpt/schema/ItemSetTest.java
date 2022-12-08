/*******************************************************************
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
*******************************************************************/
package com.ibm.research.drl.dpt.schema;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemSetTest {

    @Test
    public void testEquals() {
        ItemSet is1 = new ItemSet(2);
        ItemSet is2 = new ItemSet(2);

        assertEquals(is1, is2);

        is2.set(0);

        assertNotEquals(is1, is2);
    }

    @Test
    public void testCompare() {
        ItemSet is1 = new ItemSet(2);
        ItemSet is2 = new ItemSet(2);

        assertThat(is1.compareTo(is1), is(0));
        assertThat(is1.compareTo(is2), is(0));

        is1.set(0);

        assertThat(is1.compareTo(is2), is(not(0)));
    }

    @Test
    public void testConstructor() {
        ItemSet is = new ItemSet(3);

        is.set(1);

        ItemSet is2 = new ItemSet(is.toString());

        assertEquals(is, is2);
    }
}
