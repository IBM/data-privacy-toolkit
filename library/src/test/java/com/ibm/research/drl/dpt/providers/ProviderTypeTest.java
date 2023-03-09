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
package com.ibm.research.drl.dpt.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ProviderTypeTest {

    @Test
    public void testFriendlyName() {
        String friendlyName = ProviderType.CITY.getFriendlyName();
        assertEquals("City", friendlyName);
    }

    @Test
    public void testPublicValues() {
        Collection<ProviderType> providerTypes = ProviderType.publicValues();
        assertFalse(providerTypes.contains(ProviderType.EMPTY));
        assertFalse(providerTypes.contains(ProviderType.UNKNOWN));
    }

    @Test
    public void serialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        for (ProviderType type : ProviderType.publicValues()) {
            String s = mapper.writeValueAsString(type);

            assertNotNull(s);
        }
    }
}
