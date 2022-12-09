/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemporalAnnotationMaskingProvider extends AbstractMaskingProvider {
    private final static String DAY = "day";
    private final static String WEEK = "week";
    private final static String MONTH = "month";
    private final static String YEAR = "year";

    final static Pattern dayBefore = Pattern.compile("\\bday\\s+before\\b");
    private final static Pattern daysBefore = Pattern.compile("\\bdays\\s+before\\b");

    final static Pattern getNumber = Pattern.compile("(\\d+)");

    public TemporalAnnotationMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
    }

    @Override
    public String mask(String identifier) {
        identifier = identifier.toLowerCase();
        final String temporalPeriod;

        if (identifier.contains("today") || identifier.contains("now") || identifier.contains("yesterday") || identifier.contains("tomorrow")) {
            temporalPeriod = DAY;
        } else if (identifier.contains("week")) {
            temporalPeriod = WEEK;
        } else if (identifier.contains("month")){
            temporalPeriod = MONTH;
        } else {
            temporalPeriod = YEAR;
        }


        final int identifierPeriods;
        if (identifier.contains("now") || identifier.contains("today")) {
            identifierPeriods = 0;
        } else if (identifier.contains("yesterday")) {
            if (dayBefore.matcher(identifier).find()) {
                identifierPeriods = -2;
            } else if (daysBefore.matcher(identifier).find()) {
                identifierPeriods = - (extractNumbers(identifier) + 1);
            } else {
                identifierPeriods = -1;
            }
        } else if (identifier.contains("tomorrow")) {
            identifierPeriods = +1;
        } else {
            identifierPeriods = extractNumbers(identifier);
        }

        final int numberOfPeriods = random.nextInt(10) - 5 + identifierPeriods;

        if (numberOfPeriods == 0) return identifier;
        if (numberOfPeriods < 0) {
            if (DAY.equals(temporalPeriod)) {
                if (-1 == numberOfPeriods) return "yesterday";
            }
            return Math.abs(numberOfPeriods) + " " + temporalPeriod + getIfPlural(numberOfPeriods) + " ago";
        } else {
            if (DAY.equals(temporalPeriod)) {
                if (identifier.contains("yesterday")) {
                    if (2 == numberOfPeriods) return "tomorrow";
                    if (1 == numberOfPeriods) return "today";
                }
            }
            return numberOfPeriods + " " + temporalPeriod + getIfPlural(numberOfPeriods);
        }
    }

    private String getIfPlural(int numberOfPeriods) {
        if (1 != Math.abs(numberOfPeriods)) {
            return "s";
        }
        return "";
    }

    private int extractNumbers(String identifier) {
        final Matcher matcher = getNumber.matcher(identifier);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}
