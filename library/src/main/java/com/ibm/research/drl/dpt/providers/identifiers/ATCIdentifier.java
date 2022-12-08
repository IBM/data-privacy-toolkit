/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.ATCManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class ATCIdentifier extends AbstractManagerBasedIdentifier {
    private final static ATCManager atcManager = ATCManager.getInstance();
    private final String[] appropriateNames = new String[] {"ATC"};

    @Override
    protected Manager getManager() {
        return atcManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.ATC;
    }

    @Override
    public String getDescription() {
        return "ATC identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }
}
