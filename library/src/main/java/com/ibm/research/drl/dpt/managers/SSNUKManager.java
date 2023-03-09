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

    private static final SSNUKManager instance = new SSNUKManager();

    public static SSNUKManager getInstance() {
        return instance;
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
