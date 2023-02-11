/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.providers.identifiers.DateTimeIdentifier;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import com.ibm.research.drl.dpt.util.HashUtils;
import com.ibm.research.drl.dpt.util.RandomGenerators;
import com.ibm.research.drl.dpt.util.Tuple;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * The type Date time masking provider.
 */
public class DateTimeMaskingProvider extends AbstractMaskingProvider {
    private static final Logger logger = LogManager.getLogger(DateTimeMaskingProvider.class);
    private final static DateTimeIdentifier dateTimeIdentifier = new DateTimeIdentifier();

    private final TemporalAnnotationMaskingProvider temporalMaskingProvider;

    private final int failMode;
    private final boolean shiftDate;
    private final int shiftSeconds;

    private final boolean replaceDaySameClass;

    private final boolean yearMask;
    private final int yearRangeUp;
    private final int yearRangeDown;

    private final boolean monthMask;
    private final int monthRangeUp;
    private final int monthRangeDown;

    private final boolean dayMask;
    private final int dayRangeUp;
    private final int dayRangeDown;

    private final boolean hourMask;
    private final int hourRangeUp;
    private final int hourRangeDown;

    private final boolean minutesMask;
    private final int minutesRangeUp;
    private final int minutesRangeDown;

    private final boolean secondsMask;
    private final int secondsRangeUp;
    private final int secondsRangeDown;

    private final boolean generalizeWeekYear;
    private final boolean generalizeMonthYear;
    private final boolean generalizeQuarterYear;
    private final boolean generalizeYear;
    private final boolean generalizeNYearInterval;
    private final int generalizeNYearIntervalValue;

    private final boolean returnOriginalOnUnknownFormat;

    private final int keyBasedMaxDays;
    private final int keyBasedMinDays;

    private final boolean trimTimeToHourInterval;
    private final int numberOfHourIntervals;
    private final String timeZone;

