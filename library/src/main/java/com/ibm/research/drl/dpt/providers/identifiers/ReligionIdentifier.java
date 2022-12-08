/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.ReligionManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Collection;
import java.util.Collections;

public class ReligionIdentifier extends AbstractManagerBasedIdentifier {
    private final Collection<String> appropriateNames = Collections.singletonList("Religion");
    private final static ReligionManager religionManager = ReligionManager.getInstance();

    @Override
    protected Manager getManager() {
        return religionManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return appropriateNames;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.RELIGION;
    }

    @Override
    public String getDescription() {
        return "Religion identifier of the most popular religions";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
