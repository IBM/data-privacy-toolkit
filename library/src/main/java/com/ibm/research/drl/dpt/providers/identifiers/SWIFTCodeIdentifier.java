/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.SWIFTCodeManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class SWIFTCodeIdentifier extends AbstractManagerBasedIdentifier {
    private final static String[] appropriateNames = {"SWIFT"};
    private final static SWIFTCodeManager swiftCodeManager = SWIFTCodeManager.getInstance();

    @Override
    protected Manager getManager() {
        return swiftCodeManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SWIFT;
    }

    @Override
    public String getDescription() {
        return "SWIFT Code identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
