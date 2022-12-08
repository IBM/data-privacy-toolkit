/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.MedicineManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

public class MedicineIdentifier extends AbstractManagerBasedIdentifier {
    private final static MedicineManager medicineManager = MedicineManager.getInstance();
    private final static String[] appropriateNames = {"Medicine"};

    @Override
    protected Manager getManager() {
        return medicineManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.MEDICINE;
    }

    @Override
    public String getDescription() {
        return "Medicine identification";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }
}
