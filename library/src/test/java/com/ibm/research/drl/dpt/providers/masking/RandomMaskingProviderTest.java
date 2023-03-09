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

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class RandomMaskingProviderTest {

    @Test
    public void testMask() throws Exception {

        RandomMaskingProvider maskingProvider = new RandomMaskingProvider();

        String value = "AAAAcccDDD12345566";
        String maskedValue = maskingProvider.mask(value);

        assertNotEquals(maskedValue, value);
        assertEquals(maskedValue.length(), value.length());

        for(int i = 0; i < maskedValue.length(); i++) {
            char c1 = maskedValue.charAt(i);
            char c2 = value.charAt(i);
            assertEquals(Character.getType(c1), Character.getType(c2));
        }
    }

    @Test
    public void testOtherChar() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        RandomMaskingProvider maskingProvider = new RandomMaskingProvider(maskingConfiguration);
        
        String value = "*A-Acc,DDD123455";
        String maskedValue = maskingProvider.mask(value);
        assertNotEquals(maskedValue, value);
        assertEquals(maskedValue.length(), value.length());
        for(int i = 0; i < maskedValue.length(); i++) {
            char c1 = maskedValue.charAt(i);
            char c2 = value.charAt(i);
            assertEquals(Character.getType(c1), Character.getType(c2));
            if (i == 0 || i == 2 || i == 6) {
                assertEquals(c1, c2);
            }
        }
    }

}
