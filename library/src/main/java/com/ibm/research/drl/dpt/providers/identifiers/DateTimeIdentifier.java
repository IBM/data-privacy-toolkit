/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.Tuple;

import java.text.ParsePosition;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class DateTimeIdentifier extends AbstractIdentifier {
    private static final String[] appropriateNames = {"Datetime", "Timestamp", "Birthday", "Birth date", "Date", "BirthDate", "Date of birth"};

    private static final Tuple<String, Pattern>[] patterns = new Tuple[]{
            new Tuple<>("dd-MM-yyyy HH:mm:ss", Pattern.compile("^\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}$")),
            new Tuple<>("yyyy-MM-dd HH:mm:ss", Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")),
            new Tuple<>("d/M/yyyy HH:mm:ss", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$")),
            new Tuple<>("d/M/yyyy", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$")),
            new Tuple<>("MM/dd/yyyy", Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$")),
            new Tuple<>("yyyy/MM/dd", Pattern.compile("^\\d{4}/\\d{2}/\\d{2}$")),
            new Tuple<>("d-MM-yyyy", Pattern.compile("^\\d{1,2}-\\d{2}-\\d{4}$")),
            new Tuple<>("MM-dd-yyyy", Pattern.compile("^\\d{2}-\\d{2}-\\d{4}$")),
            new Tuple<>("yyyy-MM-dd", Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$")),
            new Tuple<>("M/dd/yyyy", Pattern.compile("^\\d{1}/\\d{2}/\\d{4}$")),
            new Tuple<>("d/MM/yyyy", Pattern.compile("^\\d{1}/\\d{2}/\\d{4}$")),
            new Tuple<>("dd.MM.yyyy", Pattern.compile("^\\d{2}.\\d{2}.\\d{4}$")),
            new Tuple<>("MM.dd.yyyy", Pattern.compile("^\\d{2}.\\d{2}.\\d{4}$")),
            new Tuple<>("ddMMyyyyHHmm", Pattern.compile("^\\d{2}\\d{2}\\d{4}\\d{2}\\d{2}$")),
            new Tuple<>("dd-MMM-yyyy", Pattern.compile("^\\d{1,2}-\\w{3,}-\\d{4}$")),
            new Tuple<>("dd-MMMM-yyyy", Pattern.compile("^\\d{1,2}-\\w{3,}-\\d{4}$")),
            new Tuple<>("MMM d, yyyy", Pattern.compile("^\\w{3} \\d{1,2}, \\d{4}$")),
            new Tuple<>("MMM d,yyyy", Pattern.compile("^\\w{3} \\d{1,2},\\d{4}$")),
            new Tuple<>("MMM d yyyy", Pattern.compile("^\\w{3} \\d{1,2} \\d{4}$")),
            new Tuple<>("MMMM d, yyyy", Pattern.compile("^\\w{3,} \\d{1,2}, \\d{4}$")),
            new Tuple<>("MMMM d,yyyy", Pattern.compile("^\\w{3,} \\d{1,2},\\d{4}$")),
            new Tuple<>("MMMM d yyyy", Pattern.compile("^\\w{3,} \\d{1,2} \\d{4}$")),
            new Tuple<>("EEE, dd MMM yyyy HH:mm:ss z", Pattern.compile("^\\w{3}, \\d{2} \\w{3} \\d{4} \\d{2}:\\d{2}:\\d{2} \\w{1,}$")),
            new Tuple<>("EEE MMM d HH:mm:ss yyyy", Pattern.compile("^\\w{3} \\w{3} \\d{1,2} \\d{1,2}:\\d{2}:\\d{2} \\d{4}$")),
            new Tuple<>("M/d/YY", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{2}")),
            new Tuple<>("MM/dd/yyyy HH:mm:ss", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$")),
            new Tuple<>("MMMM 'of' yyyy", Pattern.compile("^\\w{3,} of \\d{4}")),
            new Tuple<>("MMMM yyyy", Pattern.compile("^\\w{3,} \\d{4}")),
            new Tuple<>("dd-MM-yy", Pattern.compile("^\\d{2}-\\d{2}-\\d{2}$")),
            new Tuple<>("d MMMM ''yy", Pattern.compile("^\\d{1,2} \\w{3,} '\\d{2}")),
            new Tuple<>("''yy-MMMM", Pattern.compile("^'\\d{2}-\\w{3,}")),
            new Tuple<>("dd MMM yyyy", Pattern.compile("^\\d{1,2} \\w{3,} \\d{4}$")),
            new Tuple<>("dd MMMM yyyy", Pattern.compile("^\\d{1,2} \\w{3,} \\d{4}$")),
            new Tuple<>("MMMM d", Pattern.compile("^\\w{3,}\\s+\\d{1,2}$")),
            new Tuple<>("MMMM d 'of this year'", Pattern.compile("^\\w{3,}\\s+\\d{1,2} of this year$")),
            new Tuple<>("d-MM-yy", Pattern.compile("^\\d{1,2}-\\d{2}-\\d{2}$")),
            new Tuple<>("yyyy-MM-dd'T'HH:mm:ss.SSSX", Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{1,4}Z$")),
            new Tuple<>("yyyy-MM-dd'T'HH:mm:ssX", Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$")),
            new Tuple<>("yyyy-MM-dd HH:mm:ss.SSSSSS", Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1,}$")),
            new Tuple<>("yyyy-MM-dd HH:mm:ss.SSS", Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1,}$")),
            new Tuple<>("yyyy-MM-dd HH:mm:ss.S", Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{1}$")),
    };

    private static final Tuple<String, Pattern>[] ampmPatterns = new Tuple[]{
            new Tuple<>("d/M/yyyy,K:mm a", Pattern.compile("^\\d{2}/\\d{2}/\\d{4},\\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("M/d/yyyy,K:mm a", Pattern.compile("^\\d{2}/\\d{2}/\\d{4},\\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("EEE MMM d, yyyy K:mm a", Pattern.compile("^\\w{3} \\w{3} \\d{1,2}, \\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("d/M/yyyy 'at' K:mm a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} at \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("M/d/yyyy 'at' K:mm a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} at \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("d/M/yyyy k:mm a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("d/M/yyyy K:mm a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("d/M/yyyy K:mm:ss a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("MMM d, yyyy k:mm a", Pattern.compile("^\\w{3} \\d{1,2}, \\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("MMM d, yyyy K:mm a", Pattern.compile("^\\w{3} \\d{1,2}, \\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("MMM dd,yyyy h:mm a", Pattern.compile("^\\w{3} \\d{1,2},\\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("MMM dd yyyy h:mm a", Pattern.compile("^\\w{3} \\d{1,2} \\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("MMMM d, yyyy K:mm a", Pattern.compile("^\\w{3,} \\d{1,2}, \\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("MMMM d, yyyy h:mm a", Pattern.compile("^\\w{3,} \\d{1,2}, \\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("M/d/y K:mm a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("M/d/y k:mm a", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4} \\d{1,2}:\\d{2}\\s*[A|P]M$")),
            new Tuple<>("M/d/y 'at' K:mma", Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{2,} at \\d{1,2}:\\d{2}[A|P]M$")),
            new Tuple<>("MMMM d 'at' Ka", Pattern.compile("^\\w{3,} \\d{1,2} at \\d{1,2}[A|P]M$")),
    };

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    private static final DateTimeFormatter[] dateFormats;
    private static final DateTimeFormatter[] ampmDateFormats;

    static {
        dateFormats = new DateTimeFormatter[patterns.length];
        ampmDateFormats = new DateTimeFormatter[ampmPatterns.length];

        for (int i = 0; i < patterns.length; i++) {
            dateFormats[i] = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern((patterns[i].getFirst()))
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                    .toFormatter()
                    .withZone(ZoneOffset.systemDefault());
        }

        for (int i = 0; i < ampmPatterns.length; i++) {
            ampmDateFormats[i] = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern((ampmPatterns[i].getFirst()))
                    .parseDefaulting(ChronoField.HOUR_OF_AMPM, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.systemDefault());
        }
    }

    @Override
    public ProviderType getType() {
        return ProviderType.DATETIME;
    }

    /**
     * Matching format date format.
     *
     * @param data the data
     * @return the date format
     */
    public Tuple<DateTimeFormatter, TemporalAccessor> matchingFormat(String data) {

        int digits = 0;

        for (int i = 0; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                digits++;
            }
        }

        if (digits == 0) {
            return null;
        }

        data = data.replaceAll("(?<=\\d)(st|nd|rd|th)", "").replaceAll("\\s+", " ");

        if (data.endsWith("am")) {
            data = data.substring(0, data.length() - 2) + "AM";
        } else if (data.endsWith("pm")) {
            data = data.substring(0, data.length() - 2) + "PM";
        }

        return checkAllPaterns(data);
    }

    private Tuple<DateTimeFormatter, TemporalAccessor> checkAllPaterns(String data) {
        Tuple<DateTimeFormatter, TemporalAccessor> f = checkPatterns(data, patterns, dateFormats);
        if (f != null) {
            return f;
        }

        return checkPatterns(data, ampmPatterns, ampmDateFormats);
    }

    private Tuple<DateTimeFormatter, TemporalAccessor>
    checkPatterns(String data, Tuple<String, Pattern>[] patterns, DateTimeFormatter[] dateFormats) {

        for (int i = 0; i < patterns.length; i++) {
            Pattern p = patterns[i].getSecond();
            if (p.matcher(data).matches()) {
                try {
                    DateTimeFormatter f = dateFormats[i];
                    ParsePosition position = new ParsePosition(0);
                    TemporalAccessor t = f.parse(data, position);
                    int index = position.getIndex();
                    if (index == 0 || index != data.length()) {
                        continue;
                    }
                    return new Tuple<>(f, t);
                } catch (Exception ignored) {
                }
            }
        }

        return null;
    }

    /**
     * Parse tuple.
     *
     * @param data the data
     * @return the tuple
     */

    private final static String[] temporalSubstrings = {"ago", "last", "next", "every", "day", "week", "month", "year"};

    public boolean isTemporal(String identifier) {
        String p = identifier.toLowerCase();

        for (String pattern : temporalSubstrings) {
            if (p.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOfThisType(String data) {

        if (data.length() <= 6 || data.length() >= 64) {
            return false;
        }

        return matchingFormat(data.trim()) != null;

    }

    @Override
    public String getDescription() {
        return "Date and time identification. Formats recognized are dd-MM-yyyy HH:mm:ss and dd/MM/yyyy HH:mm:ss";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }

    @Override
    public int getMinimumLength() {
        return 6;
    }

    @Override
    public int getMaximumLength() {
        return 42;
    }
}
