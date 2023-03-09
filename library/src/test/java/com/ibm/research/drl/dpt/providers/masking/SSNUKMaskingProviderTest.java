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
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.SSNUKIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SSNUKMaskingProviderTest {

    @Test
    public void testMaskPrefixPreservation() {
        MaskingProvider maskingProvider = new SSNUKMaskingProvider();
        SSNUKIdentifier identifier = new SSNUKIdentifier();

        String ssn = "AB123456C";
        String maskedValue = maskingProvider.mask(ssn);

        assertNotEquals(maskedValue, ssn);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertTrue(maskedValue.startsWith("AB"));
    }

    @Test
    public void testMaskNoPrefixPreservation() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("ssnuk.mask.preservePrefix", false);
        MaskingProvider maskingProvider = new SSNUKMaskingProvider(configuration);
        SSNUKIdentifier identifier = new SSNUKIdentifier();

        String ssn = "AB123456C";

        int count = 0;

        for(int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(ssn);
            assertNotEquals(maskedValue, ssn);
            assertTrue(identifier.isOfThisType(maskedValue));

            if(!maskedValue.startsWith("AB")) {
                count++;
            }
        }

        assertTrue(count > 0);
    }
}
