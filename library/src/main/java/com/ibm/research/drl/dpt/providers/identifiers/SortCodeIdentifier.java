/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class SortCodeIdentifier extends AbstractRegexBasedIdentifier {
    private final String BANK_ID = "(?:0[15789]" +
            "|1[0-8]" +
            "|2[0357]" +
            "|30" +
            "|4[02349]" +
            "|5\\d" +
            "|6[024]" +
            "|7[012347]" +
            "|8[023456789]" +
            "|9[0123589])";

    private final Collection<Pattern> patterns = Arrays.asList(
            Pattern.compile(BANK_ID + "\\d{4}"),
            Pattern.compile(BANK_ID + "\\s\\d{2}\\s\\d{2}"),
            Pattern.compile(BANK_ID + "-\\d{2}-\\d{2}")
    );

    @Override
    protected Collection<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SORT_CODE;
    }

    @Override
    public String getDescription() {
        return "Identify sort codes";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 6;
    }

    @Override
    public int getMaximumLength() {
        return 8;
    }
}
