/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class MaterializedHierarchyTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testNodes() {

        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));

        assertEquals("*", terms.getNode("*").getValue());
        assertEquals(5, terms.getNode("*").length());
        assertTrue(terms.getNode("Europe").cover("Italy"));

        assertEquals(3, terms.getHeight());
        assertEquals(0, terms.getNodeLevel("Italy"));
        assertEquals(0, terms.getNode("Italy").getLevel());
        assertEquals(1, terms.getNodeLevel("Europe"));
        assertEquals(1, terms.getNode("Europe").getLevel());
        assertEquals(2, terms.getNode("*").getLevel());
    }

    @Test
    public void testEncoding() {

        MaterializedHierarchy materializedHierarchy = new MaterializedHierarchy();
        materializedHierarchy.add("worker", "construction", "*");
        materializedHierarchy.add("researcher", "academia", "*");

        assertEquals(3, materializedHierarchy.getHeight());

        assertEquals("construction".toUpperCase(), materializedHierarchy.encode("worker", 1, false).toUpperCase());
        assertEquals("construction".toUpperCase(), materializedHierarchy.encode("construction", 0, false).toUpperCase());
        assertEquals("*".toUpperCase(), materializedHierarchy.encode("construction", 1, false).toUpperCase());
        assertEquals("*", materializedHierarchy.encode("worker", 10, false));
    }
    
    @Test
    public void testIndex() {
        MaterializedHierarchy terms = new MaterializedHierarchy();
        terms.add(Arrays.asList("Greece", "Europe", "*"));
        terms.add(Arrays.asList("Italy", "Europe", "*"));
        terms.add(Arrays.asList("Egypt", "Africa", "*"));
        terms.add(Arrays.asList("Singapore", "Asia", "*"));
        terms.add(Arrays.asList("China", "Asia", "*"));
        
        assertEquals(0, terms.getIndex("Greece").intValue());
        assertEquals(0, terms.getIndex("greece").intValue());
        assertEquals(4, terms.getIndex("China").intValue());
        assertNull(terms.getIndex("foo"));
    }

    @Test
    public void hierarchyIsDeserializedCorrectly() throws JsonProcessingException {
        String serializedHierarchy = "{\"terms\":[[\"Male\",\"*\"],[\"Female\",\"*\"]]}";

        MaterializedHierarchy hierarchy = mapper.readValue(serializedHierarchy, MaterializedHierarchy.class);

        assertThat(serializedHierarchy, is(mapper.writeValueAsString(hierarchy)));
    }
}
