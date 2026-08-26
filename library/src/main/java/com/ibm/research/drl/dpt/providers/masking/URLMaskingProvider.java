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
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.util.RandomGenerators;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;

/** Masking provider for URL values. */
public class URLMaskingProvider extends AbstractComplexMaskingProvider<String> {
    /** Whether to mask the username and password components of the URL. */
    private final boolean maskUsernamePassword;
    /** Whether to randomise the port number. */
    private final boolean randomizePort;
    /** Whether to remove the query string from the URL. */
    private final boolean removeQuery;
    /** Whether to mask the query string components. */
    private final boolean maskQuery;
    /** Number of domain levels to preserve in the masked URL. */
    private final int preserveDomains;
    /** The masking configuration for this provider. */
    private final MaskingConfiguration configuration;
    /** Cache of masking providers keyed by provider type. */
    private final Map<ProviderType, MaskingProvider> providerMap =
            new HashMap<>(ProviderType.values().length);
    /** The masking provider factory. */
    private final MaskingProviderFactory maskingProviderFactory;
    /** Secure random source. */
    private final SecureRandom random;


    /**
     * Constructs a URLMaskingProvider.
     *
     * @param complexType    the complex type name
     * @param configuration  the masking configuration
     * @param maskedFields   the set of fields already masked
     * @param factory        the masking provider factory
     */
    public URLMaskingProvider(String complexType, MaskingConfiguration configuration, Set<String> maskedFields, MaskingProviderFactory factory) {
        super(complexType, configuration, maskedFields, factory);

        this.random = new SecureRandom();
        this.maskUsernamePassword = configuration.getBooleanValue("url.mask.usernamePassword");
        this.randomizePort = configuration.getBooleanValue("url.mask.port");
        this.preserveDomains = configuration.getIntValue("url.preserve.domains");
        this.removeQuery = configuration.getBooleanValue("url.mask.removeQuery");
        this.maskQuery = configuration.getBooleanValue("url.mask.maskQuery");
        this.configuration = configuration;
        this.maskingProviderFactory = factory;
    }


    /**
     * Constructs a URLMaskingProvider with default configuration.
     *
     * @param factory the masking provider factory
     */
    public URLMaskingProvider(MaskingProviderFactory factory) {
        this("url", new DefaultMaskingConfiguration(), Collections.emptySet(), factory);
    }

    /**
     * Constructs a URLMaskingProvider with the given configuration.
     *
     * @param maskingConfiguration the masking configuration
     * @param factory              the masking provider factory
     */
    public URLMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("url", maskingConfiguration, Collections.emptySet(), factory);
    }

    /**
     * Constructs a URLMaskingProvider with an explicit random source.
     *
     * @param random               the secure random source (unused; kept for API compatibility)
     * @param maskingConfiguration the masking configuration
     * @param factory              the masking provider factory
     */
    public URLMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("url", maskingConfiguration, Collections.emptySet(), factory);
    }

    private int randomizePort(int exceptionPort) {
        int ret = random.nextInt(65536);
        if (ret == exceptionPort) {
            return randomizePort(exceptionPort);
        }
        return ret;
    }

    private String randomizeUsernamePassword(String userInfo) {
        final String[] parts = userInfo.split(":");

        if (parts.length == 0) {
            return RandomGenerators.randomUIDGenerator(8);
        }

        final String username = RandomGenerators.randomUIDGenerator(parts[0].length());

        if (parts.length == 2) {
            final String password = RandomGenerators.randomUIDGenerator(8 + random.nextInt(8));

            return username + ":" + password;
        }

        return username;
    }

    private String randomizeHostname(String hostname) {
        /*
           host can be expressed as a host name or a literal IP address. If IPv6 literal
           address is used, it should be enclosed in square brackets ('[' and ']'), as specified by RFC 2732;
         */
        if (hostname == null || hostname.length() == 0) {
            return hostname;
        }

        if (hostname.charAt(0) == '[' && hostname.charAt(hostname.length() - 1) == ']') {
            hostname = hostname.substring(1, hostname.length() - 1);
            return String.format("[%s]", RandomGenerators.randomHostnameGenerator(hostname, 0));
        }

        return RandomGenerators.randomHostnameGenerator(hostname, preserveDomains);
    }


    /**
     * Gets masking provider.
     *
     * @param type the type
     * @return the masking provider
     */
    public synchronized MaskingProvider getMaskingProvider(ProviderType type) {
        if (providerMap.containsKey(type)) {
            return providerMap.get(type);
        }

        MaskingProvider maskingProvider = maskingProviderFactory.get(type, configuration);
        providerMap.put(type, maskingProvider);

        return maskingProvider;
    }

    private String maskQuery(String query) {
        if (query.equals("/") || query.equals("/?") || query.length() == 0) {
            return query;
        }

        Collection<Identifier> identifiers = IdentifierFactory.defaultIdentifiers();

        String[] tokens = query.split("&");
        String[] maskedTokens = new String[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (i == 0) {
                token = token.substring(1);
            }

            String[] parts = token.split("=");
            if (parts.length == 1 || parts[1].isEmpty()) {
                maskedTokens[i] = token;
                continue;
            }

            for (Identifier identifier : identifiers) {
                if (identifier.isOfThisType(parts[1])) {
                    MaskingProvider maskingProvider = getMaskingProvider(identifier.getType());
                    parts[1] = maskingProvider.mask(parts[1]);
                    break;
                }
            }

            maskedTokens[i] = parts[0] + "=" + parts[1];
        }

        return "?" + StringUtils.join(maskedTokens, '&');
    }

    private String buildURLString(String protocol, String host, int port, String file, String userInfo) {
        StringBuilder builder = new StringBuilder(protocol);
        builder.append("://");

        if (userInfo != null) {
            builder.append(userInfo);
            builder.append("@");
        }

        if (host != null) {
            builder.append(host);
        }

        builder.append(":");
        builder.append(port);
        if (!removeQuery) {
            if (file != null) {
                if (!maskQuery) {
                    builder.append(file);
                } else {
                    builder.append(maskQuery(file));
                }
            }
        }
        return builder.toString();
    }

    private String maskURL(URL url) {
        String protocol = url.getProtocol();
        int port = url.getPort();
        String host = url.getHost();
        String filename = url.getFile();

        if (port == -1) {
            port = url.getDefaultPort();
        }

        if (this.randomizePort) {
            port = randomizePort(port);
        }

        host = randomizeHostname(host);

        String userInfo = url.getUserInfo();
        if (userInfo != null && this.maskUsernamePassword) {
            userInfo = randomizeUsernamePassword(userInfo);
        }

        return buildURLString(protocol, host, port, filename, userInfo);

    }

    @Override
    public String mask(String identifier) {
        try {
            URL url = new URL(identifier);
            return maskURL(url);
        } catch (MalformedURLException e) {
            return RandomGenerators.generateRandomURL();
        }
    }
}
