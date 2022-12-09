/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;


import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;

import java.security.SecureRandom;
import java.util.Collection;

public class SSNUKManager implements Manager {

    private final static char[] allowedSuffixLetters = "ABCD".toCharArray();
    private final static char[] allowedFirstLetters = "ABCEGHJKLMNOPRSTWXYZ".toCharArray();
    private final static char[] allowedSecondLetters = "ABCEGHJKLMNPRSTWXYZ".toCharArray();

    private static final Collection<ResourceEntry> resourceList =
            LocalizationManager.getInstance().getResources(Resource.SSNUK_PREFIXES);
    private final SecureRandom random;

    private static SSNUKManager instance = new SSNUKManager();

    public static SSNUKManager getInstance() {
        return  instance;
    }

    private SSNUKManager() {
        this.random = new SecureRandom();
    }

    public String getRandomPrefix() {
        String prefix = "" + allowedFirstLetters[random.nextInt(allowedFirstLetters.length)];
        prefix += allowedSecondLetters[random.nextInt(allowedSecondLetters.length)];

        return prefix;
    }

    public String getRandomSuffix() {
        return "" + allowedSuffixLetters[random.nextInt(allowedSuffixLetters.length)];
    }

    @Override
    public boolean isValidKey(String identifier) {
        return false;
    }

    @Override
    public String getRandomKey() {
        return null;
    }
}
