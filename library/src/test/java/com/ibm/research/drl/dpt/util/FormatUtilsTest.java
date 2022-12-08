/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormatUtilsTest {

    @Test
    public void testTitleCase() {
        String value = "JOHN";

        assertEquals("John", FormatUtils.makeTitleCase(value));

        value = "JOHN FOO";

        assertEquals("John Foo", FormatUtils.makeTitleCase(value));
    }
}
