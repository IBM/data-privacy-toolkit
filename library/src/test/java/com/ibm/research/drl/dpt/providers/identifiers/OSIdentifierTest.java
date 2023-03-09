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

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OSIdentifierTest {
    OSIdentifier identifier = new OSIdentifier();

    @Test
    public void testCorrectlyIdentifiesKnownTerms() {
        String[] knownTexts = {
                "Mac OS Sierra",
                "Mac Sierra",
                "macOS Sierra",
                "Windows XP",
                "Red Hat Linux"

        };

        String[] invalidTerms = {
                "Robin",
                "Diane",
                "Crohn's ileitis"
        };
        
        for (String text : knownTexts) {
            assertThat(text, identifier.isOfThisType(text), is(true));
        }
        
        for(String text: invalidTerms) {
            assertThat(text, identifier.isOfThisType(text), is(false));
        }
    }
    
    @Test
    public void testNormalization() {
        String[] terms = {
                "Mac OS SIERRA",
                "macos sierra"
        };

        for (String text : terms) {
            assertThat(text, identifier.isOfThisType(text), is(true));
        }
    }
}
