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
package com.ibm.research.drl.dpt.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HashUtilsTest {

    @Test
    public void testHashUtils() {
        long l = HashUtils.longFromHash("000000");
        assertNotNull(l);

        long originalValue = l;
        for(int i = 0; i < 1000; i++) {
            l = HashUtils.longFromHash("000000");
            assertEquals(originalValue, l);
        }
    }

    @Test
    public void testNull() {
        Long l = HashUtils.longFromHash(null);
        assertNotNull(l);
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;

        long start = System.currentTimeMillis();

        for(int i = 0; i < N; i++) {
            var l = HashUtils.longFromHash("000000");
        }

        System.out.println("N: " + N + ", time: " + (System.currentTimeMillis() - start));
    }
}
