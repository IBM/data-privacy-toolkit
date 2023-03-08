/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class DummyMaskingProviderTest {
    @Test
    public void testThatDummyDoesNothing() {
        DummyMaskingProvider dummy = new DummyMaskingProvider(null);

        assertThat(dummy.mask("FOO"), equalTo("FOO"));
    }

    @Test
    public void testThatDummyCopiesValueOfLinkedField() {
        DummyMaskingProvider dummy = new DummyMaskingProvider(null);

        assertThat(dummy.maskEqual("FOO", "BAR"), equalTo("BAR"));
    }
}