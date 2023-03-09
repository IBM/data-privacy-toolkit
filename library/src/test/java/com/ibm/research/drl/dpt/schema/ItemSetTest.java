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
