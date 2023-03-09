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

public class YOBHierarchyTest {

    @Test
    public void testYOBHierarchy() {
        YOBHierarchy hierarchy = YOBHierarchy.getInstance();

        assertEquals(5, hierarchy.getHeight());
        assertEquals("1950", hierarchy.encode("1950", 0, false));
        assertEquals("1950-1951", hierarchy.encode("1950", 1, false));
        assertEquals("1948-1951", hierarchy.encode("1950", 2, false));
        assertEquals("1944-1951", hierarchy.encode("1950", 3, false));
        assertEquals("*", hierarchy.encode("1950", 4, false));

        assertEquals("1951", hierarchy.encode("1951", 0, false));
        assertEquals("1950-1951", hierarchy.encode("1951", 1, false));
        assertEquals("1948-1951", hierarchy.encode("1951", 2, false));
        assertEquals("1944-1951", hierarchy.encode("1951", 3, false));
        assertEquals("*", hierarchy.encode("1951", 4, false));
    }
    
    @Test
    public void testLeaves() {
        YOBHierarchy hierarchy = YOBHierarchy.getInstance();
        assertEquals(0, hierarchy.getNodeLeaves("1978").size());
        assertEquals(2, hierarchy.getNodeLeaves("1978-1979").size());
    }
}
