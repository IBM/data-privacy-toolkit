/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.CountyManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

public class CountyIdentifier extends AbstractManagerBasedIdentifier {
    private final static CountyManager countyManager = CountyManager.getInstance();

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    protected Manager getManager() {
        return countyManager;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.COUNTY;
    }

    @Override
    public String getDescription() {
        return "County Identification";
    }

}
