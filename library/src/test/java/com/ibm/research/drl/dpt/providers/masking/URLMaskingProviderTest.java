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

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import com.ibm.research.drl.dpt.providers.identifiers.URLIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class URLMaskingProviderTest {

    URLIdentifier identifier = new URLIdentifier();

    public boolean URLsMatch(String u1, String u2) throws MalformedURLException{
        URL url1 = new URL(u1);
        URL url2 = new URL(u2);

        String userinfo1 = url1.getUserInfo();
        String userinfo2 = url2.getUserInfo();

        int port1 = url1.getPort();
        if (port1 == -1) {
            port1 = url1.getDefaultPort();
        }

        int port2 = url2.getPort();
        if (port2 == -1) {
            port2 = url2.getDefaultPort();
        }

        if (userinfo1 == null) {
            userinfo1 = "";
        }

        if (userinfo2 == null) {
            userinfo2 = "";
        }

        String file1 = url1.getFile();
        if (file1.equals("/")) { file1 = ""; }

        String file2 = url2.getFile();
        if (file2.equals("/")) { file2 = ""; }

        return url1.getHost().equals(url2.getHost()) && url1.getProtocol().equals(url2.getProtocol())
                && port1 == port2 && file1.equals(file2) && userinfo1.equals(userinfo2);
    }

    @Test
    public void testDefaultMask() throws Exception {
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));

        String url = "http://www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);

        assertFalse(URLsMatch(url, maskedResult));
        assertTrue(identifier.isOfThisType(maskedResult));
    }

    @Test
    public void testURLWithIPv4Address() throws Exception {
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();

        String url = "http://10.22.33.44";
        String maskedResult = urlMaskingProvider.mask(url);

        assertFalse(URLsMatch(url, maskedResult));
        assertTrue(identifier.isOfThisType(maskedResult));
        String host = new URL(maskedResult).getHost();
        assertTrue(ipAddressIdentifier.isIPv4(host));
    }

    @Test
    public void testURLWithIPv6Address() throws Exception {
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();

        String url = "http://[1::4:5:6:7:8]:100/";
        String maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));
        String host = new URL(maskedResult).getHost();
        assertEquals('[', host.charAt(0));
        assertEquals(']', host.charAt(host.length() - 1));
        assertTrue(ipAddressIdentifier.isIPv6(host.substring(1, host.length()-1)));
    }

    @Test
    public void testNoDomainPreservation() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.usernamePassword", true);
        configuration.setValue("url.mask.port", true);
        configuration.setValue("url.preserve.domains", 0);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));

        // we do not preserve anything
        String url = "http://www.nba.com";
        int domainOK = 0;

        for (int i = 0; i < 100; i++) {
            String maskedResult = urlMaskingProvider.mask(url);
            assertTrue(identifier.isOfThisType(maskedResult));
            assertNotEquals("www.nba.com", new URL(maskedResult).getHost());
            assertFalse(new URL(maskedResult).getHost().startsWith("www.nba."));
            assertFalse(new URL(maskedResult).getHost().endsWith("nba.com"));
            if (!maskedResult.endsWith(".com")) {
                domainOK++;
            }
        }

        assertTrue(domainOK > 0);

        url = "http://www.nba.co.uk";
        domainOK = 0;
        for (int i = 0; i < 100; i++) {
            String maskedResult = urlMaskingProvider.mask(url);
            assertTrue(identifier.isOfThisType(maskedResult));
            assertNotEquals("www.nba.co.uk", new URL(maskedResult).getHost());
            assertFalse(new URL(maskedResult).getHost().startsWith("www.nba."));
            assertFalse(new URL(maskedResult).getHost().endsWith("nba.co.uk"));
            if (!maskedResult.endsWith(".co.uk")) {
                domainOK++;
            }
        }
        assertTrue(domainOK > 0);
    }

    @Test
    public void testTLDIsPreserverd() throws Exception {
        //we preserve TLD
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.usernamePassword", true);
        configuration.setValue("url.mask.port", true);
        configuration.setValue("url.preserve.domains", 1);

        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));
        assertTrue(new URL(maskedResult).getHost().endsWith(".com"));

        url = "http://www.nba.co.uk";
        maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));
        assertTrue(new URL(maskedResult).getHost().endsWith(".co.uk"));
    }

    @Test
    public void testFirstSubdomainIsPreserved() throws Exception {
        //we preserve TLD + first subdomain
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.usernamePassword", true);
        configuration.setValue("url.mask.port", true);
        configuration.setValue("url.preserve.domains", 2);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));
        assertTrue(new URL(maskedResult).getHost().endsWith(".nba.com"));
        assertNotEquals("www.nba.com", new URL(maskedResult).getHost());
    }

    @Test
    public void testPreserveDomainValueLongerThanDomains() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.usernamePassword", true);
        configuration.setValue("url.mask.port", true);
        configuration.setValue("url.preserve.domains", 5);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));
        assertEquals("www.nba.com", new URL(maskedResult).getHost());
    }

    @Test
    public void testUsernamePasswordMask() throws Exception {

        MaskingProvider urlMaskingProvider = new URLMaskingProvider(new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));

        /* we test that both username and passwords get randomized */
        String url = "http://user1:pass1@www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));

        String originalUserInfo = new URL(url).getUserInfo();
        String maskedUserInfo = new URL(maskedResult).getUserInfo();
        assertNotEquals(originalUserInfo, maskedUserInfo);
        assertNotNull(maskedUserInfo);
        assertEquals(2, maskedUserInfo.split(":").length);
        assertNotEquals(maskedUserInfo.split(":")[0], originalUserInfo.split(":")[0]);
        assertNotEquals(maskedUserInfo.split(":")[1], originalUserInfo.split(":")[1]);

        /* we test that username is randomized */
        url = "http://user1@www.nba.com";
        maskedResult = urlMaskingProvider.mask(url);
        assertTrue(identifier.isOfThisType(maskedResult));

        originalUserInfo = new URL(url).getUserInfo();
        maskedUserInfo = new URL(maskedResult).getUserInfo();
        assertEquals(1, maskedUserInfo.split(":").length);
        assertNotEquals(maskedUserInfo.split(":")[0], originalUserInfo.split(":")[0]);
    }

    @Test
    public void testUserInfoFlagOff() throws Exception {
        /* we test that if we turn the flag off the user information is preserved */
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.usernamePassword", false);
        configuration.setValue("url.mask.port", true);
        configuration.setValue("url.preserve.domains", 0);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://user1:pass1@www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);
        String originalUserInfo = new URL(url).getUserInfo();
        String maskedUserInfo = new URL(maskedResult).getUserInfo();
        assertEquals(originalUserInfo, maskedUserInfo);
    }

    @Test
    public void testRandomizePortFlag() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.usernamePassword", false);
        configuration.setValue("url.mask.port", true);
        configuration.setValue("url.preserve.domains", 0);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://www.nba.com";
        String maskedResult = urlMaskingProvider.mask(url);
        URL maskedURL = new URL(maskedResult);
        int maskedPort = maskedURL.getPort();
        assertNotEquals(maskedPort, new URL(url).getDefaultPort());
        assertTrue(maskedPort > 0 && maskedPort < 65536);
    }

    @Test
    public void testRemoveQueryPart() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.removeQuery", true);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://www.nba.com?q=abcd";
        String maskedResult = urlMaskingProvider.mask(url);
        URL maskedURL = new URL(maskedResult);
        String maskedFile = maskedURL.getFile();
        assertTrue(maskedFile.isEmpty() || maskedFile.equals("/"));
    }

    @Test
    public void testMaskQueryPart() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("url.mask.maskQuery", true);
        MaskingProvider urlMaskingProvider = new URLMaskingProvider(configuration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));
        String url = "http://www.nba.com?q=John&q2=foobar&q3=";
        String maskedResult = urlMaskingProvider.mask(url);
        URL maskedURL = new URL(maskedResult);
        String maskedFile = maskedURL.getFile();
        assertNotEquals("?q=John", maskedFile);
    }

    @Test
    public void testMaskInvalidValue() throws Exception {
        URLMaskingProvider urlMaskingProvider = new URLMaskingProvider(new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));

        String url = "junk";

        for (int i = 0; i < 100; i++) {
            String maskedResult = urlMaskingProvider.mask(url);
            assertNotEquals(maskedResult, url);
            assertTrue(identifier.isOfThisType(maskedResult));
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration
        };

        String[] originalValues = new String[]{"http://www.nba.com", "http://user1:pass1@www.nba.com", "http://10.22.33.44"};

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            MaskingProvider maskingProvider = new URLMaskingProvider(maskingConfiguration, new MaskingProviderFactory(new ConfigurationManager(), Collections.emptyMap()));

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %s: %d operations took %d milliseconds (%f per op)%n",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N);
            }
        }
    }
}

