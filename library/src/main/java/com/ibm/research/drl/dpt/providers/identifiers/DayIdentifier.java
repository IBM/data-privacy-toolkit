/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.DayManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class DayIdentifier extends AbstractManagerBasedIdentifier {
    private static final DayManager dayManager = DayManager.getInstance();
    private static final String[] appropriateNames = {"Day"};

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.DAY;
    }

    @Override
    protected Manager getManager() {
        return dayManager;
    }

    @Override
    public String getDescription() {
        return "Identifies days";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }
}
