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
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.EmailIdentifier;
import com.ibm.research.drl.dpt.util.DomainUtils;
import com.ibm.research.drl.dpt.util.RandomGenerators;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.Set;

/**
 * The type Email masking provider.
 */

public class EmailMaskingProvider extends AbstractComplexMaskingProvider<String> {
    /** Logger for this class. */
    private static final Logger log = LogManager.getLogger(EmailMaskingProvider.class);

    /** Shared email identifier instance. */
    private static final EmailIdentifier emailIdentifier = new EmailIdentifier();
    /** Number of domain levels to preserve in the masked address. */
    private final int preserveDomains;
    /** Whether to generate a name-based username when randomising. */
    private final boolean nameBasedUsername;
    /** Virtual field name for the username component masking provider. */
    private final String usernameVirtualField;
    /** Virtual field name for the domain component masking provider. */
    private final String domainVirtualField;
    /** Masking provider for the username component. */
    private final MaskingProvider usernameVirtualFieldMaskingProvider;
    /** Masking provider for the domain component. */
    private final MaskingProvider domainVirtualFieldMaskingProvider;
    /** Number of IP subnet octets to preserve when masking a domain. */
    private final int preserveSubnets;
    /** The configured failure mode. */
    private final int failMode;


    /**
     * Constructs an EmailMaskingProvider with default configuration.
     *
     * @param factory the masking provider factory
     */
    public EmailMaskingProvider(MaskingProviderFactory factory) {
        this("name", new DefaultMaskingConfiguration(), Collections.emptySet(), factory);
    }

    /**
     * Constructs an EmailMaskingProvider with the given configuration and factory.
     *
     * @param maskingConfiguration the masking configuration
     * @param factory              the masking provider factory
     */
    public EmailMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("name", maskingConfiguration, Collections.emptySet(), factory);
    }

    /**
     * Constructs an EmailMaskingProvider.
     *
     * @param complexType  the complex type name
     * @param configuration the masking configuration
     * @param maskedFields  the set of already-masked fields
     * @param factory       the masking provider factory
     */
    public EmailMaskingProvider(String complexType, MaskingConfiguration configuration, Set<String> maskedFields, MaskingProviderFactory factory) {
        super(complexType, configuration, maskedFields, factory);

        this.preserveDomains = configuration.getIntValue("email.preserve.domains");
        this.nameBasedUsername = configuration.getBooleanValue("email.nameBasedUsername");
        this.usernameVirtualField = configuration.getStringValue("email.usernameVirtualField");
        this.domainVirtualField = configuration.getStringValue("email.domainVirtualField");
        this.failMode = configuration.getIntValue("fail.mode");

        this.usernameVirtualFieldMaskingProvider = (this.usernameVirtualField != null) ?
                getMaskingProvider(this.usernameVirtualField, getConfigurationForSubfield(this.usernameVirtualField, configuration), this.factory) : null;
        this.domainVirtualFieldMaskingProvider = (this.domainVirtualField != null) ?
                getMaskingProvider(this.domainVirtualField, getConfigurationForSubfield(this.domainVirtualField, configuration), this.factory) : null;

        this.preserveSubnets = getConfigurationForSubfield(this.domainVirtualField, configuration).getIntValue("ipaddress.subnets.preserve");

    }

    private String randomizeUsernamePart() {
        if (nameBasedUsername) {
            return RandomGenerators.buildNameBasedUsername();
        }
        return RandomGenerators.randomUsernameAndDomain();
    }

    private String buildEmail(String username, String maskedDomain) {
        String builder = username + "@" +
                maskedDomain;
        return builder;
    }

    @Override
    public String mask(String identifier) {
        String domain;
        String originalUsername;

        if (!emailIdentifier.isOfThisType(identifier)) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.GENERATE_RANDOM:
                    domain = RandomGenerators.randomUIDGenerator(8) + "." + RandomGenerators.getRandomTLD();
                    return buildEmail(RandomGenerators.randomUsernameAndDomain(), domain);
                case FailMode.THROW_ERROR:
                    log.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }

        }

        int index = identifier.indexOf('@');
        originalUsername = identifier.substring(0, index);
        domain = identifier.substring(index + 1);

        String username = (this.usernameVirtualFieldMaskingProvider != null) ?
                this.usernameVirtualFieldMaskingProvider.mask(originalUsername) : randomizeUsernamePart();

        String maskedDomain;

        if (this.domainVirtualFieldMaskingProvider != null) {
            Tuple<String, String> domainParts = DomainUtils.splitDomain(domain, this.preserveDomains, this.preserveSubnets);
            String hostname = domainParts.getFirst();
            String tld = domainParts.getSecond();

            maskedDomain = this.domainVirtualFieldMaskingProvider.mask(hostname);
            if (!tld.isEmpty()) {
                maskedDomain += "." + tld;
            }
        } else {
            maskedDomain = RandomGenerators.randomHostnameGenerator(domain, this.preserveDomains);
        }

        return buildEmail(username, maskedDomain);
    }

}
