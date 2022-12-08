/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class URLIdentifierTest {
    @Test
    public void testIsOfThisType() throws Exception {

        URLIdentifier identifier = new URLIdentifier();

        String[] validURLs = {
                "www.google.com",
                "mail.google.com",
                "http://www.nba.com",
                "http://www.nba.co.uk",
                "https://www.nba.com",
                "http://www.nba.com/index.html",
                "http://www.nba.com/index.html?q=MichaelJordan",
                "http://www.nba.com:8080",
                "http://22.33.44.55",
                "http://22.33.44.55:8080",
                "https://22.33.44.55:8080",
                "http://username@test.com",
                "https://username@test.com",
                "http://username:password@test.com",
                "https://username:password@test.com",
                "http://[2001:db8:1f70::999:de8:7648:6e8]/index.html",
                "http://[2001:db8:1f70::999:de8:7648:6e8]:100/",
                "http://www.w3.org/TR/html4/strict.dtd"
        };

        for(String validURL: validURLs) {
            assertTrue(identifier.isOfThisType(validURL), validURL);
        }

        String[] invalidURLs = {
                "xyzw",
                "https://w3.ibm.com:443/help or download the Help@IBM mobile app for iOS from",
                "http://www.foo.com\r"
        };

        for (String invalidURL : invalidURLs) {
            assertFalse(identifier.isOfThisType(invalidURL), invalidURL);
        }
    }
}
