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
import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PluggableRegexIdentifierTest {

    @Test
    public void testCaptureGroups() {

        String patternWithGroup = ".*(bar)";
        String patternNoGroup = "test.*";
        String patternMultipleGroups = ".*(good).*(bad)";
        String bracketPattern = "\\[((([A-Z][A-Za-z]+)\\s*)+)\\]";

        PluggableRegexIdentifier pluggableRegexIdentifier = new PluggableRegexIdentifier(
                "foobar",
                Collections.emptyList(),
                Arrays.asList(patternWithGroup, patternNoGroup, patternMultipleGroups, bracketPattern),
                ValueClass.TEXT);

        Tuple<Boolean, Tuple<Integer, Integer>> result = pluggableRegexIdentifier.isOfThisTypeWithOffset("foobar");

        assertTrue(result.getFirst());
        assertEquals(3, result.getSecond().getFirst().intValue());
        assertEquals(3, result.getSecond().getSecond().intValue());

        result = pluggableRegexIdentifier.isOfThisTypeWithOffset("test1");
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(5, result.getSecond().getSecond().intValue());

        result = pluggableRegexIdentifier.isOfThisTypeWithOffset("nomatch");
        assertFalse(result.getFirst());

        result = pluggableRegexIdentifier.isOfThisTypeWithOffset("tost1good2bad");
        assertTrue(result.getFirst());
        assertEquals(5, result.getSecond().getFirst().intValue());
        assertEquals(4, result.getSecond().getSecond().intValue());

        result = pluggableRegexIdentifier.isOfThisTypeWithOffset("[Hu]");
        assertTrue(result.getFirst());
        assertEquals(1, result.getSecond().getFirst().intValue());
        assertEquals(2, result.getSecond().getSecond().intValue());
    }

    @Test
    public void testRegisterOK() {
        String value = "foobar";
        final IdentifierFactory factory = IdentifierFactory.getDefaultIdentifierFactory();

        for(Identifier identifier: factory.availableIdentifiers()) {
            assertFalse(identifier.isOfThisType(value));
        }

        Collection<String> patterns = Collections.singletonList("foobar");
        Identifier pluggableRegexIdentifier = new PluggableRegexIdentifier(
                "FOOBAR2",
                Collections.emptyList(),
                patterns,
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

        assertTrue(matchingIdentifier instanceof PluggableRegexIdentifier);
        assertThat(matchingIdentifier.getType().name(), is("FOOBAR2"));
    }

    @Test
    public void testGreek() {
        List<String> patterns = Arrays.asList(
            "ΑΡ. ΜΗΤΡΩΟΥ: (Patient ID)",
            "ΚΩΔ. ΠΕΡΙΣΤΑΤΙΚΟΥ: (Case ID)",
            "ΗΜΕΡ. ΓΕΝΝΗΣΗΣ: (dd/mm/yyyy)");

        PluggableRegexIdentifier pluggableRegexIdentifier = new PluggableRegexIdentifier(
                "FOOBAR2",
                Collections.emptyList(),
                patterns,
                ValueClass.TEXT);

        String input = "ΑΡ. ΜΗΤΡΩΟΥ: Patient ID";

        Tuple<Boolean, Tuple<Integer, Integer>> res = pluggableRegexIdentifier.isOfThisTypeWithOffset(input);
        assertTrue(res.getFirst());
        assertEquals(13, res.getSecond().getFirst().intValue());
        assertEquals("Patient ID".length(), res.getSecond().getSecond().intValue());
    }
}
