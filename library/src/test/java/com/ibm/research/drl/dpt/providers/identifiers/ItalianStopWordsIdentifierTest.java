/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ItalianStopWordsIdentifierTest {
    @Test
    public void testPositive() {
        ItalianStopWordsIdentifier identifier = new ItalianStopWordsIdentifier();

        for (String valid : new String[]{
                "il",
                "mio",
                "grande",
                "MIO",
                "MiO"
        }) {
            assertThat(valid, identifier.isOfThisType(valid), is(true));
        }
    }
}