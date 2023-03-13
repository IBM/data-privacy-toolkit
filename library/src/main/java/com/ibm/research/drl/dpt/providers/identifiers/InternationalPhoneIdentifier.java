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


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InternationalPhoneIdentifier extends AbstractIdentifier {
    private static final Pattern pattern = Pattern.compile("^((\\+|00|011)\\s?(?<countrycode>\\d{1,3})([-|\\s]*)?(?<number>(\\(\\d{1,4}\\))?([-|\\s]*)?(\\d{1,4}([-|\\s]*)?){1,}))$");
    private final Set<String> validCountryCodes;

    public InternationalPhoneIdentifier() {
        this.validCountryCodes = loadFromResource("/identifier/common/phone_country_codes.csv");
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
        return 18;
    }

    @Override
    public boolean isOfThisType(String data) {
        int dataLength = data.length();

        if (dataLength < getMinimumLength() || dataLength > getMaximumLength()) {
            return false;
        }

        char firstChar = data.charAt(0);
        if (firstChar != '+' && firstChar != '0') {
            return false;
        }

        if (countSpacesAndDashes(data) == 0) {
            long numberOfDigits = NumberUtils.countDigits(data);
            if (numberOfDigits < 10 || numberOfDigits > 15) {
                return false;
            }

            if (data.startsWith("+")) {
                data = data.substring(1);
            } else if (data.startsWith("00")) {
                data = data.substring(2);
            }

            if (containsIllegalCharacters(data)) {
                return false;
            }

            if (data.length() < 4) {
                return false;
            }

            String prefix2 = data.substring(0, 2);
            String prefix3 = data.substring(0, 3);

            return isValidCountryCode(prefix2) || isValidCountryCode(prefix3);
        }

        Matcher matcher = pattern.matcher(data);

        boolean matches = matcher.matches();
        if (!matches) {
            return false;
        }

        String countryCode = matcher.group("countrycode");

        if (!isValidCountryCode(countryCode)) {
            return false;
        }

        String number = matcher.group("number");

        return NumberUtils.countDigits(number) <= 15;
    }

    private boolean containsIllegalCharacters(String data) {
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);

            if (Character.isDigit(ch) || Character.isWhitespace(ch) || ch == '-') {
                continue;
            }

            return true;
        }

        return false;
    }

    private int countSpacesAndDashes(String data) {
        int counter = 0;

        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);

            if (Character.isWhitespace(ch) || ch == '-') {
                counter++;
            }
        }

        return counter;
    }

    private boolean isValidCountryCode(String countryCode) {
        return this.validCountryCodes.contains(countryCode);
    }

    @Override
    public String getDescription() {
        return "International phone number identification";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.PHONE;
    }

    private Set<String> loadFromResource(String resourceName) {
        try (InputStream inputStream = InternationalPhoneIdentifier.class.getResourceAsStream(resourceName)) {
            CsvMapper mapper = new CsvMapper().enable(CsvParser.Feature.WRAP_AS_ARRAY);

            MappingIterator<String[]> termsIterator = mapper.readerFor(
                    String[].class
            ).with(CsvSchema.emptySchema().withoutHeader()).readValues(inputStream);

            return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(
                                    termsIterator,
                                    Spliterator.ORDERED
                            ), true).
                    map(s -> s[0].trim()).
                    filter(((Predicate<String>) String::isEmpty).negate()).
                    collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Unable to load " + resourceName, e);
        }
    }
}
