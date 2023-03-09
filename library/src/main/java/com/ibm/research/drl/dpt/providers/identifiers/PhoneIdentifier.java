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
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.MSISDNManager;
import com.ibm.research.drl.dpt.models.PhoneNumber;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ibm.research.drl.dpt.util.NumberUtils.countDigits;

public class PhoneIdentifier extends AbstractIdentifier implements IdentifierWithOffset {
    private static final String[] appropriateNames = {
            "Phone Number", "Mobile",
            "Mobile Number", "Telephone", "Tel.",
            "Fax"
    };

    /**
     * The constant msisdnManager.
     */
    private static final MSISDNManager msisdnManager = MSISDNManager.getInstance();

    private static final String textualPrefix = "(?:(?:Pgr|Ph|Fax|Phone|Contact|Mobile(?:\\s*No.)?):?\\s*#?)";
    private static final String prefix = "(?:" +
            "\\(?" +
            "(?<prefix>\\+|00)" +
            "(?<countryCode>\\d{1,3})" +
            "\\)?" +
            ")";
    private static final String separator = "(?<separator>[- ])";

    private final Pattern[] phonePatterns = {
            Pattern.compile(
                    textualPrefix + "?" +
                            "("
                            + prefix + "?" + separator + "?(?:\\(?(?<areaCode>\\d{3})\\)?[ -]?)?" +
                            "(?<number>[0-9]+(-?[0-9]{2,}){3,})" +
                            ")(?:-mobile)?", Pattern.CASE_INSENSITIVE),
    };

    @Override
    public ProviderType getType() {
        return ProviderType.PHONE;
    }


    private PhoneNumber parseUSNumber(String identifier) {
        String prefix = "+";
        String countryCode = "1";
        String separator = "-";

        String areaCode = identifier.substring(0, 3);
        String idn = identifier.substring(3);

        return new PhoneNumber(prefix, countryCode, separator, idn, areaCode, false);
    }

    /**
     * Gets phone number.
     *
     * @param identifier the identifier
     * @return the phone number
     */
    public PhoneNumber getPhoneNumber(String identifier) {
        Matcher matcher = getMatchingPattern(identifier);
        if (matcher == null) {
            if (msisdnManager.isValidUSNumber(identifier)) {
                return parseUSNumber(identifier);
            }

            return null;
        } else {
            if (identifier.length() < 6) {
                return null;
            }

            String prefix = matcher.group("prefix");

            String countryCode = matcher.group("countryCode");
            if (null == countryCode) countryCode = "1";

            String separator = matcher.group("separator");
            if (null == separator) separator = "";

            String areaCode = matcher.group("areaCode");
            String number = matcher.group("number");

            return new PhoneNumber((null == prefix ? "+" : prefix), countryCode, separator, number, areaCode, null != prefix);
        }
    }

    /**
     * Gets matching pattern.
     *
     * @param data the data
     * @return the matching pattern
     */
    private Matcher getMatchingPattern(String data) {
        if (data.length() < 6) {
            return null;
        }

        for (Pattern pattern : phonePatterns) {
            final Matcher m = pattern.matcher(data);
            if (!m.matches()) {
                continue;
            }

            return m;
        }

        return null;
    }

    @Override
    public boolean isOfThisType(String data) {
        return isOfThisTypeWithOffset(data).getFirst();
    }

    @Override
    public String getDescription() {
        return "Phone number identification. Supports international formats with country code detection";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data) {

        if (data.length() > 64) {
            return new Tuple<>(false, null);
        }

        final long numberOfDigits = countDigits(data);

        if (numberOfDigits < 7 || numberOfDigits > 15) {
            return new Tuple<>(false, null);
        }

        return Arrays.stream(phonePatterns).map(
                        pattern -> {
                            return pattern.matcher(data);
                        }
                ).filter(Matcher::matches).
                filter(this::limitLength).
                map(
                        matcher -> new Tuple<>(true, new Tuple<>(matcher.start(1), matcher.end(1) - matcher.start(1)))
                ).reduce((a, b) -> a).orElse(new Tuple<>(false, null));
    }

    private boolean limitLength(Matcher matcher) {
        final long numberOfDigits = countDigits(matcher.group(0));
        return 7 <= numberOfDigits && numberOfDigits <= 15;
    }


    @Override
    public boolean isPOSIndependent() {
        return true;
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 5;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}
