/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.ICDv9Manager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;


public class ICDv9Identifier extends AbstractManagerBasedIdentifier {
    private static final String[] appropriateNames = {"ICD", "Disease code", "ICD9"};
    private static final ICDv9Manager icdv9Manager = ICDv9Manager.getInstance();

    @Override
    public ProviderType getType() {
        return ProviderType.ICDv9;
    }

    @Override
    public String getDescription() {
        return "ICD9 identification";
    }

    @Override
    protected Manager getManager() {
        return icdv9Manager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }
}
