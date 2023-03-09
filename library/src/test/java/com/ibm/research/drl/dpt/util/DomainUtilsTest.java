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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DomainUtilsTest {

    @Test
    public void testDomainSplit() {

        String domain = "ie.ibm.com";

        Tuple<String, String> results = DomainUtils.splitDomain(domain, 3);
        assertEquals("", results.getFirst());
        assertEquals("ie.ibm.com", results.getSecond());

        results = DomainUtils.splitDomain(domain, 30);
        assertEquals("", results.getFirst());
        assertEquals("ie.ibm.com", results.getSecond());

        results = DomainUtils.splitDomain(domain, 0);
        assertEquals(domain, results.getFirst());
        assertTrue(results.getSecond().isEmpty());

        results = DomainUtils.splitDomain(domain, -1);
        assertEquals(domain, results.getFirst());
        assertTrue(results.getSecond().isEmpty());

        results = DomainUtils.splitDomain(domain, 1);
        assertEquals("ie.ibm", results.getFirst());
        assertEquals("com", results.getSecond());

        results = DomainUtils.splitDomain(domain, 2);
        assertEquals("ie", results.getFirst());
        assertEquals("ibm.com", results.getSecond());

        domain = "1.2.3.4";
        results = DomainUtils.splitDomain(domain, 0);
        assertEquals(domain, results.getFirst());
        assertTrue(results.getSecond().isEmpty());

        domain = "domain.pr@";
        results = DomainUtils.splitDomain(domain, 2);
        assertEquals(domain, results.getFirst());
        assertTrue(results.getSecond().isEmpty());
    }

    @Test
    public void testDomainSplitEffectiveTLD() {
        String domain = "www.ibm.co.uk";

        Tuple<String, String> results = DomainUtils.splitDomain(domain, 1);
        assertEquals("www.ibm", results.getFirst());
        assertEquals("co.uk", results.getSecond());

        results = DomainUtils.splitDomain(domain, 2);
        assertEquals("www", results.getFirst());
        assertEquals("ibm.co.uk", results.getSecond());
    }

    @Test
    public void testIPV4AddressSplit() {
        String domain = "1.2.3.4";

        Tuple<String, String> results = DomainUtils.splitIPV4Address(domain, 1);
        assertEquals("1.2.3", results.getFirst());
        assertEquals("4", results.getSecond());

        results = DomainUtils.splitIPV4Address(domain, 0);
        assertEquals("1.2.3.4", results.getFirst());
        assertTrue(results.getSecond().isEmpty());

        results = DomainUtils.splitIPV4Address(domain, 2);
        assertEquals("1.2", results.getFirst());
        assertEquals("3.4", results.getSecond());

        results = DomainUtils.splitIPV4Address(domain, 4);
        assertTrue(results.getFirst().isEmpty());
        assertEquals("1.2.3.4", results.getSecond());

        results = DomainUtils.splitIPV4Address(domain, 40);
        assertEquals("", results.getFirst());
        assertEquals("1.2.3.4", results.getSecond());
    }

}
