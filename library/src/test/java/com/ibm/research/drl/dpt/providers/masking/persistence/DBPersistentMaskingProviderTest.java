/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class DBPersistentMaskingProviderTest {
    @Test
    @Disabled("Require mocked database, to explore")
    public void testDBCache() throws Exception {
        /*
        DBCache cache = new DBCache();

        String host = "jdbc:postgresql://localhost/postgres";
        String username = "postgres";
        String password = "pipichu123";

        cache.initialize(host, username, password, 0, "foo");

        String originalValue = "foo";
        MaskingProvider maskingProvider = new RandomMaskingProvider();

        String maskedValue_once = cache.getValue(originalValue, maskingProvider);
        assertNotEquals(maskedValue_once, originalValue);
        assertEquals(3, maskedValue_once.length());

        String maskedValue_twice = cache.getValue(originalValue, maskingProvider);

        assertEquals(maskedValue_once, maskedValue_twice);
        */

    }
}