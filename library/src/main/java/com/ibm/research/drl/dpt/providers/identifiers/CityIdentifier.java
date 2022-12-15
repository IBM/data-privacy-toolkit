/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class CityIdentifier extends AbstractManagerBasedIdentifier {
    private static final String[] appropriateNames = {"City"};
    private static final CityManager cityManager = CityManager.getInstance();

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.CITY;
    }

    @Override
    public String getDescription() {
        return "City identification, supports all major cities in the world with population above 100K";
    }

    @Override
    protected Manager getManager() {
        return cityManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }
}
