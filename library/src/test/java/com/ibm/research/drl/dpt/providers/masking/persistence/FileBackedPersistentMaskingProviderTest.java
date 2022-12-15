/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.EmailMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FileBackedPersistentMaskingProviderTest {
    @Test
    public void testPersistence() {

        String email1 = "joedoe1@foo.com";
        String email2 = "joedoe2@foo.com";

        MaskingProvider emailMaskingProvider = new EmailMaskingProvider(new MaskingProviderFactory());

        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("persistence.file", "/tmp");
        configuration.setValue("persistence.namespace", "AAAA");

        FileBackedPersistentMaskingProvider provider = new FileBackedPersistentMaskingProvider(emailMaskingProvider, configuration);

        String maskedEmail1_once = provider.mask(email1);
        String maskedEmail1_twice = provider.mask(email1);
        assertEquals(maskedEmail1_once, maskedEmail1_twice);

        String maskedEmail2_once = provider.mask(email2);
        assertNotEquals(maskedEmail2_once, maskedEmail1_once);
    }
}
