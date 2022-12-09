/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.CountryManager;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.CountryNameSpecification;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CountryMaskingProviderTest {

    @Test
    public void testRandomCountryGenerator() {
        CountryMaskingProvider countryMaskingProvider = new CountryMaskingProvider();

        String originalCountry = "United Kingdom";

        int randomizationOK = 0;

        for(int i = 0; i < 1000; i++) {
            String randomCountry = countryMaskingProvider.mask(originalCountry);
            if(!randomCountry.equalsIgnoreCase(originalCountry)) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testPseudorandom() {

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("country.mask.pseudorandom", true);

        MaskingProvider maskingProvider = new CountryMaskingProvider(maskingConfiguration);

        String originalCity = "Italy";
        String maskedCity = maskingProvider.mask(originalCity);

        String firstMask = maskedCity;

        for(int i = 0; i < 100; i++) {
            maskedCity = maskingProvider.mask(originalCity);
            assertEquals(firstMask, maskedCity);
        }

    }

    @Test
    @Disabled
    public void testHashCode() {
        String a = "Γκάμπια";
        String b = "Γεωργία";

        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
    }

    @Test
    public void testLocalization() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new CountryMaskingProvider();

        String greekOriginalValue = "Ελλάδα";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.COUNTRY, Collections.singletonList("gr"));
        Set<String> greekValues = new HashSet<>();


        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekValues.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 10000; i++) {
            String maskedValue = maskingProvider.mask(greekOriginalValue);
            assertTrue(greekValues.contains(maskedValue.toUpperCase()));
        }
    }

    @Test
    public void testEmptyValue() {
        CountryMaskingProvider countryMaskingProvider = new CountryMaskingProvider();

        String originalCountry = "";
        String randomCountry = countryMaskingProvider.mask(originalCountry);

        assertNotEquals(randomCountry, originalCountry);
    }

    @Test
    public void testPreservesFormat() {
        CountryMaskingProvider countryMaskingProvider = new CountryMaskingProvider();
        CountryManager countryManager = CountryManager.getInstance();

        String originalCountry = "GB";
        String randomCountry = countryMaskingProvider.mask(originalCountry);
        //assertFalse(randomCountry.equals(originalCountry));
        assertTrue(countryManager.isValidCountry(randomCountry, CountryNameSpecification.ISO2));

        originalCountry = "ITA";
        randomCountry = countryMaskingProvider.mask(originalCountry);
        //assertFalse(randomCountry.equals(originalCountry));
        assertTrue(countryManager.isValidCountry(randomCountry, CountryNameSpecification.ISO3));

        originalCountry = "ITALY";
        randomCountry = countryMaskingProvider.mask(originalCountry);
        //assertFalse(randomCountry.equals(originalCountry));
        assertTrue(countryManager.isValidCountry(randomCountry, CountryNameSpecification.NAME));

    }

    @Test
    public void testClosestCountry() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("country.mask.closest", true);

        MaskingProvider countryMaskingProvider = new CountryMaskingProvider(maskingConfiguration);

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String originalCountry = "GB";
            String randomCountry = countryMaskingProvider.mask(originalCountry);
            if (!randomCountry.equals(originalCountry)) {
                randomizationOK++;
            }
        }

        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testCompoundMask() {
        CountryMaskingProvider maskingProvider = new CountryMaskingProvider();

        String originalCountry = "Italy";

        Map<String, OriginalMaskedValuePair> maskedValues = new HashMap<>();
        maskedValues.put("city", new OriginalMaskedValuePair("Rome", "Athens"));

        FieldRelationship fieldRelationship = new FieldRelationship(ValueClass.LOCATION, RelationshipType.LINKED,
                "field0", new RelationshipOperand[] {new RelationshipOperand("city", ProviderType.CITY)});

        String maskedCountry = maskingProvider.mask(originalCountry, "field0", fieldRelationship, maskedValues);
        assertEquals("Greece".toUpperCase(), maskedCountry.toUpperCase());
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration closestMaskingConfiguration = new DefaultMaskingConfiguration("closest");
        closestMaskingConfiguration.setValue("country.mask.closest", true);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration, closestMaskingConfiguration
        };

        String[] originalValues = new String[]{
                "GB"
        };

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            CountryMaskingProvider maskingProvider = new CountryMaskingProvider(maskingConfiguration);

            for (String originalValue : originalValues) {
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < N; i++) {
                    String maskedValue = maskingProvider.mask(originalValue);
                }

                long diff = System.currentTimeMillis() - startMillis;
                System.out.printf("%s: %s: %d operations took %d milliseconds (%f per op)%n",
                        maskingConfiguration.getName(), originalValue, N, diff, (double) diff / N);
            }
        }
    }
}
