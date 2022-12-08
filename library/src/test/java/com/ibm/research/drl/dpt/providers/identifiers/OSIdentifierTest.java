/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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
