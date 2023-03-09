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
package com.ibm.research.drl.dpt.nlp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterHistogramTest {

    @Test
    public void testHistogram() {

        String input = "aaaa";

        int[] histogram = CharacterHistogram.generateHistogram(input);

        assertEquals(4, histogram[0]);
        for(int i = 1; i < histogram.length; i++) {
            assertEquals(0, histogram[i]);
        }

        input = "abcde!!#$$90809";
        histogram = CharacterHistogram.generateHistogram(input);

        assertEquals(1, histogram[0]);
        assertEquals(1, histogram[1]);
        assertEquals(1, histogram[2]);
        assertEquals(1, histogram[3]);
        assertEquals(1, histogram[4]);
        for(int i = 5; i < histogram.length; i++) {
            assertEquals(0, histogram[i]);
        }
    }
}


