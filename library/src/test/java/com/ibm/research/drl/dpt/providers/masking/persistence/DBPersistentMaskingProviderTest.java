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
package com.ibm.research.drl.dpt.providers.masking.persistence;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class DBPersistentMaskingProviderTest {
    @Test
    @Disabled("Require mocked database, to explore")
    public void testDBCache() throws Exception {
        /*
        DBCache cache = new DBCache();

        String host = "jdbc:postgresql://localhost/postgres";
        String username = "postgres";
        String password = "pipichu123";

        cache.initialize(host, username, password, 0, "foo");

        String originalValue = "foo";
        MaskingProvider maskingProvider = new RandomMaskingProvider();

        String maskedValue_once = cache.getValue(originalValue, maskingProvider);
        assertNotEquals(maskedValue_once, originalValue);
        assertEquals(3, maskedValue_once.length());

        String maskedValue_twice = cache.getValue(originalValue, maskingProvider);

        assertEquals(maskedValue_once, maskedValue_twice);
        */

    }
}