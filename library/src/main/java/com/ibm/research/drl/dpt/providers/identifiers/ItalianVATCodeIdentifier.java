/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.regex.Pattern;

public class ItalianVATCodeIdentifier extends AbstractIdentifier {
    private final static Pattern identifier = Pattern.compile("\\d{11}");

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("ITALIAN_VAT");
    }

    @Override
    public boolean isOfThisType(String data) {
        return identifier.matcher(data).matches();
    }

    @Override
    public String getDescription() {
        return "Identifier for Italian VAT (Partita IVA)";
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
        return 11;
    }
}
