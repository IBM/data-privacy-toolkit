/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.DependentManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

public class DependentIdentifier extends AbstractManagerBasedIdentifier {
    private final static DependentManager DEPENDENT_MANAGER = DependentManager.getInstance();

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    protected Manager getManager() {
        return DEPENDENT_MANAGER;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.DEPENDENT;
    }

    @Override
    public String getDescription() {
        return "Identifies dependents";
    }

}
