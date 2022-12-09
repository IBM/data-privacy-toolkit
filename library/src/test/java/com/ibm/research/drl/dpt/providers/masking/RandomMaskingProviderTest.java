/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author santonat
 */
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
