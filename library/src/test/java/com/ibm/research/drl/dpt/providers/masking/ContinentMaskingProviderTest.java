/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.Readers;
import com.ibm.research.drl.dpt.util.localization.LocalizationManager;
import com.ibm.research.drl.dpt.util.localization.Resource;
import com.ibm.research.drl.dpt.util.localization.ResourceEntry;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ContinentMaskingProviderTest {

    @Test
    public void testMask() {
        MaskingProvider maskingProvider = new ContinentMaskingProvider();

        // different values
        String originalContinent = "Europe";
        String maskedContinent = maskingProvider.mask(originalContinent);
        assertNotEquals(originalContinent, maskedContinent);
    }

    @Test
    public void testLocalization() throws Exception {
        //this test assumes that GR is loaded by default

        MaskingProvider maskingProvider = new ContinentMaskingProvider();

        // different values
        String originalContinent = "Europe";
        String maskedContinent = maskingProvider.mask(originalContinent);
        assertNotEquals(originalContinent, maskedContinent);

        String greekContinent = "Ευρώπη";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.CONTINENT, List.of("gr"));
        Set<String> greekCities = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekCities.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 100; i++) {
            maskedContinent = maskingProvider.mask(greekContinent);
            assertTrue(greekCities.contains(maskedContinent.toUpperCase()));
        }
    }

    @Test
    public void testMaskClosest() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("continent.mask.closest", true);
        maskingConfiguration.setValue("continent.mask.closestK", 2);
        MaskingProvider maskingProvider = new ContinentMaskingProvider(maskingConfiguration);

        String originalContinent = "Europe";

        String[] validNeighbors = {
                "Europe",
                "Africa"
        };

        List<String> neighborsList = Arrays.asList(validNeighbors);

        for(int i = 0; i < 100; i++) {
            String maskedContinent = maskingProvider.mask(originalContinent);
            assertTrue(neighborsList.contains(maskedContinent));
        }
    }

    @Test
    public void testCompoundMask() {
        ContinentMaskingProvider maskingProvider = new ContinentMaskingProvider();

        String originalContinent = "Europe";

        for(int i = 0; i < 100; i++) {
            String maskedContinent = maskingProvider.maskLinked(originalContinent,  "Australia");
            assertTrue("Oceania".equalsIgnoreCase(maskedContinent));
        }
    }

    @Test
    public void testCompoundMaskLinkWithCity() {
        ContinentMaskingProvider maskingProvider = new ContinentMaskingProvider();

        String originalContinent = "Europe";

        FieldRelationship fieldRelationship = new FieldRelationship(ValueClass.LOCATION, RelationshipType.LINKED,
                "field0", new RelationshipOperand[] {new RelationshipOperand("city", ProviderType.CITY)});

        for(int i = 0; i < 100; i++) {
            String maskedContinent = maskingProvider.maskLinked(originalContinent, "Sydney", ProviderType.CITY);
            assertTrue("Oceania".equalsIgnoreCase(maskedContinent));
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration closestConfiguration = new DefaultMaskingConfiguration("closest");
        closestConfiguration.setValue("continent.mask.closest", true);

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration//, closestConfiguration
        };

        String originalContinent = "Europe";

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            ContinentMaskingProvider maskingProvider = new ContinentMaskingProvider(maskingConfiguration);

            int N = 1000000;

            long startMillis = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                String maskedValue = maskingProvider.mask(originalContinent);
            }

            long diff = System.currentTimeMillis() - startMillis;
            System.out.printf("%s: %d operations took %d milliseconds (%f per op)%n",
                    maskingConfiguration.getName(), N, diff, (double) diff / N);
        }
    }
}
