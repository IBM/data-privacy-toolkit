/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ProviderTypeTest {

    @Test
    public void testFriendlyName() {
        String friendlyName = ProviderType.CITY.getFriendlyName();
        assertEquals("City", friendlyName);
    }

    @Test
    public void testPublicValues() {
        Collection<ProviderType> providerTypes = ProviderType.publicValues();
        assertFalse(providerTypes.contains(ProviderType.EMPTY));
        assertFalse(providerTypes.contains(ProviderType.UNKNOWN));
    }

    @Test
    public void serialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        for (ProviderType type : ProviderType.publicValues()) {
            String s = mapper.writeValueAsString(type);

            assertNotNull(s);
        }
    }
}
