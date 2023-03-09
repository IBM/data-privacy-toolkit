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
import com.ibm.research.drl.dpt.providers.identifiers.IMSIIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IMSIMaskingProviderTest {

    @Test
    public void testMaskPreserveMCCMNC() {
        MaskingProvider maskingProvider = new IMSIMaskingProvider();
        Identifier identifier = new IMSIIdentifier();

        String value = "310150123456789";
        String maskedValue = maskingProvider.mask(value);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertTrue(maskedValue.startsWith("310150"));
    }

    @Test
    public void testMaskPreserveMCC() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("imsi.mask.preserveMCC", true);
        configuration.setValue("imsi.mask.preserveMNC", false);
        MaskingProvider maskingProvider = new IMSIMaskingProvider(configuration);
        Identifier identifier = new IMSIIdentifier();

        String value = "310150123456789";
        int randomMNCOK = 0;
        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            assertTrue(identifier.isOfThisType(maskedValue));
            assertTrue(maskedValue.startsWith("310"));
            if(!maskedValue.startsWith("150", 3)) {
                randomMNCOK++;
            }
        }

        assertTrue(randomMNCOK > 0);
    }

    @Test
    public void testMaskNoPreserve() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("imsi.mask.preserveMCC", false);
        configuration.setValue("imsi.mask.preserveMNC", false);
        MaskingProvider maskingProvider = new IMSIMaskingProvider(configuration);
        Identifier identifier = new IMSIIdentifier();

        String value = "310150123456789";

        int randomizationOKMCC = 0;
        int randomizationOKMNC = 0;

        for (int i = 0; i < 100; i++) {
            String maskedValue = maskingProvider.mask(value);
            assertTrue(identifier.isOfThisType(maskedValue));
            if(!maskedValue.startsWith("310")) {
                randomizationOKMCC++;
            }
            if(!maskedValue.startsWith("150", 3)) {
                randomizationOKMNC++;
            }
        }

        assertTrue(randomizationOKMCC > 0);
        assertTrue(randomizationOKMNC > 0);

    }

    @Test
    public void testMaskInvalidValue() {
        MaskingProvider maskingProvider = new IMSIMaskingProvider();
        Identifier identifier = new IMSIIdentifier();

        String value = "foobar";
        for(int i = 0 ; i < 1000; i++) {
            String maskedValue = maskingProvider.mask(value);
            assertTrue(identifier.isOfThisType(maskedValue));
        }
    }
}

