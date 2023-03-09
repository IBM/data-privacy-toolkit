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
import com.ibm.research.drl.dpt.managers.MSISDNManager;
import com.ibm.research.drl.dpt.models.PhoneNumber;
import com.ibm.research.drl.dpt.providers.identifiers.PhoneIdentifier;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class PhoneMaskingProvider implements MaskingProvider {
    private final static PhoneIdentifier phoneIdentifier = new PhoneIdentifier();
    private static final MSISDNManager msisdnManager = MSISDNManager.getInstance();
    private final boolean preserveCountryCode;
    private final boolean preserveAreaCode;
    private final SecureRandom random;

    /**
     * Instantiates a new Phone masking provider.
     */
    public PhoneMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Phone masking provider.
     *
     * @param configuration the configuration
     */
    public PhoneMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Phone masking provider.
     *
     * @param random the random
     */
    public PhoneMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Phone masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public PhoneMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveCountryCode = configuration.getBooleanValue("phone.countryCode.preserve");
        this.preserveAreaCode = configuration.getBooleanValue("phone.areaCode.preserve");
    }

    private String generateRandomPhoneNumber() {
        String countryCode = msisdnManager.getRandomCountryCode();
        String separator = "-";
        String phoneNumber = RandomGenerators.generateRandomDigitSequence(10);

        return "+" + countryCode +
                separator +
                phoneNumber;
    }

    @Override
    public String mask(String identifier) {
        return oldMask(identifier);
    }

    private String oldMask(String identifier) {
        PhoneNumber phoneNumber = phoneIdentifier.getPhoneNumber(identifier);
        if (phoneNumber == null) {
            return generateRandomPhoneNumber();
        }

        String countryCode = null;
        if (this.preserveCountryCode) {
            countryCode = phoneNumber.getCountryCode();
        } else {
            countryCode = msisdnManager.getRandomCountryCode();
        }

        String areaCode;
        if (this.preserveAreaCode) {
            areaCode = phoneNumber.getAreaCode();
        } else {
            StringCharacterIterator acIterator = new StringCharacterIterator(phoneNumber.getAreaCode());
            StringBuilder randomAreaCode = new StringBuilder();

            for (char c = acIterator.first(); c != CharacterIterator.DONE; c = acIterator.next()) {
                randomAreaCode.append(RandomGenerators.randomDigit());
            }

            areaCode = randomAreaCode.toString();
        }

        StringBuilder builder = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(phoneNumber.getNumber());

        for (char c = iterator.first(); CharacterIterator.DONE != c; c = iterator.next()) {
            if (Character.isDigit(c)) {
                c = (char) ('0' + random.nextInt(10));
            }
            builder.append(c);
        }

        if (phoneNumber.isHasPrefix()) {
            return phoneNumber.getPrefix() + countryCode +
                    phoneNumber.getSeparator() +
                    areaCode +
                    builder;
        }

        return areaCode + builder;
    }
}
