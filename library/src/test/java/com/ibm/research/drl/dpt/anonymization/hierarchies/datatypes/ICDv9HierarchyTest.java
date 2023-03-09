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

public class ICDv9HierarchyTest {

    @Test
    public void testRaceHierarchy() {
        ICDv9Hierarchy hierarchy = ICDv9Hierarchy.getInstance();

        assertEquals(4, hierarchy.getHeight());
        assertEquals("250.61".toUpperCase(), hierarchy.encode("250.61", 0, false).toUpperCase());
        assertEquals("250".toUpperCase(), hierarchy.encode("250.61", 1, false).toUpperCase());
        assertEquals("240-279".toUpperCase(), hierarchy.encode("250.61", 2, false).toUpperCase());
        assertEquals("*".toUpperCase(), hierarchy.encode("250.61", 3, false).toUpperCase());
    }
}

