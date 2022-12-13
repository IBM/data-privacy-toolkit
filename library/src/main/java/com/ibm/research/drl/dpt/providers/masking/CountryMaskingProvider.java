/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.models.Country;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;

import java.security.SecureRandom;
import java.util.Map;

/**
 * The type Country masking provider.
 *
 * @author stefanob
 */
public class CountryMaskingProvider extends AbstractMaskingProvider {
    private static final CountryManager countryManager = CountryManager.getInstance();
    private final boolean getClosest;
    private final int closestK;
    private final boolean getPseudorandom;
    private static final CityManager cityManager = CityManager.getInstance();

    /**
     * Instantiates a new Country masking provider.
     */
    public CountryMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public CountryMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Country masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public CountryMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.getClosest = configuration.getBooleanValue("country.mask.closest");
        this.closestK = configuration.getIntValue("country.mask.closestK");
        this.getPseudorandom = configuration.getBooleanValue("country.mask.pseudorandom");
    }

    @Override
    public String mask(String identifier, String fieldName,
                       FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> values) {

        String cityFieldName = fieldRelationship.getOperands()[0].getName();

        OriginalMaskedValuePair pair = values.get(cityFieldName);
        if (pair == null) {
            return mask(identifier);
        }

        String maskedCity = pair.getMasked();

        City city = cityManager.getKey(maskedCity);
        if (city == null) {
            return mask(identifier);
        }

        Country country = countryManager.lookupCountry(city.getCountryCode(), city.getNameCountryCode());
        if (country == null) {
            return mask(identifier);
        }

        return country.getName();
    }

    @Override
    public String mask(String identifier) {

        if (this.getPseudorandom) {
            return countryManager.getPseudorandom(identifier);
        }

        if (getClosest) {
            return countryManager.getClosestCountry(identifier, this.closestK);
        }

        Country country = countryManager.lookupCountry(identifier);

        if (country == null) {
            return countryManager.getRandomKey();
        } else {
            return countryManager.getRandomKey(identifier, country.getNameCountryCode());
        }
    }
}
