package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GermanTaxIdentificationNumberIdentifier extends AbstractIdentifier  {
    private static final Pattern pattern = Pattern.compile("([1-9]\\d{9})(\\d)");
    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("GERMAN_TIN");
    }

    @Override
    public boolean isOfThisType(String data) {
        if (data.length() == getMaximumLength()) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.matches()) {
                return this.checkLastDigit(matcher.group(1), matcher.group(2));
            }
        }
        return false;
    }

    private boolean checkLastDigit(String firstTenDigits, String parityString) {
        if (firstTenDigits.length() != 10) return false;

        final int checkDigit;
        try {
            checkDigit = Integer.parseInt(parityString, 10);
        } catch (NumberFormatException exception) {
            return false;
        }

        int product = 10;

        for (int i = 0; i < firstTenDigits.length(); ++i) {
            int digit = firstTenDigits.charAt(i) - '0';

            int sum = (digit + product) % 10;

            if (sum == 0) {
                sum = 10;
            }

            product = (sum * 2) % 11;
        }

        int validation = (11 - product) % 10;

        return checkDigit == validation;
    }

    @Override
    public String getDescription() {
        return "Identifier for the German Tax Identification Number (TIN)";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 11;
    }

    @Override
    public int getMaximumLength() {
        return 11;
    }
}
