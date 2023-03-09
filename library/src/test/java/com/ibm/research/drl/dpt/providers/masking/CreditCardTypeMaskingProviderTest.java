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

import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.CreditCardTypeIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreditCardTypeMaskingProviderTest {

    @Test
    public void testMask() {
        CreditCardTypeMaskingProvider maskingProvider = new CreditCardTypeMaskingProvider();
        CreditCardTypeIdentifier identifier = new CreditCardTypeIdentifier();

        String originalValue = "VISA";

        int nonMatches = 0;
        for (int i = 0; i < 1000; i++) {
            String maskedValue = maskingProvider.mask(originalValue);
            assertTrue(identifier.isOfThisType(maskedValue));
            if (!maskedValue.equals(originalValue)) {
                nonMatches++;
            }
        }

        assertTrue(nonMatches > 0);
    }

    @Test
    public void testCompoundMask() {
        CreditCardTypeMaskingProvider maskingProvider = new CreditCardTypeMaskingProvider();

        String originalCCType = "VISA";

        for (int i = 0; i < 100; i++) {
            String maskedCCType = maskingProvider.maskLinked(originalCCType, "5523527012345678", ProviderType.CREDIT_CARD);
            assertTrue("Mastercard".equalsIgnoreCase(maskedCCType));
        }
    }
}

