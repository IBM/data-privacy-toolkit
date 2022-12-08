/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.regex.Pattern;

public class NationalRegistrationIdentityCardIdentifier extends AbstractIdentifier {
    private static final Pattern pattern = Pattern.compile("^[STFG]\\d\\d\\d\\d\\d\\d\\d\\p{Alnum}$");

    /* if validation is required : https://gist.github.com/eddiemoore/7131781 */

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("NRIC");
    }

    @Override
    public boolean isOfThisType(String data) {
        if (data.length() != 9) {
            return false;
        }

        return pattern.matcher(data).matches();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA & CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 9;
    }

    @Override
    public int getMaximumLength() {
        return 9;
    }
}
