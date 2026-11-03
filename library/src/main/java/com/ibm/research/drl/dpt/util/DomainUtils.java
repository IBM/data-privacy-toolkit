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
package com.ibm.research.drl.dpt.util;

import com.ibm.research.drl.dpt.managers.TLDManager;
import com.ibm.research.drl.dpt.providers.identifiers.IPAddressIdentifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/** Utility methods for domain and IP address splitting. */
public class DomainUtils {

    private DomainUtils() {}

    private static final IPAddressIdentifier ipAddressIdentifier = new IPAddressIdentifier();
    private static final TLDManager tldManager = TLDManager.instance();

    /**
     * Splits an IPv4 address, preserving a given number of subnet octets.
     *
     * @param address        the IPv4 address string
     * @param preserveSubnets the number of trailing octets to preserve
     * @return a tuple of (prefix, preserved suffix)
     */
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

    /**
     * Splits a domain, preserving a given number of domain labels.
     *
     * @param domain          the domain string
     * @param preserveDomains the number of domain labels to preserve
     * @return a tuple of (prefix, preserved suffix)
     */
    public static Tuple<String, String> splitDomain(String domain, int preserveDomains) {
        return splitDomain(domain, preserveDomains, 0);
    }

    /**
     * Splits a domain, preserving domain labels and subnet octets.
     *
     * @param domain          the domain string
     * @param preserveDomains the number of domain labels to preserve
     * @param preserveSubnets the number of subnet octets to preserve
     * @return a tuple of (prefix, preserved suffix)
     */
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

