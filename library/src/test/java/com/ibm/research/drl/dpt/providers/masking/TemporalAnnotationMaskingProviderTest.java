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