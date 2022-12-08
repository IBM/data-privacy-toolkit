/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.AnimalSpeciesManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

public class AnimalSpeciesIdentifier extends AbstractManagerBasedIdentifier {
    private final static AnimalSpeciesManager ANIMAL_SPECIES_MANAGER = AnimalSpeciesManager.getInstance();

    @Override
    protected Manager getManager() {
        return ANIMAL_SPECIES_MANAGER;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.ANIMAL;
    }

    @Override
    public String getDescription() {
        return "Identifies animal species";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
