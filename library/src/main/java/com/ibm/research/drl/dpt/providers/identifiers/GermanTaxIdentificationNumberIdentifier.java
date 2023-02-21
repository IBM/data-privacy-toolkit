package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GermanTaxIdentificationNumberIdentifier extends AbstractIdentifier {
    private static final Pattern pattern = Pattern.compile("([1-9]\\d ?\\d{3} ?\\d{3} ?\\d{2})(\\d)");
    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("GERMAN_TIN");
    }

    @Override
    public boolean isOfThisType(String data) {
        if (data.length() >= getMinimumLength() && data.length() <= getMaximumLength()) {
            Matcher matcher = pattern.matcher(data);

            if (matcher.matches()) {
                if (correctRepetitions(matcher.group(1))) {
                    return this.checkLastDigit(matcher.group(1), matcher.group(2));
                }
            }
        }
        return false;
    }

    private boolean correctRepetitions(String firstTenDigits) {
        Map<Integer, Integer> counts = new HashMap<>(10);

        Integer i_m1 = null;
        Integer i_m2 = null;

        for (int i = 0; i < firstTenDigits.length(); ++i) {
            char currentChar = firstTenDigits.charAt(i);

            if (!Character.isDigit(currentChar)) continue;

            Integer current = currentChar - '0';

            counts.merge(current, 1, Integer::sum);

            if (i_m1 != null && i_m2 != null && i_m1.equals(current) && i_m2.equals(current)) {
                return false; // three consecutive appearances of the same value
            }

            i_m2 = i_m1;
            i_m1 = current;
        }

        boolean found = false;
        for (int count : counts.values()) {
            if (count == 2 || count == 3) {
                if (found) return false;
                found = true;
            }
        }

        return found;
    }

    private boolean checkLastDigit(String firstTenDigits, String parityString) {
        final int checkDigit;
        try {
            checkDigit = Integer.parseInt(parityString, 10);
        } catch (NumberFormatException exception) {
            return false;
        }

        int product = 10;

        for (int i = 0; i < firstTenDigits.length(); ++i) {
            if (!Character.isDigit(firstTenDigits.charAt(i))) continue;

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
        return 14;
    }
}
