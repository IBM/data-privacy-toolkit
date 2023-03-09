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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingProvider maskingProvider = new BooleanMaskingProvider();
        String value = "true";

        int randomOK = 0;
        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            if (!maskedValue.equals(value)) {
                randomOK++;
            }
        }

        assertTrue(randomOK > 0);
    }
}

