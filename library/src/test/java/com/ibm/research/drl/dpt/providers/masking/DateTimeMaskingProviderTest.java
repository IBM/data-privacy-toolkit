/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2023                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.DateTimeIdentifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;

public class DateTimeMaskingProviderTest {

    @Test
    public void testWithCustomTimeZone() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();

        for (String id : ZoneId.getAvailableZoneIds()) {
            maskingConfiguration.setValue("datetime.format.timezone", id);
            maskingConfiguration.setValue("datetime.format.fixed", "yyyy-MM-dd");

            MaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

            String originalValue = "2015-12-10";
            String maskedValue = maskingProvider.mask(originalValue);

            assertNotNull(maskedValue);
            assertThat(maskedValue, not(originalValue));
        }
    }

    @Test
    public void testInvalidRangeValues() {
        String[] values = new String[]{"year", "month", "day", "hour", "minutes", "seconds"};
        
        for(String value: values) {
            assertThrows(RuntimeException.class, () -> {
                MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
                maskingConfiguration.setValue("datetime." + value + ".mask", true);
                maskingConfiguration.setValue("datetime." + value + ".rangeUp", -1);


                new DateTimeMaskingProvider(maskingConfiguration);
            });
            assertThrows(RuntimeException.class, () -> {
                MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
                maskingConfiguration.setValue("datetime." + value + ".mask", true);
                maskingConfiguration.setValue("datetime." + value + ".rangeDown", -1);

                new DateTimeMaskingProvider(maskingConfiguration);
            });
        }
    }
    
    
    @Test
    public void testTrimToIntervalThrowsExceptionForNegativeNumberOfIntervals() {
        assertThrows(RuntimeException.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("datetime.mask.trimTimeToHourInterval", true);
            configuration.setValue("datetime.mask.numberOfIntervals", -10);

            new DateTimeMaskingProvider(configuration);
        });
    }

    @Test
    public void testTrimToIntervalThrowsExceptionForTooLargeNumberOfIntervals() {
        assertThrows(Exception.class, () -> {
            DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
            configuration.setValue("datetime.mask.trimTimeToHourInterval", true);
            configuration.setValue("datetime.mask.numberOfIntervals", 1000);

            new DateTimeMaskingProvider(configuration);
        });
    }

    @Test
    public void testWithIntervalWorksCorrectly() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.day.mask", false);
        configuration.setValue("datetime.month.mask", false);
        configuration.setValue("datetime.year.mask", false);
        configuration.setValue("datetime.mask.trimTimeToHourInterval", true);
        configuration.setValue("datetime.mask.numberOfIntervals", 4);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);

        String original = "11-04-1999 11:15:22";

        String value = maskingProvider.mask(original);

        DateTimeIdentifier identifier = new DateTimeIdentifier();
        Calendar originalCal = Calendar.getInstance();
        originalCal.setTime(Date.from(Instant.from(identifier.matchingFormat(original).getSecond())));

        Calendar maskedCal = Calendar.getInstance();
        maskedCal.setTime(Date.from(Instant.from(identifier.matchingFormat(value).getSecond())));


        assertThat(maskedCal.get(Calendar.YEAR), is(originalCal.get(Calendar.YEAR)));
        assertThat(maskedCal.get(Calendar.MONTH), is(originalCal.get(Calendar.MONTH)));
        assertThat(maskedCal.get(Calendar.DAY_OF_MONTH), is(originalCal.get(Calendar.DAY_OF_MONTH)));
        //
        assertThat(maskedCal.get(Calendar.HOUR_OF_DAY), is(6));
        assertThat(maskedCal.get(Calendar.MINUTE), is(0));
        assertThat(maskedCal.get(Calendar.SECOND), is(0));

        original = "12-04-1999 12:15:22";

        value = maskingProvider.mask(original);

        identifier = new DateTimeIdentifier();
        originalCal = Calendar.getInstance();
        originalCal.setTime(Date.from(Instant.from(identifier.matchingFormat(original).getSecond())));

        maskedCal = Calendar.getInstance();
        maskedCal.setTime(Date.from(Instant.from(identifier.matchingFormat(value).getSecond())));

        assertThat(maskedCal.get(Calendar.YEAR), is(originalCal.get(Calendar.YEAR)));
        assertThat(maskedCal.get(Calendar.MONTH), is(originalCal.get(Calendar.MONTH)));
        assertThat(maskedCal.get(Calendar.DAY_OF_MONTH), is(originalCal.get(Calendar.DAY_OF_MONTH)));
        //
        assertThat(maskedCal.get(Calendar.HOUR_OF_DAY), is(12));
        assertThat(maskedCal.get(Calendar.MINUTE), is(0));
        assertThat(maskedCal.get(Calendar.SECOND), is(0));
    }

    @Test
    public void testMask() {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "08-12-1981 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);

        originalDateTime = "08-12-1981";
        maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
    }
    
    @Test
    public void testReturnOriginalOnUnknownFormat() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.returnOriginalOnUnknownFormat", true);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);
     
        String originalValue = "Totally unknown datetime format";
        String maskedValue = maskingProvider.mask(originalValue);
        
        assertEquals(maskedValue, originalValue);
    }

    @Test
    public void testDoNotReturnOriginalOnUnknownFormat() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.returnOriginalOnUnknownFormat", false);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        String originalValue = "Totally unknown datetime format";
        String maskedValue = maskingProvider.mask(originalValue);

        assertNotEquals(maskedValue, originalValue);
    }

    @Test
    public void testMaskShiftDate() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.shiftDate", true);
        maskingConfiguration.setValue("datetime.mask.shiftSeconds", 120);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "08-12-1981 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);

        assertEquals("08-12-1981 00:02:00", maskedDateTime);
    }

    @Test
    public void testMaskShiftPlusOneHourTimeOnly() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.format.fixed", "HHmmss");
        maskingConfiguration.setValue("datetime.day.mask", false);
        maskingConfiguration.setValue("datetime.year.mask", false);
        maskingConfiguration.setValue("datetime.month.mask", false);
        maskingConfiguration.setValue("datetime.hour.mask", true);
        maskingConfiguration.setValue("datetime.hour.rangeUp", 0);
        maskingConfiguration.setValue("datetime.hour.rangeDown", 1);
        maskingConfiguration.setValue("datetime.minutes.mask", false);
        maskingConfiguration.setValue("datetime.seconds.mask", false);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "000000";

        for(int i = 0; i < 100; i++) {
            String maskedDateTime = maskingProvider.mask(originalDateTime);
            assertNotEquals(originalDateTime, maskedDateTime);
            int hour = Integer.parseInt(maskedDateTime.substring(0, 2));
            assertEquals(23, hour);
        }
    }

    @Test
    public void testMaskShiftPlusOneDay() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.format.fixed", "dd-MM-yyyy HH:mm:ss");
        maskingConfiguration.setValue("datetime.day.mask", true);
        maskingConfiguration.setValue("datetime.day.rangeUp", 0);
        maskingConfiguration.setValue("datetime.day.rangeDown", 7);
        maskingConfiguration.setValue("datetime.year.mask", false);
        maskingConfiguration.setValue("datetime.month.mask", false);
        maskingConfiguration.setValue("datetime.hour.mask", false);
        maskingConfiguration.setValue("datetime.minutes.mask", false);
        maskingConfiguration.setValue("datetime.seconds.mask", false);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "08-12-1981 00:00:00";
        
        for(int i = 0; i < 100; i++) {
            String maskedDateTime = maskingProvider.mask(originalDateTime);
            assertNotEquals(originalDateTime, maskedDateTime);
            int day = Integer.parseInt(maskedDateTime.split("-")[0]);
            assertTrue(day <= 8);
            assertTrue(day >= 1);
        }
    }

    @Test
    public void testMaskShiftDateFixedFormat() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.shiftDate", true);
        maskingConfiguration.setValue("datetime.mask.shiftSeconds", 120);
        maskingConfiguration.setValue("datetime.format.fixed", "dd-MM-yyyy HH:mm:ss");

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "08-12-1981 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);

        assertEquals("08-12-1981 00:02:00", maskedDateTime);
    }

    @Test
    @Disabled
    public void testMaskShiftDatePerformance() {
        int N = 1000000;

        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.shiftDate", true);
        maskingConfiguration.setValue("datetime.mask.shiftSeconds", 120);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "08-12-1981 00:00:00";

        long startMillis = System.currentTimeMillis();

        for(int i = 0; i < N; i++) {
            String maskedDateTime = maskingProvider.mask(originalDateTime);
            assertEquals("08-12-1981 00:02:00", maskedDateTime);
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.printf("%s: %d operations took %d milliseconds (%f per op)%n",
                maskingConfiguration.getName(), N, diff, (double) diff / N);
    }

    @Test
    public void testMaskShiftDateNegative() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.shiftDate", true);
        maskingConfiguration.setValue("datetime.mask.shiftSeconds", -120);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "08-12-1981 00:04:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);

        assertEquals("08-12-1981 00:02:00", maskedDateTime);
    }

    @Test
    public void testMaskFixedFormat() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy");

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "08-12-1981";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
    }

    @Test
    public void testMaskFixedFormatAMPM() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy K:mm:ss a");

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "08-12-1981 02:14:12 AM";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
    }

    @Test
    public void testMaskAlphabetFixedFormat() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "08-JAn-1981";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
    }

    @Test
    public void testMaskFixedFormatNoChange() {
        DefaultMaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy HH:mm:ss");
        configuration.setValue("datetime.year.mask", false);
        configuration.setValue("datetime.month.mask", false);
        configuration.setValue("datetime.day.mask", false);
        configuration.setValue("datetime.hour.mask", false);
        configuration.setValue("datetime.minutes.mask", false);
        configuration.setValue("datetime.seconds.mask", false);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "08-12-1981 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals(originalDateTime, maskedDateTime);
    }

    
    @Test
    @Disabled
    public void testPerformance() {
        DefaultMaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration("default");
        DefaultMaskingConfiguration fixedConfiguration = new DefaultMaskingConfiguration("fixed format");
        fixedConfiguration.setValue("datetime.format.fixed", "dd-MM-yyyy");

        DefaultMaskingConfiguration[] configurations = new DefaultMaskingConfiguration[]{
                maskingConfiguration, fixedConfiguration
        };

        for (DefaultMaskingConfiguration configuration : configurations) {
            DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);

            int N = 1000000;
            String originalDateTime = "08-12-1981";

            long startMillis = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                String maskedDateTime = maskingProvider.mask(originalDateTime);

                assertNotNull(maskedDateTime);
            }

            long diff = System.currentTimeMillis() - startMillis;
            System.out.printf("%s: %d operations took %d milliseconds (%f per op)%n",
                    configuration.getName(), N, diff, (double) diff / N);
        }
    }

    @Test
    public void testCompoundMaskingLess() throws Exception {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "04-03-2014 00:00:00";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(originalDateTime);

        String originalOperandTime = "04-03-2015 00:00:00";
        Date originalOperandDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(originalOperandTime);

        long originalDiff = originalOperandDate.getTime() - originalDate.getTime();

        String maskedOperandTime = "08-12-2015 00:00:00";
        Date maskedOperandDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(maskedOperandTime);

        String maskedDateTime = maskingProvider.maskLess(originalDateTime, maskedOperandTime, originalOperandTime);
        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(maskedDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
        assertTrue(maskedDate.before(maskedOperandDate));

        long maskedDiff = maskedOperandDate.getTime() - maskedDate.getTime();
        assertEquals(maskedDiff, originalDiff);
    }

    @Test
    public void testCompoundMaskingEquals() {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "04-03-2014 00:00:00";
        String maskedOperandTime = "08-12-2015 00:00:00";

        String maskedDateTime = maskingProvider.maskEqual(originalDateTime, maskedOperandTime);
        assertEquals(maskedOperandTime, maskedDateTime);
    }

    @Test
    public void testCompoundMaskingLessDateFormat() throws Exception {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "04-03-2014";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalDateTime);

        String originalOperandTime = "04-03-2015";
        Date originalOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalOperandTime);

        long originalDiff = originalOperandDate.getTime() - originalDate.getTime();

        String maskedOperandTime = "08-12-2015";
        Date maskedOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedOperandTime);

        String maskedDateTime = maskingProvider.maskLess(originalDateTime, maskedOperandTime, originalOperandTime);
        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
        assertTrue(maskedDate.before(maskedOperandDate));

        long maskedDiff = maskedOperandDate.getTime() - maskedDate.getTime();
        assertEquals(maskedDiff, originalDiff);
    }

    @Test
    public void testCompoundMaskingEqualDateFormat() {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();
        String originalDateTime = "04-03-2014";
        String maskedOperandTime = "08-12-2015";
        String maskedDateTime = maskingProvider.maskEqual(originalDateTime, maskedOperandTime);
        assertEquals(maskedOperandTime, maskedDateTime);
    }

    @Test
    public void testCompoundMaskingDistanceBefore() throws Exception {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "04-03-2014";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalDateTime);

        String originalOperandTime = "04-03-2015";
        Date originalOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalOperandTime);

        long originalDiff = originalOperandDate.getTime() - originalDate.getTime();

        String maskedOperandTime = "08-12-2015";
        Date maskedOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedOperandTime);

        String maskedDateTime = maskingProvider.maskDistance(originalDateTime, originalOperandTime, maskedOperandTime);

        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
        assertTrue(maskedDate.before(maskedOperandDate));

        long maskedDiff = maskedOperandDate.getTime() - maskedDate.getTime();
        assertEquals(maskedDiff, originalDiff);
    }

    @Test
    public void testCompoundMaskingDistanceAfter() throws Exception {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "04-03-2016";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalDateTime);

        String originalOperandTime = "04-03-2015";
        Date originalOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalOperandTime);

        long originalDiff = originalOperandDate.getTime() - originalDate.getTime();

        String maskedOperandTime = "08-12-2015";
        Date maskedOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedOperandTime);

        String maskedDateTime = maskingProvider.maskDistance(originalDateTime, originalOperandTime, maskedOperandTime);

        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedDateTime);
        assertNotEquals(originalDateTime, maskedDateTime);
        assertTrue(maskedDate.after(maskedOperandDate));

        long maskedDiff = maskedOperandDate.getTime() - maskedDate.getTime();
        assertEquals(maskedDiff, originalDiff);
    }

    @Test
    public void testCompoundMaskingDistanceEqual() throws Exception {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        // different values
        String originalDateTime = "04-03-2015";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalDateTime);

        String originalOperandTime = "04-03-2015";
        Date originalOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalOperandTime);

        long originalDiff = originalOperandDate.getTime() - originalDate.getTime();

        String maskedOperandTime = "08-12-2015";
        Date maskedOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedOperandTime);

        String maskedDateTime = maskingProvider.maskDistance(originalDateTime, originalOperandTime, maskedOperandTime);
        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedDateTime);

        long maskedDiff = maskedOperandDate.getTime() - maskedDate.getTime();
        assertEquals(maskedDiff, originalDiff);

        assertEquals(0L, maskedDiff);
    }

    @Test
    public void testCompoundMaskingDistanceEqualFixedFormat() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.format.fixed", "dd-MM-yyyy");
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "04-03-2015";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalDateTime);

        String originalOperandTime = "04-03-2015";
        Date originalOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(originalOperandTime);

        long originalDiff = originalOperandDate.getTime() - originalDate.getTime();

        String maskedOperandTime = "08-12-2015";
        Date maskedOperandDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedOperandTime);

        String maskedDateTime = maskingProvider.maskDistance(originalDateTime, originalOperandTime, maskedOperandTime);
        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy").parse(maskedDateTime);

        long maskedDiff = maskedOperandDate.getTime() - maskedDate.getTime();
        assertEquals(maskedDiff, originalDiff);

        assertEquals(0L, maskedDiff);
    }

    @Test
    public void testCompoundMaskingDistanceEqualFixedFormatHours() {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.format.fixed", "HHmmss");
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "040000";
        String originalOperandTime = "050000";

        String maskedOperandTime = "070000";

        String maskedDateTime = maskingProvider.maskDistance(originalDateTime, originalOperandTime, maskedOperandTime);

        assertEquals("060000", maskedDateTime);
    }

    @Test
    public void testCompoundMaskingKey() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        long maxDays = maskingConfiguration.getIntValue("datetime.mask.keyBasedMaxDays");
        
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "04-03-2014 00:00:00";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(originalDateTime);

        String user1_lastValue = null;
        
        for(int i = 0; i < 100; i++) {
            String operandValue = "user1";

            String maskedDateTime = maskingProvider.maskWithKey(originalDateTime, operandValue);
            Date maskedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(maskedDateTime);
            
            long maskedDiff = (originalDate.getTime() - maskedDate.getTime()) / (24*60*60*1000);

            assertTrue(maskedDiff < maxDays);

            assertNotEquals(originalDateTime, maskedDateTime);
            
            if (user1_lastValue != null) {
                assertEquals(maskedDateTime, user1_lastValue);
            }
            
            user1_lastValue = maskedDateTime;
        }
        
        String user2_lastValue = null;

        for(int i = 0; i < 100; i++) {
            String operandValue = "user2";

            String maskedDateTime = maskingProvider.maskWithKey(originalDateTime, operandValue);
            Date maskedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(maskedDateTime);
            
            long maskedDiff = (originalDate.getTime() - maskedDate.getTime()) / (24*60*60*1000);
            
            assertThat(maskedDiff, lessThan( maxDays));
            assertNotEquals(originalDateTime, maskedDateTime);

            if (user2_lastValue != null) {
                assertEquals(maskedDateTime, user2_lastValue);
            }

            user2_lastValue = maskedDateTime;
        }
       
        assertNotEquals(user1_lastValue, user2_lastValue);
    }

    @Test
    public void testCompoundMaskingKeyNullOperand() throws Exception {
        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        long maxDays = maskingConfiguration.getIntValue("datetime.mask.keyBasedMaxDays");

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "04-03-2014 00:00:00";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(originalDateTime);

        String maskedDateTime = maskingProvider.maskWithKey(originalDateTime, null);
        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(maskedDateTime);

        long maskedDiff = (originalDate.getTime() - maskedDate.getTime()) / (24*60*60*1000);

        assertTrue(maskedDiff < maxDays);
    }

    @Test
    public void testCompoundMaskingKeyWithMinDays() throws Exception {
        int maxDays = 7;
        int minDays = 2;

        MaskingConfiguration maskingConfiguration = new DefaultMaskingConfiguration();
        maskingConfiguration.setValue("datetime.mask.keyBasedMaxDays", maxDays);
        maskingConfiguration.setValue("datetime.mask.keyBasedMinDays", minDays);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(maskingConfiguration);

        // different values
        String originalDateTime = "04-03-2014 00:00:00";
        Date originalDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(originalDateTime);

        String operandValue = "user1";

        String user1_lastValue = maskingProvider.maskWithKey(originalDateTime, operandValue);
        Date maskedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(user1_lastValue);

        long maskedDiff = (originalDate.getTime() - maskedDate.getTime()) / (24 * 60 * 60 * 1000);
        assertTrue(maskedDiff < maxDays);
        assertTrue(maskedDiff >= minDays);

        assertTrue(maskedDate.before(originalDate));
        assertNotEquals(originalDateTime, user1_lastValue);


        operandValue = "user2";

        String user2_lastValue = maskingProvider.maskWithKey(originalDateTime, operandValue);
        maskedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(user2_lastValue);
        maskedDiff = (originalDate.getTime() - maskedDate.getTime()) / (24 * 60 * 60 * 1000);
        assertTrue(maskedDiff < maxDays);
        assertTrue(maskedDiff >= minDays);
        assertNotEquals(originalDateTime, user2_lastValue);

        assertNotEquals(user1_lastValue, user2_lastValue);
    }

    @Test
    @Disabled("Ignored because of Java inconsistencies in WEEK_OF_YEAR")
    public void testGeneralizeWeekYear() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.weekyear", true);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "05-01-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);

        assertEquals("01/2016", maskedDateTime);
    }

    @Test
    public void testGeneralizeMonthYear() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.monthyear", true);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "01-03-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("03/2016", maskedDateTime);
    }

    @Test
    public void testGeneralizeMonthYearFixedFormat() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.monthyear", true);
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy HH:mm:ss");
        
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "01-03-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("03/2016", maskedDateTime);
    }

    @Test
    public void testGeneralizeMonthYearISOStamp() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.monthyear", true);
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "2014-10-07T14:45:00Z";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("10/2014", maskedDateTime);
    }
    
    @Test
    public void testGeneralizeQuarterYear() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.quarteryear", true);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "12-12-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("04/2016", maskedDateTime);

        originalDateTime = "12-01-2016 00:00:00";
        maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("01/2016", maskedDateTime);
    }

    @Test
    public void testGeneralizeQuarterYearFixedFormat() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.quarteryear", true);
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy HH:mm:ss");

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "12-12-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("04/2016", maskedDateTime);

        originalDateTime = "12-01-2016 00:00:00";
        maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("01/2016", maskedDateTime);
    }

    @Test
    public void testGeneralizeYear() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.year", true);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "12-12-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("2016", maskedDateTime);
    }

    @Test
    public void testGeneralizeNYearInterval() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.generalize.nyearinterval", true);
        configuration.setValue("datetime.generalize.nyearintervalvalue", 5);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        String originalDateTime = "12-12-2016 00:00:00";
        String maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("2015-2020", maskedDateTime);

        configuration.setValue("datetime.generalize.nyearintervalvalue", 10);
        maskingProvider = new DateTimeMaskingProvider(configuration);
        maskedDateTime = maskingProvider.mask(originalDateTime);
        assertEquals("2010-2020", maskedDateTime);
    }

    @Test
    public void testMaskInvalidValue() {
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider();

        String invalidValue = "fooo";
        String maskedDate = maskingProvider.mask(invalidValue);

        assertThat(maskedDate, is(""));
    }

    @Test
    public void testMaskEmptyValueFixedFormat() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy HH:mm:ss");
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);
        

        String invalidValue = "";
        String maskedDate = maskingProvider.mask(invalidValue);
        assertThat(maskedDate, is(""));
    }

    @Test
    public void testMaskReplaceSameDayClassWeekdays() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy");
        configuration.setValue("datetime.mask.replaceDaySameClass", true);
        
        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);

        String date = "18-04-2018"; //this is Wednesday
        
        for(int i = 0; i < 100; i++) {
            String masked = maskingProvider.mask(date);
            int day = Integer.parseInt(masked.split("-")[0]);
            assertThat(day , greaterThanOrEqualTo( 16));
            assertThat(day, lessThanOrEqualTo(20));
        }
    }

    @Test
    public void testMaskReplaceSameDayClassWeekends() {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy");
        configuration.setValue("datetime.mask.replaceDaySameClass", true);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);

        String date = "15-04-2018"; //this is Sunday

        for(int i = 0; i < 100; i++) {
            String masked = maskingProvider.mask(date);
            int day = Integer.parseInt(masked.split("-")[0]);
            assertThat(day, anyOf(equalTo(14), equalTo(15)));
        }
    }


    @Test
    public void testMaskReplaceDaysWithDiffPriv() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy");
        configuration.setValue("datetime.mask.replaceDayWithDiffPriv", true);
        configuration.setValue("datetime.mask.replaceDayWithDiffPrivEpsilon", 3.0);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);

        String date = "18-04-2018"; //this is Wednesday

        int weekdays = 0;
        int weekends = 0;
        int sameDay = 0;
        
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        
        for(int i = 0; i < 100000; i++) {
            String masked = maskingProvider.mask(date);
            Date d = format.parse(masked);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int maskedDay = cal.get(Calendar.DAY_OF_WEEK);
           
            if (maskedDay == Calendar.WEDNESDAY) {
                sameDay++;
            } else if (maskedDay >= Calendar.MONDAY && maskedDay <= Calendar.FRIDAY) {
                weekdays++;
            }
            else {
                weekends++;
            }
        }

        System.out.println("same day: " + sameDay);
        System.out.println("weekdays: " + weekdays/4);
        System.out.println("weekends: " + weekends/2);
    }

    @Test
    @Disabled("Performance testing?")
    public void testMaskReplaceDaysWithDiffPrivWeekend() throws Exception {
        MaskingConfiguration configuration = new DefaultMaskingConfiguration();
        configuration.setValue("datetime.format.fixed", "dd-MM-yyyy");
        configuration.setValue("datetime.mask.replaceDayWithDiffPriv", true);
        configuration.setValue("datetime.mask.replaceDayWithDiffPrivEpsilon", 3.0);

        DateTimeMaskingProvider maskingProvider = new DateTimeMaskingProvider(configuration);

        String date = "21-04-2018"; //this is Saturday 

        int weekdays = 0;
        int weekends = 0;
        int sameDay = 0;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        for(int i = 0; i < 100000; i++) {
            String masked = maskingProvider.mask(date);
            Date d = format.parse(masked);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            int maskedDay = cal.get(Calendar.DAY_OF_WEEK);

            if (maskedDay == Calendar.SATURDAY) {
                sameDay++;
            }

            if (maskedDay >= Calendar.MONDAY && maskedDay <= Calendar.FRIDAY) {
                weekdays++;
            }
            else {
                weekends++;
            }
        }

        System.out.println("weekends: " + weekends);
        System.out.println("weekdays: " + weekdays);
        System.out.println("same day: " + sameDay);
    }
}
