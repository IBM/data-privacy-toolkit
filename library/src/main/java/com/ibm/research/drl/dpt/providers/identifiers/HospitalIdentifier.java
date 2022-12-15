/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.HospitalManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class HospitalIdentifier extends AbstractManagerBasedIdentifier {
    private final String[] appropriateNames = {"Hospital", "Medical Center"};

    private final static HospitalManager hospitalManager = HospitalManager.getInstance();

    @Override
    protected Manager getManager() {
        return hospitalManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.HOSPITAL;
    }

    @Override
    public String getDescription() {
        return "Hospital and medical center name identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
