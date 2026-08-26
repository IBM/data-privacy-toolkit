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

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;


/** Masking provider for timestamp values, supporting component-level masking. */
public class TimeStampMaskingProvider implements MaskingProvider {
    /** The date-time formatter built from the configured format. */
    private DateTimeFormatter formatter;
    /** Whether to mask the year component. */
    private final boolean year;
    /** Whether to mask the month component. */
    private final boolean month;
    /** Whether to mask the day component. */
    private final boolean day;
    /** Whether to mask the hour component. */
    private final boolean hour;
    /** Whether to mask the minute component. */
    private final boolean minute;
    /** Whether to mask the second component. */
    private final boolean second;
    /** The fixed timestamp format string, if configured. */
    private final String fixedFormat;

    /**
     * Constructs a TimeStampMaskingProvider.
     *
     * @param ignore        unused random source
     * @param configuration the masking configuration
     */
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
