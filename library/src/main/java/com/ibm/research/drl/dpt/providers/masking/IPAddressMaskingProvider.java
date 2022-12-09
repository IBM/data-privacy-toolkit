/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import com.ibm.research.drl.dpt.util.RandomGenerators;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.security.SecureRandom;

public class IPAddressMaskingProvider extends AbstractMaskingProvider {
    private final static Logger logger = LogManager.getLogger(IPAddressMaskingProvider.class);

    private final static IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();
    private final int preservedPrefixes;
    private final int failMode;

    /**
     * Instantiates a new Ip address masking provider.
     */
    public IPAddressMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Ip address masking provider.
     *
     * @param random the random
     */
    public IPAddressMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Ip address masking provider.
     *
     * @param configuration the configuration
     */
    public IPAddressMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Ip address masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public IPAddressMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preservedPrefixes = configuration.getIntValue("ipaddress.subnets.preserve");
        this.failMode = configuration.getIntValue("fail.mode");

        if (this.preservedPrefixes < 0 || this.preservedPrefixes > 4) {
            String msg = "ipaddress.subnets.preserve must be between 0 and 4 (inclusive)";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private String randomSubnet() {
        int subnetAsInt = random.nextInt(256);
        return Integer.toString(subnetAsInt);
    }

    private String ipv4mask(String identifier) {
        String[] parts = identifier.split("\\.");
        String[] maskedParts = new String[4];

        if (this.preservedPrefixes >= 4) {
            return identifier;
        }

        for(int i=0; i<parts.length; i++) {
            if(i < this.preservedPrefixes) {
                maskedParts[i] = parts[i];
            }
            else {
                maskedParts[i] = randomSubnet();
            }
        }

        return StringUtils.join(maskedParts, '.');
    }

    private String ipv6mask(String identifier) {

        String suffix = "";

        int idx = identifier.indexOf('%');
        if (idx > 0) {
            suffix = identifier.substring(idx);
            identifier = identifier.substring(0, idx);
        }

        String[] parts = identifier.split(":", -1);
        String[] maskedParts = new String[parts.length];

        for(int i=0; i<parts.length; i++) {
            String part = parts[i];

            if (part.isEmpty()) {
                maskedParts[i] = "";
            }
            else if (ipAddressIdentifier.isIPv4(part)) {
                maskedParts[i] = ipv4mask(part);
            }
            else {
                maskedParts[i] = RandomGenerators.randomHexSequence(Math.max(part.length() / 2, 1) );
            }

        }

        return StringUtils.join(maskedParts, ':');
    }


    /**
     * Direct mask string.
     *
     * @param identifier the identifier
     * @param isIPv4     the is i pv 4
     * @return the string
     */
/* TODO: find a way to protect this */
    public String directMask(String identifier, boolean isIPv4) {
        if (isIPv4) {
            return ipv4mask(identifier);
        }

        return ipv6mask(identifier);
    }

    @Override
    public String mask(String identifier) {
        if (ipAddressIdentifier.isIPv4(identifier)) {
            return ipv4mask(identifier);
        }
        else if(ipAddressIdentifier.isIPv6(identifier)) {
            return ipv6mask(identifier);
        }

        switch (failMode) {
            case FailMode.RETURN_ORIGINAL:
                return identifier;
            case FailMode.THROW_ERROR:
                logger.error("invalid IP address");
                throw new IllegalArgumentException("invalid IP address");
            case FailMode.RETURN_EMPTY:
                return "";
            case FailMode.GENERATE_RANDOM:
            default:
                return String.format("%d.%d.%d.%d", random.nextInt(255), random.nextInt(255), random.nextInt(255), random.nextInt(255));
        }

    }
}
