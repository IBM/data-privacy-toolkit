/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.StatesUSManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

public class StatesUSIdentifier extends AbstractManagerBasedIdentifier {
    private final static StatesUSManager statesUSManager = StatesUSManager.getInstance();

    @Override
    protected Manager getManager() {
        return statesUSManager;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.STATES_US;
    }

    @Override
    public String getDescription() {
        return "Identifies US states";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
