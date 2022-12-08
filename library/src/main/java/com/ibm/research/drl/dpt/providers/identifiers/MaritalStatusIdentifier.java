/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.MaritalStatusManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class MaritalStatusIdentifier extends AbstractManagerBasedIdentifier {
    private Collection<String> appropriateNames = Arrays.asList("Marital Status");
    private final MaritalStatusManager maritalStatusManager = MaritalStatusManager.getInstance();

    @Override
    protected Manager getManager() {
        return maritalStatusManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return this.appropriateNames;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.MARITAL_STATUS;
    }

    @Override
    public String getDescription() {
        return "Marital status identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
