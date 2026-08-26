/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
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
public class AddressMaskingProvider implements MaskingProvider {
    /** Shared address identifier instance. */
    private static final AddressIdentifier addressIdentifier = new AddressIdentifier();
    /** Shared postal code manager instance. */
    private static final PostalCodeManager postalCodeManager = PostalCodeManager.getInstance();
    /** Shared street name manager instance. */
    private static final StreetNameManager streetNameManager = StreetNameManager.getInstance();
    /** Masking provider used to randomise the country component. */
    private final CountryMaskingProvider countryMaskingProvider;
    /** Masking provider used to randomise the city component. */
    private final CityMaskingProvider cityMaskingProvider;
    /** Whether to randomise the country component. */
    private final boolean randomizeCountry;
    /** Whether to randomise the street number component. */
    private final boolean randomizeNumber;
    /** Whether to randomise the road type component. */
    private final boolean randomizeRoadType;
    /** Whether to randomise the postal code component. */
    private final boolean randomizePostalCode;
    /** Whether to replace the postal code with a nearby one. */
    private final boolean nearestPostalCode;
    /** Number of nearest postal codes to select from. */
    private final int nearestPostalCodeK;
    /** Whether to randomise the city component. */
    private final boolean randomizeCity;
    /** Whether to randomise the street name component. */
    private final boolean randomizeName;
    /** Whether to apply pseudorandom (hash-based) masking. */
    private final boolean getPseudorandom;
    /** Secure random source. */
    private final SecureRandom random;

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
