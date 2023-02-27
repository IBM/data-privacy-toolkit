/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.Age;
import com.ibm.research.drl.dpt.models.AgePortion;
import com.ibm.research.drl.dpt.models.AgePortionFormat;
import com.ibm.research.drl.dpt.providers.identifiers.AgeIdentifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgeMaskingProviderTest {
    private static final AgeIdentifier AGE_IDENTIFIER = new AgeIdentifier();
    
    @Test
    public void testMaskAgeRedact() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("age.mask.redactNumbers", true);

        AgeMaskingProvider ageMaskingProvider = new AgeMaskingProvider(maskingConfiguration);

        assertEquals("XX years old", ageMaskingProvider.mask("9 years old"));
        assertEquals("XX years and XX months", ageMaskingProvider.mask("5 years and 6 months"));
        assertEquals("XX years, XX months, and XX days", ageMaskingProvider.mask("5 years, 6 months, and 11 days"));

    }

    @Test
    public void testForcedMissingPortion() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("age.mask.redactNumbers", true);

        AgeMaskingProvider ageMaskingProvider = new AgeMaskingProvider(maskingConfiguration);

        String originalValue = "5 year and 6 month";

        assertEquals("XX year and XX month", ageMaskingProvider.mask(originalValue));

        Age age = AGE_IDENTIFIER.parseAge(originalValue);

        assertEquals("XX year and 6 month", ageMaskingProvider.mask(originalValue, new Age(
                age.getYearPortion(),
                new AgePortion(false, -1, -1, AgePortionFormat.NUMERICAL),
                age.getWeeksPortion(),
                age.getDaysPortion()
        )));

        assertEquals("5 year and 6 month", ageMaskingProvider.mask(originalValue, new Age(
                new AgePortion(false, -1, -1, AgePortionFormat.NUMERICAL),
                new AgePortion(false, -1, -1, AgePortionFormat.NUMERICAL),
                age.getWeeksPortion(),
                age.getDaysPortion()
        )));
    }

    @Test
    public void testMaskAgeRandomize() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("age.mask.redactNumbers", false);
        maskingConfiguration.setValue("age.mask.randomNumbers", true);

        AgeMaskingProvider ageMaskingProvider = new AgeMaskingProvider(maskingConfiguration);

        final AgePortion MISSING_AGE_PORTION = new AgePortion(false, -1, -1, AgePortionFormat.NUMERICAL);

        String originalValue = "9 years old";
        Age age = new Age(new AgePortion(true, 0, 1, AgePortionFormat.NUMERICAL), MISSING_AGE_PORTION, MISSING_AGE_PORTION, MISSING_AGE_PORTION);
        
        int randomOK = 0;
        
        for(int i = 0; i < 100; i++) {
            String masked = ageMaskingProvider.mask(originalValue, age);
            assertTrue(AGE_IDENTIFIER.isOfThisType(masked));
            if (!masked.equals(originalValue)) {
                randomOK++;
            }
        }
        
        assertTrue(randomOK > 0);

        randomOK = 0;
        for(int i = 0; i < 100; i++) {
            String masked = ageMaskingProvider.mask(originalValue);
            if (!masked.equals(originalValue)) {
                randomOK++;
            }
            assertTrue(AGE_IDENTIFIER.isOfThisType(masked));
        }
        
        assertTrue(randomOK > 0);
        
        originalValue = "5 year and 6 month";
        age = new Age(new AgePortion(true, 0, 1, AgePortionFormat.NUMERICAL), new AgePortion(true, 11, 12, AgePortionFormat.NUMERICAL), MISSING_AGE_PORTION, MISSING_AGE_PORTION);
        String masked = ageMaskingProvider.mask(originalValue, age);
        assertNotEquals(masked, originalValue);
        assertTrue(AGE_IDENTIFIER.isOfThisType(masked));
        masked = ageMaskingProvider.mask(originalValue);
        assertNotEquals(masked, originalValue);
        assertTrue(AGE_IDENTIFIER.isOfThisType(masked));

        originalValue = "five years old";

        randomOK = 0;
        for(int i = 0; i < 100; i++) {
            masked = ageMaskingProvider.mask(originalValue);
            if (!masked.equals(originalValue)) {
                randomOK++;
            }
        }
        
        assertTrue(randomOK > 0);
    }
}

