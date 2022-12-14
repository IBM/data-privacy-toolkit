/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class LiteralDateIdentifier extends AbstractIdentifier {
    private static final MonthIdentifier monthManager = new MonthIdentifier();
    private static final String[] appropriateNames = {"Date", "Data"};

    private final YOBIdentifier yobIdentifier = new YOBIdentifier();
    private final Pattern yearPattern = Pattern.compile("\\d{2}(?:\\d{2})?");
    private final Pattern dayIdentifier = Pattern.compile("\\d{1,2}\\s*(?:th)");
    private final Pattern dayPattern = Pattern.compile("(?:0?[1-9])|(?:[12][0-9])|(?:3[01])}");

    @Override
    public ProviderType getType() {
        return ProviderType.DATETIME;
    }

    @Override
    public boolean isOfThisType(String data) {
        List<String> parts = new ArrayList<>(Arrays.asList(data.strip().split("\\s+")));

        if (parts.size() < 2 || parts.size() > 3) return false;

        int count = 0;

        Iterator<String> it;

        it = parts.iterator();
        while (it.hasNext()) {
            String part = it.next();

            if (dayIdentifier.matcher(part).matches() ||
                    dayPattern.matcher(part).matches()
            ) {
                count += 1;
                it.remove();
                break;
            }
        }
        it = parts.iterator();
        while (it.hasNext()) {
            String part = it.next();

            if (monthManager.isOfThisType(part)) {
                count += 1;
                it.remove();
                break;
            }
        }
        it = parts.iterator();
        while (it.hasNext()) {
            String part = it.next();

            if (yobIdentifier.isOfThisType(part) ||
                    yearPattern.matcher(part).matches()
            ) {
                count += 1;
                it.remove();
                break;
            }
        }

        return parts.isEmpty() && count > 1;
    }

    @Override
    public String getDescription() {
        return "Identifier for dates in the form day month year|day month|month year";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return 0;
    }

    @Override
    public int getMinimumLength() {
        return 4;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}
