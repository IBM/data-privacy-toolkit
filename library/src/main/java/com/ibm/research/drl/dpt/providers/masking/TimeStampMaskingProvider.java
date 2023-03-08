/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;


public class TimeStampMaskingProvider implements MaskingProvider {
    private DateTimeFormatter formatter;
    private final boolean year;
    private final boolean month;
    private final boolean day;
    private final boolean hour;
    private final boolean minute;
    private final boolean second;
    private final String fixedFormat;

    public TimeStampMaskingProvider(SecureRandom ignore, MaskingConfiguration configuration) {
        this.fixedFormat = configuration.getStringValue("timestamp.mask.format");
        this.year = configuration.getBooleanValue("timestamp.mask.year");
        this.month = configuration.getBooleanValue("timestamp.mask.month");
        this.day = configuration.getBooleanValue("timestamp.mask.day");
        this.hour = configuration.getBooleanValue("timestamp.mask.hour");
        this.minute = configuration.getBooleanValue("timestamp.mask.minute");
        this.second = configuration.getBooleanValue("timestamp.mask.second");
    }

    @Override
    public String mask(String identifier) {
        if (null == formatter) {
            formatter = new DateTimeFormatterBuilder().
                    appendPattern(fixedFormat).
                    toFormatter();
        }

        LocalDateTime timestamp = LocalDateTime.parse(identifier, formatter);

        int year = this.year ? 0 : timestamp.get(ChronoField.YEAR);
        int month = this.month ? 0 : timestamp.get(ChronoField.MONTH_OF_YEAR);
        int dayOfMonth = this.day ? 0 : timestamp.get(ChronoField.DAY_OF_MONTH);
        int hour = this.hour ? 0 : timestamp.get(ChronoField.HOUR_OF_DAY);
        int minute = this.minute ? 0 : timestamp.get(ChronoField.MINUTE_OF_HOUR);
        int second = this.second ? 0 : timestamp.get(ChronoField.SECOND_OF_MINUTE);

        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).format(formatter);
    }
}
