/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MonetaryMaskingProviderTest {

    @Test
    public void testReplaceCorrectly() throws Exception {
        MonetaryMaskingProvider mp = new MonetaryMaskingProvider(new DefaultMaskingConfiguration());

        assertEquals(mp.mask("$123.00"), "$XXX.XX");
        assertEquals(mp.mask("123.45 euros"), "XXX.XX euros");
    }

}