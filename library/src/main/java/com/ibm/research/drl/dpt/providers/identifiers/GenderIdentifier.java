/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.GenderManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

public class GenderIdentifier extends AbstractManagerBasedIdentifier {
    private static final GenderManager genderManager = GenderManager.getInstance();

    @Override
    protected Manager getManager() {
        return genderManager;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.GENDER;
    }

    @Override
    public String getDescription() {
        return "Gender identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
