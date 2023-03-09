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
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FailModeTest {
    
    @Test
    public void testFailModeOnNumericMaskingProvidersReturnEmpty() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.RETURN_EMPTY);
        
        MaskingProvider[] numericMaskingProviders = new MaskingProvider[] {
                new DecimalTrimmingMaskingProvider(maskingConfiguration),
                new BinningMaskingProvider(maskingConfiguration),
                new NumberVarianceMaskingProvider(maskingConfiguration),
                new ShiftMaskingProvider(maskingConfiguration)
        };
        
        String invalidValue = "abc";
        
        for(MaskingProvider maskingProvider: numericMaskingProviders) {
            assertEquals("", maskingProvider.mask(invalidValue), maskingProvider.getClass().getName());
        }
        
    }

    @Test
    public void testFailModeOnNumericMaskingProvidersReturnOriginal() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.RETURN_ORIGINAL);

        MaskingProvider[] numericMaskingProviders = new MaskingProvider[] {
                new DecimalTrimmingMaskingProvider(maskingConfiguration),
                new BinningMaskingProvider(maskingConfiguration),
                new NumberVarianceMaskingProvider(maskingConfiguration),
                new ShiftMaskingProvider(maskingConfiguration)
        };

        String invalidValue = "abc";

        for(MaskingProvider maskingProvider: numericMaskingProviders) {
            assertEquals(invalidValue, maskingProvider.mask(invalidValue), maskingProvider.getClass().getName());
        }
    }

    @Test
    public void testFailModeOnNumericMaskingProvidersThrowError() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("fail.mode", FailMode.THROW_ERROR);

        MaskingProvider[] numericMaskingProviders = new MaskingProvider[] {
                new DecimalTrimmingMaskingProvider(maskingConfiguration),
                new BinningMaskingProvider(maskingConfiguration),
                new NumberVarianceMaskingProvider(maskingConfiguration),
                new ShiftMaskingProvider(maskingConfiguration)
        };

        String invalidValue = "abc";

        for(MaskingProvider maskingProvider: numericMaskingProviders) {
            try {
                String mask = maskingProvider.mask(invalidValue);
            } catch (RuntimeException e) {
                continue;
            }

            fail("should fail");
        }
    }
}

