/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IMEIIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IMEIMaskingProviderTest {

    @Test
    public void testMask() {
        IMEIIdentifier identifier = new IMEIIdentifier();
        IMEIMaskingProvider maskingProvider = new IMEIMaskingProvider();

        String imei = "012837001234567";
        String maskedValue = maskingProvider.mask(imei);

        //by default we preserve TAC
        assertEquals(maskedValue.substring(0, 8), imei.substring(0, 8));
        assertTrue(identifier.isOfThisType(maskedValue));
        assertNotEquals(maskedValue, imei);
    }

    @Test
    public void testMaskNoTACPreservation() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("imei.mask.preserveTAC", false);
        IMEIIdentifier identifier = new IMEIIdentifier();
        IMEIMaskingProvider maskingProvider = new IMEIMaskingProvider(configuration);

        String imei = "001013001234568";
        String maskedValue = maskingProvider.mask(imei);

        assertNotEquals(maskedValue.substring(0, 8), imei.substring(0, 8));
        assertTrue(identifier.isOfThisType(maskedValue));
        assertNotEquals(maskedValue, imei);

    }

    @Test
    public void testMaskInvalidValue() {
        IMEIIdentifier identifier = new IMEIIdentifier();
        IMEIMaskingProvider maskingProvider = new IMEIMaskingProvider();

        String imei = "foobar";
        String maskedValue = maskingProvider.mask(imei);

        assertTrue(identifier.isOfThisType(maskedValue), maskedValue);
        assertNotEquals(maskedValue, imei);
    }
}
