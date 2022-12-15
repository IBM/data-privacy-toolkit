/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
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
    private static final Logger log = LogManager.getLogger(EmailMaskingProvider.class);

    private static final EmailIdentifier emailIdentifier = new EmailIdentifier();
    private final int preserveDomains;
    private final boolean nameBasedUsername;
    private final String usernameVirtualField;
    private final String domainVirtualField;
    private final MaskingProvider usernameVirtualFieldMaskingProvider;
    private final MaskingProvider domainVirtualFieldMaskingProvider;
    private final int preserveSubnets;
    private final int failMode;


    /**
     * Instantiates a new Email masking provider.
     */
    public EmailMaskingProvider(MaskingProviderFactory factory) {
        this("name", new DefaultMaskingConfiguration(), Collections.emptySet(), factory);
    }

    public EmailMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this("name", maskingConfiguration, Collections.emptySet(), factory);
    }

    /**
     * Instantiates a new Email masking provider.
     *
     * @param configuration the configuration
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
