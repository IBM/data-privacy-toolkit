/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The type Email identifier.
 */
public class EmailIdentifier extends AbstractRegexBasedIdentifier {
    private static final Collection<Pattern> emailPatterns = new ArrayList<>(List.of(
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")
    ));

    private static final String[] appropriateNames = {"E-mail", "Email"};


    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.AT;
    }

    @Override
    public int getMinimumLength() {
        return 6;
    }

    @Override
    public int getMaximumLength() {
        return 254;
    }

    @Override
    protected boolean quickCheck(String value) {
        return value.indexOf('@') != -1;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.EMAIL;
    }

    @Override
    public String getDescription() {
        return "Email identification. The format is username@domain";
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return emailPatterns;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }
}
