/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class TimeStampMaskingProviderTest {
    @Test
    public void testDropMinuteAndSeconds() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        TimeStampMaskingProvider maskingProvider = new TimeStampMaskingProvider(null, configuration);

        String original = "2018-10-22 12:34:12";
        String masked = maskingProvider.mask(original);

        assertNotEquals(original, masked);
    }

    @Test
    public void testDropMinuteAndSecondsCorrectFormat() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("timestamp.mask.format", "uuuu-MM-dd HH:mm:ss");
        configuration.setValue("timestamp.mask.year", false);
        configuration.setValue("timestamp.mask.month", false);
        configuration.setValue("timestamp.mask.day", false);
        configuration.setValue("timestamp.mask.hour", false);
        configuration.setValue("timestamp.mask.minute", true);
        configuration.setValue("timestamp.mask.second", true);


        TimeStampMaskingProvider maskingProvider = new TimeStampMaskingProvider(null, configuration);

        String original = "2018-10-22 12:34:12";
        String masked = maskingProvider.mask(original);

        assertNotEquals(original, masked);
        assertThat(masked, is("2018-10-22 12:00:00"));
    }
}