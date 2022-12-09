/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.managers.TLDManager;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class DomainUtils {

    private static final IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();
    private static final TLDManager tldManager = TLDManager.instance();

    public static Tuple<String, String> splitIPV4Address(String address, int preserveSubnets) {

        if (preserveSubnets <= 0) {
            return new Tuple<>(address, "");
        }

        List<String> parts = Arrays.asList(address.split("\\."));

        int toKeep = Math.min(parts.size(), preserveSubnets);

        return new Tuple<>(
                StringUtils.join(parts.subList(0, parts.size() - toKeep), '.'),
                StringUtils.join(parts.subList(parts.size() - toKeep, parts.size()), '.')
        );
    }

    public static Tuple<String, String> splitDomain(String domain, int preserveDomains) {
        return splitDomain(domain, preserveDomains, 0);
    }

    public static Tuple<String, String> splitDomain(String domain, int preserveDomains, int preserveSubnets) {
        if (preserveDomains <= 0) {
            return new Tuple<>(domain, "");
        }

        if (ipAddressIdentifier.isIPv4(domain)) {
            return splitIPV4Address(domain, preserveSubnets);
        }

        if (ipAddressIdentifier.isIPv6(domain)) {
            return new Tuple<>(domain, "");
        }

        int preserveSubdomains = Math.max(preserveDomains - 1, 0);

        String tld = tldManager.getTLD(domain);

        int idx;
        if (tld == null || (idx = domain.indexOf(tld)) == 0) {
            return new Tuple<>(domain, "");
        }

        domain = domain.substring(0, idx - 1);

        if (preserveSubdomains == 0) {
            return new Tuple<>(domain, tld);
        }

        List<String> parts = Arrays.asList(domain.split("\\."));

        int toKeep = Math.min(parts.size(), preserveSubdomains);

        return new Tuple<>(
                StringUtils.join(parts.subList(0, parts.size() - toKeep), '.'),
                StringUtils.join(parts.subList(parts.size() - toKeep, parts.size()), '.') + "." + tld
        );
    }


}

