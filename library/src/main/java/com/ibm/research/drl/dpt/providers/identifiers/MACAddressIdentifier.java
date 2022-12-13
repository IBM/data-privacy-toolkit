/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class MACAddressIdentifier extends AbstractRegexBasedIdentifier {
    private static final Collection<Pattern> macAddressPatterns = new ArrayList<Pattern>(List.of(
            Pattern.compile("^([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])$")
    ));

    private static final String[] appropriateNames = {"MAC", "MAC Address"};

    @Override
    protected boolean quickCheck(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == ':') {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return macAddressPatterns;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.MAC_ADDRESS;
    }

    @Override
    public String getDescription() {
        return "MAC Address identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.COLUMN;
    }

    @Override
    public int getMinimumLength() {
        return 17;
    }

    @Override
    public int getMaximumLength() {
        return getMinimumLength();
    }
}
