/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TemporalAnnotationMaskingProviderTest {
    @Test
    public void testRegExp() throws Exception {
        String test0 = "day before";
        String test1 = "the day before yesterday was sunny";
        String test2 = "3 days before yesterday";

        assertTrue(TemporalAnnotationMaskingProvider.dayBefore.matcher(test0).find());
        assertTrue(TemporalAnnotationMaskingProvider.dayBefore.matcher(test1).find());
        assertFalse(TemporalAnnotationMaskingProvider.dayBefore.matcher(test2).find());
    }

    @Test
    public void testNumberExtraction() throws Exception {
        String test = "3 days before yesterday";

        Matcher m = TemporalAnnotationMaskingProvider.getNumber.matcher(test);

        assertTrue(m.find());
        assertThat(Integer.parseInt(m.group(1)), is(3));
    }
}