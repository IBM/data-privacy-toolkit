/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.PostalCodeManager;
import com.ibm.research.drl.dpt.managers.StreetNameManager;
import com.ibm.research.drl.dpt.models.Address;
import com.ibm.research.drl.dpt.models.RoadTypes;
import com.ibm.research.drl.dpt.providers.identifiers.AddressIdentifier;
import com.ibm.research.drl.dpt.util.HashUtils;

import java.security.SecureRandom;

/**
 * The type Address masking provider.
 */
public class AddressMaskingProvider extends AbstractMaskingProvider {
    private static final AddressIdentifier addressIdentifier = new AddressIdentifier();
    private static final PostalCodeManager postalCodeManager = PostalCodeManager.getInstance();
    private static final StreetNameManager streetNameManager = StreetNameManager.getInstance();
    private final CountryMaskingProvider countryMaskingProvider;
    private final CityMaskingProvider cityMaskingProvider;
    private final boolean randomizeCountry;
    private final boolean randomizeNumber;
    private final boolean randomizeRoadType;
    private final boolean randomizePostalCode;
    private final boolean nearestPostalCode;
    private final int nearestPostalCodeK;
    private final boolean randomizeCity;
    private final boolean randomizeName;
    private final boolean getPseudorandom;

    /**
     * Instantiates a new Address masking provider.
     */
    public AddressMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Address masking provider.
     *
     * @param maskingConfiguration the masking configuration
     */
    public AddressMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Instantiates a new Address masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public AddressMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;

        this.getPseudorandom = configuration.getBooleanValue("address.mask.pseudorandom");
        if (this.getPseudorandom) {
            configuration.setValue("country.mask.pseudorandom", true);
            configuration.setValue("city.mask.pseudorandom", true);
        }

        this.countryMaskingProvider = new CountryMaskingProvider(configuration);
        this.cityMaskingProvider = new CityMaskingProvider(configuration);

        this.randomizeCountry = configuration.getBooleanValue("address.country.mask");
        this.randomizeNumber = configuration.getBooleanValue("address.number.mask");
        this.randomizeRoadType = configuration.getBooleanValue("address.roadType.mask");
        this.randomizePostalCode = configuration.getBooleanValue("address.postalCode.mask");
        this.nearestPostalCode = configuration.getBooleanValue("address.postalCode.nearest");
        this.nearestPostalCodeK = configuration.getIntValue("address.postalCode.nearestK");
        this.randomizeCity = configuration.getBooleanValue("address.city.mask");
        this.randomizeName = configuration.getBooleanValue("address.streetName.mask");
    }


    @Override
    public String mask(String identifier) {
        Address randomAddress;

        Address address = addressIdentifier.parseAddress(identifier);
        if (address == null) {
            address = new Address("", "", "", "", "", "");
            randomAddress = new Address();
        } else {
            randomAddress = new Address(address);
        }

        if (address.isPOBox()) {
            randomAddress.setPoBox(true);

            if (this.getPseudorandom) {
                String poBoxNumber = address.getPoBoxNumber();
                randomAddress.setPoBoxNumber(Long.toString(Math.abs(HashUtils.longFromHash(poBoxNumber)) % 10000));
            } else {
                randomAddress.setPoBoxNumber(random.nextInt(10000) + "");
            }
            return randomAddress.toString();
        }

        if (this.randomizeNumber) {
            if (this.getPseudorandom) {
                String number = randomAddress.getNumber();
                randomAddress.setNumber(Long.toString(Math.abs(HashUtils.longFromHash(number)) % 300));
            } else {
                randomAddress.setNumber(this.random.nextInt(300) + "");
            }
        }

        if (this.randomizeCity) {
            //psuedorandom is embedded into the provider itself, we have set the configuration accordingly
            randomAddress.setCityOrState(cityMaskingProvider.mask(address.getCityOrState()));
        }

        if (this.randomizeCountry) {
            //psuedorandom is embedded into the provider itself, we have set the configuration accordingly
            randomAddress.setCountry(countryMaskingProvider.mask(address.getCountry()));
        }

        if (this.randomizeName) {
            if (this.getPseudorandom) {
                String sname = randomAddress.getName();
                randomAddress.setName(streetNameManager.getPseudorandom(sname));
            } else {
                randomAddress.setName(streetNameManager.getRandomKey());
            }
        }

        if (this.randomizeRoadType) {
            RoadTypes[] roadTypes = RoadTypes.values();
            int randomPosition;

            if (this.getPseudorandom) {
                randomPosition = (int) (Math.abs(HashUtils.longFromHash(identifier)) % roadTypes.length);
            } else {
                randomPosition = random.nextInt(roadTypes.length);
            }

            String randomRoadType = roadTypes[randomPosition].name();
            randomAddress.setRoadType(randomRoadType);
        }

        if (this.randomizePostalCode) {

            if (this.getPseudorandom) {
                String postalCode = address.getPostalCode();
                randomAddress.setPostalCode(postalCodeManager.getPseudorandom(postalCode));
            } else if (this.nearestPostalCode) {
                String postalCode = address.getPostalCode();
                randomAddress.setPostalCode(postalCodeManager.getClosestPostalCode(postalCode, this.nearestPostalCodeK));
            } else {
                randomAddress.setPostalCode(postalCodeManager.getRandomKey());
            }
        }

        return randomAddress.toString();
    }
}
