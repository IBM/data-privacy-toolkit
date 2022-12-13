/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.NamesManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaseInsensitiveNameIdentifier extends AbstractIdentifier {
    private final static NamesManager.Names manager = NamesManager.instance();
    private final Pattern trailingPunctuation = Pattern.compile("(\\p{Alpha}+)[,.]$");
    private final Pattern hasDigit = Pattern.compile("\\d");

    @Override
    public ProviderType getType() {
        return ProviderType.NAME;
    }

    @Override
    public boolean isOfThisType(String data) {
        String[] parts = data.strip().split("\\s+");

        final double base = parts.length;

        double withoutDigits = Arrays.stream(parts)
                .map(hasDigit::matcher)
                .filter(((Predicate<? super Matcher>) Matcher::find).negate())
                .count();

        if (.9 > withoutDigits / base) return false;

        double onlyShort = Arrays.stream(parts).map(String::strip).filter(s -> s.length() < 3).count();

        if (0.4 <= onlyShort / base) return false;

        final double remaining = Arrays.stream(parts)
                .map(String::strip)
                .map(this::removeTrailingPunctuation)
                .filter(s -> s.length() >= 3)
                .filter(((Predicate<String>) s -> manager.isFirstName(s) || manager.isLastName(s)).negate())
                .count();

        return 0.1 >= (remaining / base);
    }

    private String removeTrailingPunctuation(String potentialName) {
        Matcher matcher = trailingPunctuation.matcher(potentialName);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return potentialName;
    }

    @Override
    public String getDescription() {
        return "Name identifier, not case sensitive";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public int getMinimumLength() {
        return 3;
    }

    @Override
    public int getMaximumLength() {
        return 100;
    }
}
