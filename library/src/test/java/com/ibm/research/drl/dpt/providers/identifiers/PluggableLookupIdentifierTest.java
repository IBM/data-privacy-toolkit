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
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.ValueClass;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PluggableLookupIdentifierTest {

    @Test
    public void testRegisterOKIgnoreCase() {
        String value = "xyzW";

        IdentifierFactory identifierFactory = IdentifierFactory.getDefaultIdentifierFactory();

        for(Identifier identifier: identifierFactory.availableIdentifiers()) {
            assertFalse(identifier.isOfThisType(value));
        }

        Collection<String> patterns = Arrays.asList("test", "xyzw");
        Identifier pluggableRegexIdentifier = new PluggableLookupIdentifier("FOOBAR", List.of(), patterns, true,
                ValueClass.TEXT);
        assertTrue(pluggableRegexIdentifier.isOfThisType(value));
        identifierFactory.registerIdentifier(pluggableRegexIdentifier);

        Identifier matchingIdentifier = null;
        for(Identifier identifier: identifierFactory.availableIdentifiers()) {
            if(identifier.isOfThisType(value)) {
                matchingIdentifier = identifier;
                break;
            }
        }

        assertTrue(matchingIdentifier instanceof PluggableLookupIdentifier);
        assertThat(matchingIdentifier.getType().name(), is("FOOBAR"));
    }

    @Test
    public void testRegisterOKMatchCase() {
        String value = "xyzw";
        final IdentifierFactory factory = IdentifierFactory.getDefaultIdentifierFactory();

        for(Identifier identifier: factory.availableIdentifiers()) {
            assertFalse(identifier.isOfThisType(value), identifier.getClass().getName());
        }

        Collection<String> patterns = Arrays.asList("test", "xyzw");
        Identifier pluggableRegexIdentifier = new PluggableLookupIdentifier("FOOBAR33", Collections.emptyList(), patterns, false,
                ValueClass.TEXT);
        assertTrue(pluggableRegexIdentifier.isOfThisType(value));
        factory.registerIdentifier(pluggableRegexIdentifier);

        Identifier matchingIdentifier = null;
        for(Identifier identifier: factory.availableIdentifiers()) {
            if(identifier.isOfThisType(value)) {
                matchingIdentifier = identifier;
                break;
            }
        }

        assertTrue(matchingIdentifier instanceof PluggableLookupIdentifier);
        assertEquals("FOOBAR33", matchingIdentifier.getType().name());

        value = "XYZw";
        assertFalse(pluggableRegexIdentifier.isOfThisType(value));

    }
}
