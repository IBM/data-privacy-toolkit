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
package com.ibm.research.drl.dpt.anonymization.hierarchies;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.DateHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.DateYYYYMMDDHierarchy;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeneralizationHierarchyFactoryTest {
    @Test
    public void hierarchyCanBeGeneratedPassingFQCN() {
        GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(DateYYYYMMDDHierarchy.class.getCanonicalName());

        assertNotNull(hierarchy);
    }

    @Test
    void testGetDefaultHierarchyFromJsonNode() {
        ObjectCodec mapper = new ObjectMapper();
        ObjectNode hierarchy = (ObjectNode) mapper.createObjectNode();
        hierarchy.put("type", "DATE");
        hierarchy.put("format", "dd/MM/yyyy");
        GeneralizationHierarchy dateHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(hierarchy);

        assertNotNull(dateHierarchy);
        assertThat(dateHierarchy, instanceOf(DateHierarchy.class));
    }

    @Test
    void testGetDefaultHierarchyFromString() {
        GeneralizationHierarchy dateHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy("DATE-YYYY-MM-DD");

        assertNotNull(dateHierarchy);
        assertThat(dateHierarchy, instanceOf(DateYYYYMMDDHierarchy.class));
    }
}