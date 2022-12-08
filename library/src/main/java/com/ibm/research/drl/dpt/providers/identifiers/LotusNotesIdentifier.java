/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LotusNotesIdentifier extends AbstractRegexBasedIdentifier implements IdentifierWithOffset {
    private static final String validIdentifier = "(?:\\w(?:[\\w\\s-_])*)";
    private static final List<Pattern> patterns = Arrays.asList(
            Pattern.compile(
                    "("
                            + validIdentifier + ")"
                            + "/"
                            + validIdentifier
                            + "/"
                            + validIdentifier
                            + "(?:@"+ validIdentifier  + ")*"
                    , Pattern.CASE_INSENSITIVE
            )
    );

    @Override
    protected Collection<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.PERSON;
    }

    @Override
    public String getDescription() {
        return "Identifier for Lotus Notes addresses";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.SLASH;
    }

    @Override
    public int getMinimumLength() {
        return 5;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String identifier) {
        for (Pattern pattern : getPatterns()) {
            Matcher matcher = pattern.matcher(identifier);
            if (matcher.matches()) {
                String person = matcher.group(1);

                return new Tuple<>(true, new Tuple<>(
                        identifier.indexOf(person),
                        person.length()
                ));
            }
        }
        return new Tuple<>(false, null);
    }
}
