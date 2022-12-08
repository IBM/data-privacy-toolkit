/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.IMSIManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class IMSIIdentifier extends AbstractIdentifier {
    private final static IMSIManager imsiManager = IMSIManager.getInstance();
    private final static String[] appropriateNames = {"IMSI"};

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.IMSI;
    }

    @Override
    public boolean isOfThisType(String data) {
        return imsiManager.isValidIMSI(data);
    }

    @Override
    public String getDescription() {
        return "IMSI identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 15;
    }

    @Override
    public int getMaximumLength() {
        return 15;
    }
}
