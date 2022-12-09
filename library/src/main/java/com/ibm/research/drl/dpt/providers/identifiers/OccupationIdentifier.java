/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.OccupationManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class OccupationIdentifier extends AbstractManagerBasedIdentifier {
    private final static String[] appropriateNames = {"Job", "Occupation"};
    private final static OccupationManager occupationManager = OccupationManager.getInstance();

    @Override
    protected Manager getManager() {
        return occupationManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.OCCUPATION;
    }

    @Override
    public String getDescription() {
        return "Occupation identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
