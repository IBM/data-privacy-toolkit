/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.NamesManager;
import com.ibm.research.drl.dpt.managers.NamesManager.Names;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.NumberUtils;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.Arrays;
import java.util.Collection;


public class NameIdentifier extends AbstractIdentifier implements IdentifierWithOffset {

    private static final Names names = NamesManager.instance();
    private static final String[] appropriateNames = {"Name", "Surname"};
    @Override
    public ProviderType getType() {
        return ProviderType.NAME;
    }

    @Override
    public boolean isOfThisType(String data) {
        return isOfThisTypeWithOffset(data).getFirst();
    }

    @Override
    public String getDescription() {
        return "Name identification based on popular lists";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
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
        return Integer.MAX_VALUE;
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data) {
        final String[] parts = data.split("\\s");

        boolean hasSurname = false;
        boolean hasName = false;

        if (NumberUtils.countDigits(data) > 0) {
            return new Tuple<>(false, null);
        }

        int offset = 0;
        int depth = data.length();

        for (int i = 0; i < parts.length; i++) {
            String candidate = parts[i];

            if (candidate.length() >= 1 && !Character.isUpperCase(candidate.charAt(0))) {
                return new Tuple<>(false, null);
            }

            if (candidate.length() >= 3) {

                if (candidate.endsWith(",") || candidate.endsWith(".")) {
                    candidate = candidate.substring(0, candidate.length() - 1);

                    if (i == (parts.length - 1)) {
                        depth -= 1;
                    }
                }

                if (candidate.length() < 3) continue; // skip initials & co.

                if (names.isLastName(candidate)) hasSurname = true;
                else if (names.isFirstName(candidate)) hasName = true;
                else {
                    return new Tuple<>(false, null); // something does not match, maybe avenue or so?
                }
            }
        }

        boolean result = hasSurname || hasName; // need to better understand the possible combinations

        if (result) {
            return new Tuple<>(true, new Tuple<>(offset, depth));
        }

        return new Tuple<>(false, null);
    }
}
