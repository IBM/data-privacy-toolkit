/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class TLDManager {
    private static final TLDManager instance = new TLDManager();
    SecureRandom random;
    private final String[] tlds = {"com", "org", "edu", "co.uk"};
    private final Set<String>[] tldSet = (Set<String>[]) new HashSet[256];

    private TLDManager() {
        this.random = new SecureRandom();

        for (int i = 0; i < 256; i++) {
            tldSet[i] = new HashSet<>();
        }

        buildList();
    }

    /**
     * Instance tld manager.
     *
     * @return the tld manager
     */
    public static TLDManager instance() {
        return instance;
    }

    private void buildList() {
        ResourceEntry filename = LocalizationManager.getInstance().getResources(Resource.PUBLIC_SUFFIX_LIST).iterator().next();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(filename.createStream()));

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("//")) {
                    continue;
                }

                int index = line.charAt(0);
                if (index > 255) {
                    index = 0;
                }

                tldSet[index].add(line);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException("error building TLD list");
        }
    }

    /**
     * Gets tld.
     *
     * @param hostname the hostname
     * @return the tld
     */
    public String getTLD(String hostname) {
        int r = 0;

        if (hostname == null || hostname.isEmpty()) {
            return hostname;
        }

        do {
            int index = Character.toLowerCase(hostname.charAt(0));
            if (index > 255) {
                index = 0;
            }
            if (tldSet[index].contains(hostname.toLowerCase())) {
                return hostname;
            }

            r = hostname.indexOf('.', r);
            if (r != -1) {
                hostname = hostname.substring(r + 1);
                if (hostname.isEmpty()) {
                    return null;
                }
            }
        } while (r > 0);

        return null;
    }

    /**
     * Gets random tld.
     *
     * @return the random tld
     */
    public String getRandomTLD() {
        return this.tlds[random.nextInt(tlds.length)];
    }

    /**
     * Gets random tld.
     *
     * @param exception the exception
     * @return the random tld
     */
    public String getRandomTLD(String exception) {
        String res = this.tlds[random.nextInt(tlds.length)];
        if (res.equals(exception)) {
            return getRandomTLD(exception);
        }

        return res;
    }
}
