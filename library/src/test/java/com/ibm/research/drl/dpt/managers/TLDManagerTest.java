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
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TLDManagerTest {

    @Test
    public void testTLD() {
        TLDManager tldManager = TLDManager.instance();
        String domain = "www.nba.com";
        assertEquals("com", tldManager.getTLD(domain));
        domain = "www.nba.co.uk";
        assertEquals("co.uk", tldManager.getTLD(domain));

        domain = "www.nba.COM";
        assertEquals("COM", tldManager.getTLD(domain));

        domain = "www.nba.pra";
        assertNull(tldManager.getTLD(domain));
    }

    @Test
    @Disabled
    public void testPerformanceGetTLD() {
        int N = 1000000;
        String hostname = "ie.ibm.com";
        TLDManager tldManager = TLDManager.instance();

        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            String tld = tldManager.getTLD(hostname);
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.printf("%d operations took %d milliseconds (%f msec per op)%n", N, diff, (double) diff / N);
    }
}
