/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.ContinentManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

/**
 * The type Continent identifier.
 */
public class ContinentIdentifier extends AbstractManagerBasedIdentifier {
    private static final String[] appropriateNames = {"Continent"};
    private static final ContinentManager continentManager = ContinentManager.getInstance();
    
    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.CONTINENT;
    }

    @Override
    public String getDescription() {
        return "Continent identification";
    }

    @Override
    protected Manager getManager() {
        return continentManager;
    }

    @Override
    public Collection<ProviderType> getLinkedTypes() {
        return Arrays.asList(new ProviderType[]{ProviderType.COUNTRY, ProviderType.CITY});
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }
}
