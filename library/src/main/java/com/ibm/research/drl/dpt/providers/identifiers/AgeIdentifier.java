/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.Age;
import com.ibm.research.drl.dpt.models.AgePortion;
import com.ibm.research.drl.dpt.models.AgePortionFormat;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.util.NumberUtils;
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgeIdentifier extends AbstractRegexBasedIdentifier implements IdentifierWithOffset {
    private static final AgePortion MISSING_AGE_PORTION = new AgePortion(false, -1, -1, AgePortionFormat.NUMERICAL);

    private final static String SUFFIXES = "(?:man|woman|male|female|daughter|son|niece|nephew|lady|gentleman)";

    private static final Collection<Pattern> agePatternsWithGender = new ArrayList<Pattern>(
            Arrays.asList(
                    Pattern.compile("^(?<year>[0-9]+)\\s*yrs\\.?\\s+" + SUFFIXES),
                    Pattern.compile("^(?<year>[0-9]+)\\s*years\\.?\\s+" + SUFFIXES),
                    Pattern.compile("^(?<year>[0-9]+)\\s*y/o\\.?\\s+" + SUFFIXES),
                    Pattern.compile("^(?<year>[0-9]+)\\s*yo\\.?\\s+" + SUFFIXES)
            )
    );

    private static final Collection<Pattern> agePatterns = new ArrayList<Pattern>(
            Arrays.asList(
                    Pattern.compile("^(?<year>[0-9]+)\\s+year old$"),
                    Pattern.compile("^(?<year>[0-9]+)-year old$"),
                    Pattern.compile("^(?<year>[0-9]+)-year-old$"),
                    Pattern.compile("^(?<year>[0-9]+)\\s+years\\s+of\\s+age$"),

                    Pattern.compile("^(?<year>[0-9]+)\\s+years old$"),
                    Pattern.compile("^(?<year>[0-9]+)-years old$"),
                    Pattern.compile("^(?<year>[0-9]+)-years-old$"),

                    Pattern.compile("^(?<year>[0-9]+)\\s+yrs\\.?\\s+old$"),
                    Pattern.compile("^(?<year>[0-9]+)-yrs\\.?\\s+old$"),
                    Pattern.compile("^(?<year>[0-9]+)-yrs-old$"),
                    //Pattern.compile("^(?<year>[0-9]+)yrs\\.?$"),

                    Pattern.compile("^(?<year>[0-9]+)\\s+yr(\\.)? old$"),
                    Pattern.compile("^(?<year>[0-9]+)-yr(\\.)? old$"),
                    Pattern.compile("^(?<year>[0-9]+)-yr-old$"),

                    Pattern.compile("^(?<year>[0-9]+)\\s+yo$"),
                    Pattern.compile("^(?<year>[0-9]+)\\s+y/o$"),

                    Pattern.compile("^(?<year>[0-9]+)\\s+(?<month>[0-9]+)/[0-9]+\\s+y/?o$"),

                    Pattern.compile("^(?<week>[0-9]+) weeks old$"),
                    Pattern.compile("^(?<week>[0-9]+)weeks old$"),
                    Pattern.compile("^(?<week>[0-9]+)-weeks old$"),
                    Pattern.compile("^(?<week>[0-9]+)-weeks-old$"),

                    Pattern.compile("^(?<month>[0-9]+) months old$"),
                    Pattern.compile("^(?<month>[0-9]+)months old$"),
                    Pattern.compile("^(?<month>[0-9]+)-months old$"),
                    Pattern.compile("^(?<month>[0-9]+)-months-old$"),

                    Pattern.compile("^at age\\s+(?<year>[0-9]+)$"),
                    Pattern.compile("^at age\\s+of\\s+(?<year>[0-9]+)$"),
                    Pattern.compile("^at age\\s+(?<year>[0-9]+) and (?<month>[0-9]+)$"),
                    Pattern.compile("^at age\\s+(?<year>[0-9]+) and (?<month>[0-9]+)/[0-9]+$"),

                    Pattern.compile("^at the age of (?<year>[0-9]+)$"),

                    Pattern.compile("^(?<year>[0-9]+) year and (?<month>[0-9]+) month$"),
                    Pattern.compile("^(?<year>[0-9]+) years and (?<month>[0-9]+) months$"),
                    Pattern.compile("^(?<year>[0-9]+) yrs and (?<month>[0-9]+) month$"),
                    Pattern.compile("^(?<year>[0-9]+)yr and (?<month>[0-9]+)mo$"),

                    Pattern.compile("^(?<year>[0-9]+) yrs (?<month>[0-9]+)/[0-9]+ mo$"),
                    Pattern.compile("^(?<year>[0-9]+) years (?<month>[0-9]+)/[0-9]+ mo$"),

                    Pattern.compile("^dob:\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^dob\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^date\\s+of\\s+birth\\s*:\\s+(?<day>[0-9]+)/(?<month>[0-9]+)/(?<year>[0-9]+)"),
                    Pattern.compile("^date\\s+of\\s+birth\\s*:\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^dob:\\s+[0-9][0-9]-(?<month>[0-9]+)-(?<year>[0-9]+)"), //DOB: 03-28-1934
                    Pattern.compile("^dob:\\s+[0-9][0-9]/(?<month>[0-9]+)/(?<year>[0-9]+)"), //DOB: 03/28/1934
                    Pattern.compile("^dob\\s+[0-9][0-9]-(?<month>[0-9]+)-(?<year>[0-9]+)") //DOB 03-28-1934
                    ,
                    Pattern.compile("age:\\s*(?<year>[1-9][0-9]*)"),
                    Pattern.compile("alive\\s+(?<year>[1-9][0-9]*)"),
                    Pattern.compile("comment:\\s*age\\s+(?<year>[1-9][0-9]*)"),
                    Pattern.compile("comments:\\s*age\\s+(?<year>[1-9][0-9]*)"),
                    Pattern.compile("comments:\\s*died\\s+age\\s+(?<year>[1-9][0-9]*)"),

                    Pattern.compile("^died\\s+age\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^deceased\\s+age\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^deceased\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^died\\s+at\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^died\\s+(?<year>[0-9]+)-old\\s+age"),
                    Pattern.compile("^died\\s+of\\s+(?<cause>[\\w|'|-]+\\s+){1,3}at\\s+(?<year>[0-9]+)$"),
                    Pattern.compile("^died\\s+of\\s+(?<cause>[\\w|'|-]+\\s+){1,3}at\\s+age\\s+(of\\s+)?(?<year>[0-9]+)$"),
                    Pattern.compile("^passed\\s+away\\s+at\\s+age\\s+(?<year>[0-9]+)"),
                    Pattern.compile("^passed\\s+away\\s+from\\s+(?<cause>[\\w|'|-]+\\s+){1,3}at\\s+age\\s+(of\\s+)?(?<year>[0-9]+)")

            )
    );

    private static final Set<String> suffixes = new HashSet<>(
            Arrays.asList("year old", "years old", "yrs old", "months old", "days old", "weeks old", "-years-old"));

    @Override
    public ProviderType getType() {
        return ProviderType.AGE;
    }

    @Override
    public String getDescription() {
        return "Age detection";
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return agePatterns;
    }

    public Age parseAge(String identifier) {

        AgePortion yearPortion = MISSING_AGE_PORTION;
        AgePortion monthPortion = MISSING_AGE_PORTION;
        AgePortion weekPortion = MISSING_AGE_PORTION;
        AgePortion daysPortion = MISSING_AGE_PORTION;

        for (Pattern p : getPatterns()) {
            Matcher matcher = p.matcher(identifier);
            if (matcher.matches()) {
                try {
                    int start = matcher.start("year");
                    int end = matcher.end("year");
                    yearPortion = new AgePortion(true, start, end, AgePortionFormat.NUMERICAL);
                } catch (IllegalArgumentException e) {
                }

                try {
                    int start = matcher.start("month");
                    int end = matcher.end("month");
                    monthPortion = new AgePortion(true, start, end, AgePortionFormat.NUMERICAL);
                } catch (IllegalArgumentException e) {
                }

                try {
                    int start = matcher.start("week");
                    int end = matcher.end("week");
                    weekPortion = new AgePortion(true, start, end, AgePortionFormat.NUMERICAL);
                } catch (IllegalArgumentException e) {
                }

                try {
                    int start = matcher.start("day");
                    int end = matcher.end("day");
                    daysPortion = new AgePortion(true, start, end, AgePortionFormat.NUMERICAL);
                } catch (IllegalArgumentException e) {
                }

                return new Age(yearPortion, monthPortion, weekPortion, daysPortion);
            }
        }

        return tryWordParse(identifier);
    }


    @Override
    public boolean isOfThisType(String identifier) {
        return (isOfThisTypeWithOffset(identifier).getFirst());
    }

    private Age tryWordParse(String identifier) {
        AgePortion yearPortion = MISSING_AGE_PORTION;
        AgePortion monthPortion = MISSING_AGE_PORTION;
        AgePortion weekPortion = MISSING_AGE_PORTION;
        AgePortion daysPortion = MISSING_AGE_PORTION;

        String input = identifier.toLowerCase();

        if ((input.startsWith("on his") || input.startsWith("on her")) && input.endsWith("birthday")) {
            String middlePart = input.substring("on his".length(), input.length() - "birthday".length());
            Long number = NumberUtils.createNumber(middlePart);

            if (number != null) {
                AgePortion portion = new AgePortion(true, "on his".length(), input.length() - "birthday".length(), AgePortionFormat.WORDS);
                return new Age(portion, monthPortion, weekPortion, daysPortion);
            }
        }

        for (String suffix : suffixes) {
            if (input.endsWith(suffix)) {
                input = input.substring(0, input.length() - suffix.length());
                if (NumberUtils.createNumber(input) != null) {
                    AgePortion portion = new AgePortion(true, 0, input.trim().length(), AgePortionFormat.WORDS);

                    if (suffix.contains("years") || suffix.contains("yrs")) {
                        yearPortion = portion;
                    } else if (suffix.contains("months")) {
                        monthPortion = portion;
                    } else if (suffix.contains("days")) {
                        daysPortion = portion;
                    } else if (suffix.contains("weeks") || suffix.contains("wks")) {
                        weekPortion = portion;
                    }

                    return new Age(yearPortion, monthPortion, weekPortion, daysPortion);
                }
            }
        }

        return null;
    }

    private boolean tryBirthdayPatterns(String input) {
        if ((input.startsWith("on his") || input.startsWith("on her")) && input.endsWith("birthday")) {
            String middlePart = input.substring("on his".length(), input.length() - "birthday".length()).trim();
            return NumberUtils.createNumberOrder(middlePart) != null;
        }

        return false;
    }

    private boolean tryWordMatch(String input) {
        if (tryBirthdayPatterns(input)) {
            return true;
        }

        for (String suffix : suffixes) {
            if (input.endsWith(suffix)) {
                input = input.substring(0, input.length() - suffix.length());
                if (NumberUtils.createNumber(input) != null) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String identifier) {

        identifier = identifier.toLowerCase();

        if (this.matches(identifier)) {
            return new Tuple<>(true, new Tuple<>(0, identifier.length()));
        }

        Tuple<Boolean, Tuple<Integer, Integer>> tryAuxResults = checkAuxiliaryPatterns(identifier);
        if (tryAuxResults.getFirst()) {
            return tryAuxResults;
        }

        if (tryWordMatch(identifier)) {
            return new Tuple<>(true, new Tuple<>(0, identifier.length()));
        }

        return new Tuple<>(false, null);
    }

    private Tuple<Boolean, Tuple<Integer, Integer>> checkAuxiliaryPatterns(String identifier) {
        for (Pattern p : agePatternsWithGender) {
            if (p.matcher(identifier).matches()) {
                int idx = identifier.lastIndexOf(' ');
                return new Tuple<>(true, new Tuple<>(0, idx));
            }
        }

        return new Tuple<>(false, null);
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }

    @Override
    public int getMinimumLength() {
        return 4;
    }

    @Override
    public int getMaximumLength() {
        return 0;
    }
}
