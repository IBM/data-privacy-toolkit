/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LiteralDateIdentifierTest {
    @Test
    public void testPositiveExamples() {
        LiteralDateIdentifier identifier = new LiteralDateIdentifier();

        String[] examples = new String[]{
                "10 settembre 2020",
                "may 15th 1981"
        };

        for (String example : examples) {
            assertThat(example, identifier.isOfThisType(example), is(true));
        }
    }
}