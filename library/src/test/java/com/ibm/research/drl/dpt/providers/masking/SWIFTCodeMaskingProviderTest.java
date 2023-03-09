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
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.SWIFTCodeIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SWIFTCodeMaskingProviderTest {

    @Test
    public void testDefault() {
        MaskingProvider maskingProvider = new SWIFTCodeMaskingProvider();
        Identifier identifier = new SWIFTCodeIdentifier();

        String key = "EMCRGRA1";
        String maskedValue = maskingProvider.mask(key);
        assertTrue(identifier.isOfThisType(maskedValue));
    }

    @Test
    public void testPreserveCountry() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("swift.mask.preserveCountry", true);
        MaskingProvider maskingProvider = new SWIFTCodeMaskingProvider(maskingConfiguration);
        Identifier identifier = new SWIFTCodeIdentifier();

        String key = "EMCRGRA1";
        String countryCodeOriginal = key.substring(4, 6);
        String maskedValue = maskingProvider.mask(key);
        String countryCodeMasked = maskedValue.substring(4, 6);

        System.out.println(maskedValue);
        assertTrue(identifier.isOfThisType(maskedValue));
        assertEquals(countryCodeOriginal, countryCodeMasked);
    }
}

