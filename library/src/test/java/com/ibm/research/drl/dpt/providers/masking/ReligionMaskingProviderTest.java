/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.ReligionIdentifier;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReligionMaskingProviderTest {

    @Test
    public void testMask() throws Exception {
        ReligionMaskingProvider maskingProvider = new ReligionMaskingProvider();
        ReligionIdentifier identifier = new ReligionIdentifier();

        String originalReligion = "Buddhist";

        int randomizationOK = 0;

        for(int i = 0; i < 10; i++) {
            String maskedReligion = maskingProvider.mask(originalReligion);
            assertTrue(identifier.isOfThisType(maskedReligion));
            if (!maskedReligion.equals(originalReligion)) {
                randomizationOK++;
            }
        }
        assertTrue(randomizationOK > 0);
    }
    
    @Test
    public void testProbabilistic() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("religion.mask.probabilityBased", true);
        
        ReligionMaskingProvider maskingProvider = new ReligionMaskingProvider(maskingConfiguration);
        ReligionIdentifier identifier = new ReligionIdentifier();

        String originalReligion = "Buddhist";

        int randomizationOK = 0;

        for(int i = 0; i < 100; i++) {
            String maskedReligion = maskingProvider.mask(originalReligion);
            assertTrue(identifier.isOfThisType(maskedReligion));
            if (!maskedReligion.equals(originalReligion)) {
                randomizationOK++;
            }
        }
        assertTrue(randomizationOK > 0);
    }

    @Test
    public void testLocalization() throws Exception {
        MaskingProvider maskingProvider = new ReligionMaskingProvider();

        String greekReligion = "Βουδιστής";

        Collection<ResourceEntry> entryCollection = LocalizationManager.getInstance().getResources(Resource.RELIGION, Arrays.asList("gr"));
        Set<String> greekReligions = new HashSet<>();

        for(ResourceEntry entry: entryCollection) {
            InputStream inputStream = entry.createStream();
            try (CSVParser reader = Readers.createCSVReaderFromStream(inputStream)) {
                for (CSVRecord line : reader) {
                    String name = line.get(0);
                    greekReligions.add(name.toUpperCase());
                }
                inputStream.close();
            }
        }

        for(int i = 0; i < 100; i++) {
            String greekMasked = maskingProvider.mask(greekReligion);
            assertTrue(greekReligions.contains(greekMasked.toUpperCase()));
        }
    }

    @Test
    @Disabled
    public void testPerformance() {
        int N = 1000000;
        DefaultMaskingConfiguration defaultConfiguration = new DefaultMaskingConfiguration("default");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                defaultConfiguration
        };

        String[] originalValues = new String[]{"Buddhist"};

        for (DefaultMaskingConfiguration maskingConfiguration : configurations) {
            ReligionMaskingProvider maskingProvider = new ReligionMaskingProvider(maskingConfiguration);

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
