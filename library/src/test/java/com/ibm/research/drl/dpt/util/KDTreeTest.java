/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class KDTreeTest {
    @Test
    public void correctnessWithWikipediaExample() {
        KDTree<KDTree.CartesianPoint> tree = new KDTree<>(Arrays.asList(
                new KDTree.CartesianPoint(2., 3.),
                new KDTree.CartesianPoint(5., 4.),
                new KDTree.CartesianPoint(9., 6.),
                new KDTree.CartesianPoint(4., 7.),
                new KDTree.CartesianPoint(8., 1.),
                new KDTree.CartesianPoint(7., 2.)));

        Collection<KDTree.CartesianPoint> list = tree.nearestNeighbourSearch(1, new KDTree.CartesianPoint(9, 2));

        assertThat(list.size(), is(1));
        for (KDTree.CartesianPoint point : list) {
            assertThat(point.x, is(8.));
            assertThat(point.y, is(1.));
        }
    }
}