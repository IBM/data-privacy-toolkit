/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.ZIPCodeManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class ZIPCodeIdentifier extends AbstractManagerBasedIdentifier {
    private static final ZIPCodeManager ZIP_CODE_MANAGER = new ZIPCodeManager(3);
    private static final String[] appropriateNames = {"ZIP code", "ZIP", "ZIPCODE"};

    @Override
    public ProviderType getType() {
        return ProviderType.ZIPCODE;
    }

    @Override
    public String getDescription() {
        return "ZIP code identification. Supports world manufacturer identification";
    }

    @Override
    protected Manager getManager() {
        return ZIP_CODE_MANAGER;
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
