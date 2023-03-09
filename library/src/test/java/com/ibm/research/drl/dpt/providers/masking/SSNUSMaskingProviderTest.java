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
import com.ibm.research.drl.dpt.models.SSNUS;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.SSNUSIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SSNUSMaskingProviderTest {
    @Test
    public void testMaskPreserveAreaAndGroup() {
        MaskingProvider maskingProvider = new SSNUSMaskingProvider();
        Identifier identifier = new SSNUSIdentifier();

        String ssn = "123-12-1234";
        String maskedValue = maskingProvider.mask(ssn);

        assertNotEquals(maskedValue, ssn);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertTrue(maskedValue.startsWith("123-12-"));
    }

    @Test
    public void testMaskNoPreserving() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("ssnus.mask.preserveAreaNumber", false);
        configuration.setValue("ssnus.mask.preserveGroup", false);
        MaskingProvider maskingProvider = new SSNUSMaskingProvider(configuration);
        SSNUSIdentifier identifier = new SSNUSIdentifier();

        String ssnValue = "123-12-1234";

        int randomizationOKGroup = 0;
        int randomizationOKArea = 0;

        for(int i=0; i < 1_000; i++) {
            String maskedValue = maskingProvider.mask(ssnValue);
            assertNotEquals(maskedValue, ssnValue);

            assertTrue(identifier.isOfThisType(maskedValue));
            SSNUS ssn = identifier.parseSSNUS(maskedValue);

            if(!(ssn.getAreaNumber().equals("123"))) {
                randomizationOKArea++;
            }

            if(!(ssn.getGroup().equals("12"))) {
                randomizationOKGroup++;
            }
        }

        assertTrue(randomizationOKGroup > 0);
        assertTrue(randomizationOKArea > 0);
    }

    @Test
    public void testMaskInvalidValue() {
        MaskingProvider maskingProvider = new SSNUSMaskingProvider();
        Identifier identifier = new SSNUSIdentifier();

        String ssn = "foobar";
        for (int i = 0; i < 10; i++) {
            String maskedValue = maskingProvider.mask(ssn);
            if (identifier.isOfThisType(maskedValue)) {
                return;
            }
        }
        fail("Random generation failed 10 times in a row");
    }
}

