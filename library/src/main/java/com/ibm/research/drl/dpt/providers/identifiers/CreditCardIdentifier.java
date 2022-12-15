/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;


import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class CreditCardIdentifier extends AbstractRegexBasedIdentifier implements LuhnBasedIdentifier {
    private static final String[] appropriateNames = {"CreditCard", "Credit Card", "CCN"};

    private static final Collection<Pattern> combinedPattern = Arrays.asList(
            Pattern.compile("^4\\d{15}$") //visa
            , Pattern.compile("^(?:5[1-5]\\d{2}|" +
                    "222[1-9]|" +
                    "22[3-9]\\d|" +
                    "2[3-6]\\d{2}|" +
                    "27[01]\\d|" +
                    "2720)\\d{12}$") //MasterCard
            , Pattern.compile("^3[47]\\d{13}") // AMEX
            , Pattern.compile("^3(?:0[0-5]|[68]\\d)\\d{11}$") // Diners Club
            , Pattern.compile("^6(?:011|5\\d{2})\\d{12}$") // Discover
            , Pattern.compile("^(?:2131|1800|35\\d{3})\\d{11}$") // JCB
    );

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 8;
    }

    @Override
    public int getMaximumLength() {
        return 19;
    }

    @Override
    protected boolean quickCheck(String value) {
        if (value.length() > 19) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "Credit card identification. Cards detected are VISA, Mastercard, AMEX, Diners Club, Discover and JCB";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.CREDIT_CARD;
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return combinedPattern;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }


    @Override
    public boolean isOfThisType(String identifier) {
        return matches(identifier) && checkLastDigit(identifier);
    }
}
