/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdentifiedEntityTest {
    
    @Test
    public void testEquality() {
        Set<IdentifiedEntityType> types = new HashSet<>();
        types.add(new IdentifiedEntityType("a", "b", IdentifiedEntityType.UNKNOWN_SOURCE));
        
        IdentifiedEntity first = new IdentifiedEntity("foo", 0, 10, types, Collections.singleton(PartOfSpeechType.UNKNOWN));
        
        IdentifiedEntity second = new IdentifiedEntity("foo", 0, 10, types, Collections.singleton(PartOfSpeechType.UNKNOWN));
        assertEquals(first, second);
    }
    
    @Test
    public void testConcat() {
        Set<IdentifiedEntityType> types = new HashSet<>();
        types.add(new IdentifiedEntityType("a", "b", IdentifiedEntityType.UNKNOWN_SOURCE));
        types.add(new IdentifiedEntityType("c", "d", IdentifiedEntityType.UNKNOWN_SOURCE));

        IdentifiedEntity first = new IdentifiedEntity("foo", 0, 10, types, Collections.singleton(PartOfSpeechType.UNKNOWN));
        assertEquals("a,c", first.concatTypes(",")); 
    }
}

