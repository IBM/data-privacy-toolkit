/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItalianFiscalCodeIdentifier extends AbstractIdentifier {
    private final static Collection<String> appropriateNames = Arrays.asList(
            "codice fiscale",
            "codice-fiscale"
    );

    private final static Pattern pattern = Pattern.compile(
            "[a-z]{3}" + // surname
                    "\\s*" +
                    "[a-z]{3}" + // first surname
                    "\\s*" +
                    "\\d{2}" +  // year of birth
                    "([abcdehlmprst])" + // month of birth
                    "([0-7][0-9])" + // day of birth
                    "\\s*" +
                    "([a-z][0-9]{3})" + // town of birth
                    "\\s*" +
                    "[a-z]" // checksum
            ,
            Pattern.CASE_INSENSITIVE);
    private final Map<Character, Integer> oddCharacters = buildOddCharacterMap();
    private final Map<Character, Integer> evenCharacters = buildEvenCharacterMap();

    private Map<Character, Integer> buildOddCharacterMap() {
        Map<Character, Integer> map = new HashMap<>();

        map.put('0', 1);
        map.put('1', 0);
        map.put('2', 5);
        map.put('3', 7);
        map.put('4', 9);
        map.put('5', 13);
        map.put('6', 15);
        map.put('7', 17);
        map.put('8', 19);
        map.put('9', 21);
        map.put('A', 1);
        map.put('B', 0);
        map.put('C', 5);
        map.put('D', 7);
        map.put('E', 9);
        map.put('F', 13);
        map.put('G', 15);
        map.put('H', 17);
        map.put('I', 19);
        map.put('J', 21);
        map.put('K', 2);
        map.put('L', 4);
        map.put('M', 18);
        map.put('N', 20);
        map.put('O', 11);
        map.put('P', 3);
        map.put('Q', 6);
        map.put('R', 8);
        map.put('S', 12);
        map.put('T', 14);
        map.put('U', 16);
        map.put('V', 10);
        map.put('W', 22);
        map.put('X', 25);
        map.put('Y', 24);
        map.put('Z', 23);

        return map;
    }

    private Map<Character, Integer> buildEvenCharacterMap() {
        Map<Character, Integer> map = new HashMap<>();

        for (int i = 0; i < 10; ++i) {
            map.put((char) ('0' + i), i);
        }

        for (int i = 0; i < 26; ++i) {
            map.put((char) ('A' + i), i);
        }

        return map;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("ITALIAN_FISCAL_CODE");
    }

    @Override
    public boolean isOfThisType(String data) {
        Matcher matcher = pattern.matcher(data);

        if (matcher.matches()) {
            String month = matcher.group(1);
            int day = Integer.parseInt(
                    matcher.group(2)
            );

            if (isDayValid(day, month)) {
                return isChecksumValid(data);
            }
        }
        return false;
    }

    private boolean isChecksumValid(String data) {
        data = data.toUpperCase().trim();

        int checksum = 0;

        int position = 1;

        for (int i = 0; i < data.length(); ++i) {
            if (position == 16) break;
            char character = data.charAt(i);

            if (!Character.isLetterOrDigit(character)) continue;

            Integer value;
            if (0 == position % 2) {
                value = evenCharacters.get(character);
            } else {
                value = oddCharacters.get(character);
            }

            if (null == value) {
                return false; // invalid character which is still letter or digit
            }
            checksum += value;

            position += 1;
        }

        // find last value
        char checksumCharacter = data.charAt(data.length() - 1);

        return checksumCharacter == (
                (char) ('A' + (checksum % 26))
        );
    }

    private boolean isDayValid(int day, String month) {
        if (day > 40) {
            day -= 40;
        }

        return day > 0 && day < 31;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return appropriateNames;
    }

    @Override
    public String getDescription() {
        return "Italian Fiscal code (Codice Fiscale)";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA | CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 16;
    }

    @Override
    public int getMaximumLength() {
        return 20;
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }
}
