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
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.util.EntropyUtilities;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntropyUtilitiesTest {

    @Test
    public void testEntropyCalculation() {

        Collection<Long> values = Arrays.asList(2L, 3L);

        double expected = - (0.4*Math.log(0.4) + 0.6*Math.log(0.6));
        double entropy = EntropyUtilities.calculateEntropy(values, 5L);

        assertEquals(expected, entropy, 0.1);

    }
}
