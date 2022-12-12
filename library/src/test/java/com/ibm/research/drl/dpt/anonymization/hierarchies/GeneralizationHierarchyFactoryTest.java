/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
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