    private String fixedDateFormat = null;
    private final static DateTimeFormatter defaultDateFormat = new DateTimeFormatterBuilder()
            .appendPattern("dd/MM/yyyy")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).toFormatter();

    /**
     * Instantiates a new Date time masking provider.
     */
    public DateTimeMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Date time masking provider.
     *
     * @param configuration the configuration
     */
    public DateTimeMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Date time masking provider.
     *
     * @param random the random
     */
    public DateTimeMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Date time masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public DateTimeMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.failMode = configuration.getIntValue("fail.mode");
        this.fixedDateFormat = configuration.getStringValue("datetime.format.fixed");

        this.random = random;

        this.temporalMaskingProvider = new TemporalAnnotationMaskingProvider(random, configuration);

        this.returnOriginalOnUnknownFormat = configuration.getBooleanValue("datetime.mask.returnOriginalOnUnknownFormat");
        this.shiftDate = configuration.getBooleanValue("datetime.mask.shiftDate");
        this.shiftSeconds = configuration.getIntValue("datetime.mask.shiftSeconds");

        this.replaceDaySameClass = configuration.getBooleanValue("datetime.mask.replaceDaySameClass");

        this.keyBasedMaxDays = configuration.getIntValue("datetime.mask.keyBasedMaxDays");
        this.keyBasedMinDays = configuration.getIntValue("datetime.mask.keyBasedMinDays");

        if (this.keyBasedMaxDays < this.keyBasedMinDays) {
            String msg = "datetime.mask.keyBasedMaxDays must be >= datetime.mask.keyBasedMinDays";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        if (this.keyBasedMinDays < 0) {
            String msg = "datetime.mask.keyBasedMinDays must be >= 0";
            logger.error(msg);
            throw new RuntimeException(msg);
        }

        this.yearMask = configuration.getBooleanValue("datetime.year.mask");
        this.yearRangeUp = configuration.getIntValue("datetime.year.rangeUp");
        this.yearRangeDown = configuration.getIntValue("datetime.year.rangeDown");

        if (this.yearMask) {
            if (this.yearRangeDown < 0) {
                String msg = "datetime.year.rangeDown must be >=0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            if (this.yearRangeUp < 0) {
                String msg = "datetime.year.rangeUp must be >= 0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
        }

        this.monthMask = configuration.getBooleanValue("datetime.month.mask");
        this.monthRangeUp = configuration.getIntValue("datetime.month.rangeUp");
        this.monthRangeDown = configuration.getIntValue("datetime.month.rangeDown");

        if (this.monthMask) {
            if (this.monthRangeDown < 0) {
                String msg = "datetime.month.rangeDown must be >=0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            if (this.monthRangeUp < 0) {
                String msg = "datetime.month.rangeUp must be >= 0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
        }

        this.dayMask = configuration.getBooleanValue("datetime.day.mask");
        this.dayRangeUp = configuration.getIntValue("datetime.day.rangeUp");
        this.dayRangeDown = configuration.getIntValue("datetime.day.rangeDown");

        if (this.dayMask) {
            if (this.dayRangeDown < 0) {
                String msg = "datetime.day.rangeDown must be >=0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            if (this.dayRangeUp < 0) {
                logger.error("datetime.day.rangeUp must be >= 0");
                throw new RuntimeException("datetime.day.rangeUp must be >= 0");
            }
        }

        this.hourMask = configuration.getBooleanValue("datetime.hour.mask");
        this.hourRangeUp = configuration.getIntValue("datetime.hour.rangeUp");
        this.hourRangeDown = configuration.getIntValue("datetime.hour.rangeDown");

        if (this.hourMask) {
            if (this.hourRangeDown < 0) {
                String msg = "datetime.hour.rangeDown must be >=0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            if (this.hourRangeUp < 0) {
                String msg = "datetime.hour.rangeUp must be >= 0";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
        }

        this.minutesMask = configuration.getBooleanValue("datetime.minutes.mask");
        this.minutesRangeUp = configuration.getIntValue("datetime.minutes.rangeUp");
        this.minutesRangeDown = configuration.getIntValue("datetime.minutes.rangeDown");

        if (this.minutesMask) {
            if (this.minutesRangeDown < 0) {
                logger.error("datetime.minutes.rangeDown must be >=0");
                throw new RuntimeException("datetime.minutes.rangeDown must be >=0");
            }

            if (this.minutesRangeUp < 0) {
                logger.error("datetime.minutes.rangeUp must be >= 0");
                throw new RuntimeException("datetime.minutes.rangeUp must be >= 0");
            }
        }

        this.secondsMask = configuration.getBooleanValue("datetime.seconds.mask");
        this.secondsRangeUp = configuration.getIntValue("datetime.seconds.rangeUp");
        this.secondsRangeDown = configuration.getIntValue("datetime.seconds.rangeDown");

        if (this.secondsMask) {
            if (this.secondsRangeDown < 0) {
                logger.error("datetime.seconds.rangeDown must be >=0");
                throw new RuntimeException("datetime.seconds.rangeDown must be >=0");
            }

            if (this.secondsRangeUp < 0) {
                logger.error("datetime.seconds.rangeUp must be >= 0");
                throw new RuntimeException("datetime.seconds.rangeUp must be >= 0");
            }
        }

        this.generalizeWeekYear = configuration.getBooleanValue("datetime.generalize.weekyear");
        this.generalizeMonthYear = configuration.getBooleanValue("datetime.generalize.monthyear");
        this.generalizeQuarterYear = configuration.getBooleanValue("datetime.generalize.quarteryear");
        this.generalizeYear = configuration.getBooleanValue("datetime.generalize.year");
        this.generalizeNYearInterval = configuration.getBooleanValue("datetime.generalize.nyearinterval");
        this.generalizeNYearIntervalValue = configuration.getIntValue("datetime.generalize.nyearintervalvalue");

        if (this.generalizeNYearInterval) {
            if (this.generalizeNYearIntervalValue <= 0) {
                logger.error("datetime.generalize.nyearintervalvalue must be >0");
                throw new RuntimeException("datetime.generalize.nyearintervalvalue must be >0");
            }
        }

        this.trimTimeToHourInterval = configuration.getBooleanValue("datetime.mask.trimTimeToHourInterval");
        if (this.trimTimeToHourInterval) {
            int numberOfHourIntervals = configuration.getIntValue("datetime.mask.numberOfIntervals");

            if (numberOfHourIntervals < 1 || numberOfHourIntervals >= 24 || !isGCDOf24(numberOfHourIntervals)) {
                logger.error("Number of intervals must perfectly divide 24");
                throw new IllegalArgumentException("Number of intervals must perfectly divide 24");
            }

            this.numberOfHourIntervals = numberOfHourIntervals;
        } else {
            numberOfHourIntervals = 24;
        }

        this.timeZone = configuration.getStringValue("datetime.format.timezone");
    }

    private boolean isGCDOf24(int numberOfHourIntervals) {
        BigInteger b1 = BigInteger.valueOf(numberOfHourIntervals);
        BigInteger b2 = BigInteger.valueOf(24);
        BigInteger gcd = b1.gcd(b2);
        return b1.equals(gcd);
    }

    private DateTimeFormatter buildFormatter(String fixedDateFormat) {
        DateTimeFormatterBuilder dtf = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(fixedDateFormat)
                .parseDefaulting(ChronoField.YEAR_OF_ERA, 1970)
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0);

        if (fixedDateFormat.endsWith(" a")) {
            dtf.parseDefaulting(ChronoField.HOUR_OF_AMPM, 0);
        } else {
            dtf.parseDefaulting(ChronoField.HOUR_OF_DAY, 0);
        }

        final ZoneId zone;
        if (Objects.nonNull(this.timeZone)) {
            zone = ZoneId.of(this.timeZone);
        } else {
            zone = ZoneId.systemDefault();
        }

        return dtf.toFormatter().withZone(zone);
    }

    @Override
    public String maskWithKey(String identifier, String keyValue) {
        DateTimeFormatter f;

        if (this.fixedDateFormat != null) {
            f = buildFormatter(this.fixedDateFormat);
        } else {
            Tuple<DateTimeFormatter, TemporalAccessor> matchingFormat = dateTimeIdentifier.matchingFormat(identifier);
            if (matchingFormat == null) {
                return RandomGenerators.generateRandomDate(defaultDateFormat);
            }

            f = matchingFormat.getFirst();
        }

        long minMaxDistance = this.keyBasedMaxDays - this.keyBasedMinDays;

        final long hashValue;

        if (minMaxDistance > 0) {
            hashValue = this.keyBasedMinDays + Math.abs(HashUtils.longFromHash(keyValue)) % minMaxDistance;
        } else {
            hashValue = this.keyBasedMinDays;
        }

        TemporalAccessor t = f.parse(identifier);

        if (t.isSupported(ChronoField.HOUR_OF_DAY) || t.isSupported(ChronoField.CLOCK_HOUR_OF_AMPM) ||
                t.isSupported(ChronoField.HOUR_OF_AMPM) || t.isSupported(ChronoField.MINUTE_OF_HOUR) ||
                t.isSupported(ChronoField.SECOND_OF_MINUTE)) {
            LocalDateTime dt = LocalDateTime.from(t); // double parsing for unknown formats
            dt = dt.minusDays(hashValue);
            return dt.format(f);
        } else {
            LocalDate dt = LocalDate.from(t);
            dt = dt.minusDays(hashValue);
            return dt.format(f);
        }
    }

    @Override
    public String mask(String identifier, String fieldName,
                       FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> maskedValues) {

        RelationshipType relationshipType = fieldRelationship.getRelationshipType();

        if (relationshipType == RelationshipType.KEY) {
            return maskWithKey(identifier, maskedValues.get(fieldRelationship.getOperands()[0].getName()).getOriginal());
        }

        String operand = fieldRelationship.getOperands()[0].getName();

        OriginalMaskedValuePair pair = maskedValues.get(operand);
        if (pair == null) {
            return RandomGenerators.generateRandomDate(defaultDateFormat);
        }

        String baseMaskedValue = pair.getMasked();

        if (relationshipType == RelationshipType.EQUALS) {
            return baseMaskedValue;
        }

        LocalDateTime d;
        LocalDateTime originalDate;
        LocalDateTime operandOriginalDate;
        DateTimeFormatter f;

        if (this.fixedDateFormat != null) {
            f = buildFormatter(this.fixedDateFormat);

            if (baseMaskedValue == null) {
                return RandomGenerators.generateRandomDate(f);
            }

            originalDate = LocalDateTime.parse(identifier, f);
            operandOriginalDate = LocalDateTime.parse(maskedValues.get(operand).getOriginal(), f);

            d = LocalDateTime.parse(baseMaskedValue, f);
        } else {
            if (baseMaskedValue == null) {
                return RandomGenerators.generateRandomDate(defaultDateFormat);
            }

            Tuple<DateTimeFormatter, TemporalAccessor> matchingFormat = dateTimeIdentifier.matchingFormat(baseMaskedValue);
            if (matchingFormat == null) {
                return RandomGenerators.generateRandomDate(defaultDateFormat);
            }

            f = matchingFormat.getFirst();

            d = LocalDateTime.from(matchingFormat.getSecond());

            originalDate = LocalDateTime.parse(identifier, f);
            operandOriginalDate = LocalDateTime.parse(maskedValues.get(operand).getOriginal(), f);
        }

        long diff = (operandOriginalDate.toInstant(ZoneOffset.UTC).toEpochMilli() - originalDate.toInstant(ZoneOffset.UTC).toEpochMilli());

        switch (relationshipType) {
            case LESS:
                d = d.minus(diff, ChronoField.MILLI_OF_DAY.getBaseUnit());
                break;
            case GREATER:
                d = d.plus(diff, ChronoField.MILLI_OF_DAY.getBaseUnit());
                break;
            case DISTANCE:
                if (operandOriginalDate.isAfter(originalDate)) {
                    //diff is positive
                    d = d.minus(diff, ChronoField.MILLI_OF_DAY.getBaseUnit());
                } else {
                    //diff is zero or negative
                    d = d.plus(-diff, ChronoField.MILLI_OF_DAY.getBaseUnit());
                }
                break;
            default:
                //XXX we should never reach this point!
                return mask(identifier);
        }

        return d.format(f);
    }


    private String handleError(String identifier, DateTimeFormatter defaultDateFormat) {
        switch (this.failMode) {
            case FailMode.GENERATE_RANDOM:
                return RandomGenerators.generateRandomDate(defaultDateFormat);
            case FailMode.RETURN_ORIGINAL:
                return identifier;
            case FailMode.THROW_ERROR:
                logger.error("invalid date/time");
                throw new RuntimeException("invalid date/time");
            case FailMode.RETURN_EMPTY:
            default:
                return "";
        }
    }

    @Override
    public String mask(String identifier) {
        final DateTimeFormatter dateTimeFormatter;
        final Date date;

        if (fixedDateFormat == null) {
            Tuple<DateTimeFormatter, TemporalAccessor> matchingFormat = dateTimeIdentifier.matchingFormat(identifier);
            if (matchingFormat == null) {
                if (dateTimeIdentifier.isTemporal(identifier)) {
                    return temporalMaskingProvider.mask(identifier);
                }

                if (returnOriginalOnUnknownFormat) {
                    return identifier;
                }

                return handleError(identifier, defaultDateFormat);
            }

            dateTimeFormatter = matchingFormat.getFirst();
            date = Date.from(Instant.from(matchingFormat.getSecond()));
        } else {
            dateTimeFormatter = buildFormatter(this.fixedDateFormat);

            try {
                TemporalAccessor t = dateTimeFormatter.withZone(ZoneOffset.systemDefault()).parse(identifier);
                date = Date.from(Instant.from(t));
            } catch (DateTimeParseException e) {
                return handleError(identifier, dateTimeFormatter);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar originalCal = (Calendar) cal.clone();

        if (shiftDate) {
            cal.add(Calendar.SECOND, this.shiftSeconds);
            return dateTimeFormatter.withZone(ZoneOffset.systemDefault()).format(cal.getTime().toInstant());
        }

        if (generalizeWeekYear) {
            int originalYear = cal.get(Calendar.YEAR);
            int originalWeek = cal.get(Calendar.WEEK_OF_YEAR);
            return String.format("%02d/%d", originalWeek, originalYear);

        }

        if (generalizeMonthYear) {
            int originalYear = cal.get(Calendar.YEAR);
            int originalMonth = cal.get(Calendar.MONTH);
            return String.format("%02d/%d", originalMonth + 1, originalYear);

        }

        if (generalizeQuarterYear) {
            int originalYear = cal.get(Calendar.YEAR);
            int originalMonth = cal.get(Calendar.MONTH);

            int quarter = originalMonth / 3;
            return String.format("%02d/%d", quarter + 1, originalYear);

        }

        if (generalizeYear) {
            int originalYear = cal.get(Calendar.YEAR);
            return Integer.toString(originalYear);
        }

        if (generalizeNYearInterval) {
            int nValue = this.generalizeNYearIntervalValue;
            int originalYear = cal.get(Calendar.YEAR);
            int base = originalYear - (originalYear % nValue);
            return String.format("%d-%d", base, base + nValue);
        }

        if (replaceDaySameClass) {
            int day = cal.get(Calendar.DAY_OF_WEEK);
            if (day >= Calendar.MONDAY && day <= Calendar.FRIDAY) {
                int randomWeekday = Calendar.MONDAY + random.nextInt(5);
                int diff = randomWeekday - day;
                cal.add(Calendar.DAY_OF_MONTH, diff);
            } else {
                boolean flip = random.nextBoolean();
                if (flip) {
                    int diff = day == Calendar.SATURDAY ? 1 : -1;
                    cal.add(Calendar.DAY_OF_MONTH, diff);
                }
            }

            return dateTimeFormatter.withZone(ZoneOffset.systemDefault()).format(cal.getTime().toInstant());
        }

        if (yearMask) {
            int originalYear = cal.get(Calendar.YEAR);
            int randomYear = RandomGenerators.randomWithinRange(originalYear,
                    yearRangeDown, yearRangeUp);
            cal.add(Calendar.YEAR, randomYear - originalYear);
        }

        if (monthMask) {
            int originalMonth = cal.get(Calendar.MONTH);
            int randomMonth = RandomGenerators.randomWithinRange(originalMonth,
                    monthRangeDown, monthRangeUp);
            cal.add(Calendar.MONTH, randomMonth - originalMonth);
        }

        if (dayMask) {
            int originalDay = cal.get(Calendar.DAY_OF_MONTH);
            int randomDay = RandomGenerators.randomWithinRange(originalDay,
                    dayRangeDown, dayRangeUp);
            cal.add(Calendar.DAY_OF_MONTH, randomDay - originalDay);
        }

        if (hourMask) {
            int originalHour = cal.get(Calendar.HOUR_OF_DAY);
            int randomHour = RandomGenerators.randomWithinRange(originalHour,
                    hourRangeDown, hourRangeUp);
            cal.add(Calendar.HOUR_OF_DAY, randomHour - originalHour);
        }

        if (minutesMask) {
            int originalMinutes = cal.get(Calendar.MINUTE);
            int randomMinutes = RandomGenerators.randomWithinRange(originalMinutes,
                    minutesRangeDown, minutesRangeUp);
            cal.add(Calendar.MINUTE, randomMinutes - originalMinutes);
        }

        if (secondsMask) {
            int originalSeconds = cal.get(Calendar.SECOND);
            int randomSeconds = RandomGenerators.randomWithinRange(originalSeconds,
                    secondsRangeDown, secondsRangeUp);
            cal.add(Calendar.SECOND, randomSeconds - originalSeconds);
        }

        if (!yearMask) {
            cal.set(Calendar.YEAR, originalCal.get(Calendar.YEAR));
        }
        if (!monthMask) {
            cal.set(Calendar.MONTH, originalCal.get(Calendar.MONTH));
        }
        if (!dayMask) {
            cal.set(Calendar.DAY_OF_MONTH, originalCal.get(Calendar.DAY_OF_MONTH));
        }
        if (!hourMask) {
            cal.set(Calendar.HOUR_OF_DAY, originalCal.get(Calendar.HOUR_OF_DAY));
        }
        if (!minutesMask) {
            cal.set(Calendar.MINUTE, originalCal.get(Calendar.MINUTE));
        }
        if (!secondsMask) {
            cal.set(Calendar.SECOND, originalCal.get(Calendar.SECOND));
        }

        if (trimTimeToHourInterval) {
            cal.set(Calendar.HOUR_OF_DAY, toInterval(originalCal.get(Calendar.HOUR_OF_DAY), numberOfHourIntervals));
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        LocalDateTime givenDate = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
        return givenDate.format(dateTimeFormatter);
    }


    private int toInterval(final int hours, final int numberOfHourIntervals) {
        final int intervalSize = 24 / numberOfHourIntervals;
        final int intervals = hours / intervalSize;
        return intervals * intervalSize;
    }

}
