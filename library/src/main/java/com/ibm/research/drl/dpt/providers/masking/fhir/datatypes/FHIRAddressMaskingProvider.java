/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CityManager;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.managers.StreetNameManager;
import com.ibm.research.drl.dpt.models.City;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAddress;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class FHIRAddressMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {
    private final boolean removeExtensions;

    private final static CityManager cityManager = CityManager.getInstance();
    private final static CountryManager countryManager = CountryManager.getInstance();

    private final MaskingProvider cityMaskingProvider;
    private final MaskingProvider postalCodeMaskingProvider;
    private static final StreetNameManager streetNameManager = StreetNameManager.getInstance();
    private final boolean preserveStateOnly;

    public FHIRAddressMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.address.removeExtensions");
        this.preserveStateOnly = maskingConfiguration.getBooleanValue("fhir.address.preserveStateOnly");

        this.cityMaskingProvider = this.factory.get(ProviderType.CITY, maskingConfiguration);
        this.postalCodeMaskingProvider = this.factory.get(ProviderType.RANDOM, maskingConfiguration);
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRAddress obj = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRAddress.class);
            FHIRAddress maskedObj= mask(obj);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRAddress mask(FHIRAddress address) {
        if (address == null) {
            return null;
        }

        if (preserveStateOnly) {
            FHIRAddress newAddress = new FHIRAddress();
            newAddress.setState(address.getState());
            newAddress.setLine(Collections.singletonList(address.getState()));
            newAddress.setText(address.getState());

            address = newAddress;
        } else {

            String city = address.getCity();
            String randomCity;
            String randomCountry;

            if (city != null) {
                randomCity = cityMaskingProvider.mask(city);
            } else {
                randomCity = cityManager.getRandomKey();
            }

            City maskedCity = cityManager.getKey(randomCity);
            if (maskedCity != null) {
                randomCountry = maskedCity.getCountryCode();
            } else {
                randomCountry = countryManager.getRandomKey();
            }

            address.setCity(randomCity);
            address.setCountry(randomCountry);
            address.setDistrict("");
            address.setState("");

            String postalCode = address.getPostalCode();
            if (postalCode != null) {
                address.setPostalCode(postalCodeMaskingProvider.mask(postalCode));
            }

            String randomStreetName = streetNameManager.getRandomKey();
            String randomStreetNumber = new SecureRandom().nextInt(1000) + "";


            address.setLine(Arrays.asList(randomStreetNumber + " " + randomStreetName, randomCity, randomCountry));
            address.setText(randomStreetNumber + " " + randomStreetName + ", " + randomCity + " " + randomCountry);
        }

        return address;
    }
}

