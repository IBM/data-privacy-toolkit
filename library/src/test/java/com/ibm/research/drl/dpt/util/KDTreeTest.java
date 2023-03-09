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