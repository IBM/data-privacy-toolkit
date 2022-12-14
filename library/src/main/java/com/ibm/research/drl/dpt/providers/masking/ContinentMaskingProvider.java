/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.managers.ContinentManager;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.models.Continent;
import com.ibm.research.drl.dpt.models.Country;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;

import java.security.SecureRandom;
import java.util.Map;

/**
 * The type Continent masking provider.
 *
 */
public class ContinentMaskingProvider extends AbstractMaskingProvider {
    private static final CountryManager countryManager = CountryManager.getInstance();
    private static final CityManager cityManager = CityManager.getInstance();
    private final boolean getClosest;
    private final int getClosestK;

    private final static ContinentManager continentManager = ContinentManager.getInstance();

    /**
     * Instantiates a new Continent masking provider.
     */
    public ContinentMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Continent masking provider.
     *
     * @param configuration the configuration
     */
    public ContinentMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Continent masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public ContinentMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.getClosest = configuration.getBooleanValue("continent.mask.closest");
        this.getClosestK = configuration.getIntValue("continent.mask.closestK");
    }

    @Override
    public String mask(String identifier, String fieldName,
                       FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> values) {

        RelationshipOperand relationshipOperand = fieldRelationship.getOperands()[0];

        String operandFieldName = fieldRelationship.getOperands()[0].getName();
        String operandMaskedValue = values.get(operandFieldName).getMasked();

        if (relationshipOperand.getType() == ProviderType.COUNTRY) {
            Continent continent = continentManager.getKey(identifier);
            String locale = null;
            if (continent != null) {
                locale = continent.getNameCountryCode();
            }

            Country country = countryManager.lookupCountry(operandMaskedValue, locale);
            if (country == null) {
                return mask(identifier);
            }

            return country.getContinent();
        } else if (relationshipOperand.getType() == ProviderType.CITY) {
            City city = cityManager.getKey(operandMaskedValue);
            if (city != null) {
                String countryCode = city.getCountryCode();
                Country country = countryManager.lookupCountry(countryCode, city.getNameCountryCode());
                if (country != null) {
                    return country.getContinent();
                }
            }

            return mask(identifier);
        }

        return mask(identifier);
    }

    @Override
    public String mask(String identifier) {

        if (getClosest) {
            return continentManager.getClosestContinent(identifier, getClosestK);
        }

        Continent continent = continentManager.getKey(identifier);
        if (continent == null) {
            return continentManager.getRandomKey();
        }

        return continentManager.getRandomKey(continent.getNameCountryCode());
    }
}
