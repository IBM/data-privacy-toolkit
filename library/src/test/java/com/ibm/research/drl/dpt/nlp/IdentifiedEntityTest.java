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

