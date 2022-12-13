/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

/**
 * The type Country identifier.
 *
 * @author stefanob, santonat
 */
public class CountryIdentifier extends AbstractManagerBasedIdentifier {
    private static final String[] appropriateNames = {"Country"};
    private static final CountryManager countryManager = CountryManager.getInstance();

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.COUNTRY;
    }

    @Override
    public String getDescription() {
        return "Country identification. Countries can be recognized by either their name (like United States of America)" +
                ", their 2-letter ISO code (like US) or their 3-letter ISO code (like USA)";
    }

    @Override
    public Collection<ProviderType> getLinkedTypes() {
        return Arrays.asList(ProviderType.CITY);
    }

    @Override
    protected Manager getManager() {
        return countryManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }
}
