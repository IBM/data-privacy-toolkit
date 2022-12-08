/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class POBOXIdentifier extends AbstractRegexBasedIdentifier {

    private final static Pattern POBOX = Pattern.compile("\\bP\\.?O\\.?[\\s-]?BOX[\\s-]*(?:\\d+|\\p{Alpha})\\b", Pattern.CASE_INSENSITIVE);

    @Override
    protected Collection<Pattern> getPatterns() {
        return Arrays.asList(POBOX);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.ADDRESS;
    }

    @Override
    public String getDescription() {
        return "POBOX identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }

    @Override
    public int getMinimumLength() {
        return 7;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}
