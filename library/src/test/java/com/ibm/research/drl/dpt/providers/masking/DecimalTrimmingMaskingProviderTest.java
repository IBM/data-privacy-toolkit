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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecimalTrimmingMaskingProviderTest {
    
    @Test
    public void testParseRules() {
        List<DecimalTrimmingMaskingProvider.DecimalTrimmingRule> rules = DecimalTrimmingMaskingProvider.parseRules("0,10,2;10,20,1");
        assertEquals(2, rules.size());
        
        assertEquals(0.0, rules.get(0).getLowerThreshold(), 0.000000001);
        assertEquals(10.0, rules.get(0).getUpperThreshold(), 0.000000001);
        assertEquals(2, rules.get(0).getDigitsToKeep());
        
        assertEquals(10.0, rules.get(1).getLowerThreshold(), 0.000000001);
        assertEquals(20.0, rules.get(1).getUpperThreshold(), 0.000000001);
        assertEquals(1, rules.get(1).getDigitsToKeep());
    }
   
    @Test
    public void testOutsideRules() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("decimalrounding.mask.rules", "0,10,2;10,20,1");

        DecimalTrimmingMaskingProvider maskingProvider = new DecimalTrimmingMaskingProvider(maskingConfiguration);

        String value = "25.323";
        assertEquals(value, maskingProvider.mask(value));
    }

    @Test
    public void testZeroDigits() {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("decimalrounding.mask.rules", "0,10,0;10,20,1");

        DecimalTrimmingMaskingProvider maskingProvider = new DecimalTrimmingMaskingProvider(maskingConfiguration);

        String value = "5.323";
        assertEquals("5", maskingProvider.mask(value));
    }
    
    @Test
    public void testWithinTheRules() {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("decimalrounding.mask.rules", "0,10,2;10,20,1");
        
        DecimalTrimmingMaskingProvider maskingProvider = new DecimalTrimmingMaskingProvider(maskingConfiguration);

        String value = "5.323";
        assertEquals("5.32", maskingProvider.mask(value));
       
        value = "5";
        assertEquals("5", maskingProvider.mask(value));
        
        value = "5.3";
        assertEquals("5.3", maskingProvider.mask(value));
        
        value = "5.32";
        assertEquals("5.32", maskingProvider.mask(value));

        
        value = "10.0";
        assertEquals("10.0", maskingProvider.mask(value));
        
        value = "10.01";
        assertEquals("10.0", maskingProvider.mask(value));

        value = "18.00000001";
        assertEquals("18.0", maskingProvider.mask(value));
    }

}
