package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrenchNationalIDIdentifier extends AbstractIdentifier {
    // http://resoo.org/docs/_docs/regles-numero-insee.pdf
    private static final Pattern pattern = Pattern.compile(//"([12]\\s?\\d{2}\\s?(?:0[1-9]|1[0-2]|2[0-9])\\s?(?:\\d{2}|\\d[a-zA-Z]|\\d{3})\\s?(?:\\d{3}|\\d{2})\\d{3})\\s?(0[1-9]|[1-8]\\d|9[0-7])");
            "([12]\\s?\\d{2}\\s?(?:0[1-9]|1[0-2]|3[1-9]|4[0-2]|2[0-9]|3[0-9]|50)\\s?[0-9a-zA-Z]{5}\\s?\\d{3})\\s?(\\d{2})"
    );

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("FRANCE_INSEE");
    }

    @Override
    public boolean isOfThisType(String data) {
        data = data.strip();

        if (data.length() < getMinimumLength() || data.length() > getMaximumLength())
            return false;

        Matcher matcher = pattern.matcher(data);

        if (matcher.matches()) {
            return this.isCheckDigitCorrect(matcher.group(1), matcher.group(2));
        }

        return false;
    }

    private boolean isCheckDigitCorrect(String data, String parityString) {
        final int parity;

        try {
            parity = Integer.parseInt(parityString, 10);
        } catch (NumberFormatException exception) {
            return false;
        }

        long number = 0;

        for (int i = 0; i < data.length(); ++i) {
            if (!Character.isDigit(data.charAt(i))) continue;

            int digit = data.charAt(i) - '0';

            number *= 10;
            number += digit;
        }
        
        number %= 97;
        
        if (number == 0) {
            number = 97;
        }
        
        return number == parity;
    }

    @Override
    public String getDescription() {
        return "France National identification number (INSEE)";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return 0;
    }

    @Override
    public int getMinimumLength() {
        return 15;
    }

    @Override
    public int getMaximumLength() {
        return 20;
    }
}
