/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.RaceManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

/**
 * The type Race ethnicity identifier.
 *
 * @author santonat
 */
public class RaceEthnicityIdentifier extends AbstractManagerBasedIdentifier {
    private static final RaceManager raceManager = RaceManager.getInstance();
    private static final String[] appropriateNames = {"Race", "Ethnicity"};

    @Override
    public ProviderType getType() {
        return ProviderType.RACE;
    }

    @Override
    protected Manager getManager() {
        return raceManager;
    }

    @Override
    public String getDescription() {
        return "Race/Ethnicity identification of most popular ethnic groups";
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
