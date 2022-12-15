/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.MonthManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class MonthIdentifier extends AbstractManagerBasedIdentifier {
    private static final MonthManager monthManager = MonthManager.getInstance();
    private static final String[] appropriateNames = {"Month"};

    @Override
    public ProviderType getType() {
        return ProviderType.MONTH;
    }

    @Override
    protected Manager getManager() {
        return monthManager;
    }

    @Override
    public String getDescription() {
        return "Identifies months";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
