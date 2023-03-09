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
