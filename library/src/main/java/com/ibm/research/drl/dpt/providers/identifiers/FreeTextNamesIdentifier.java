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
import com.ibm.research.drl.dpt.util.Tuple;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class FreeTextNamesIdentifier extends AbstractIdentifier implements IdentifierWithOffset {

    //JMS, DMY, 
    private static final Set<String> prefixes = new HashSet<>(Arrays.asList("Dr.", "Dr ", "Mr.", "Ms.", "Mrs.", "Mr ", "Ms ", "Mrs "));
    private static final Set<String> titles = new HashSet<>(Arrays.asList("MD", "M.D.", "RN", "Rn",
            "PTA", "LPN", "DDS", "NP", "CDE", "OD", "DOE", "DMD", "MCF", "APRN", "PHD", "Ph.D", "PA"));

    private static final Set<String> titlesWithTrailingDots = new HashSet<>(Arrays.asList("M.D.", "Ph.D."));

    private static final Set<String> specialNamePrefixes = new HashSet<>(Arrays.asList("van", "de", "la", "der", "den"));

    @Override
    public ProviderType getType() {
        return ProviderType.NAME;
    }

    @Override
    public boolean isOfThisType(String data) {
        return isOfThisTypeWithOffset(data).getFirst();
    }

    @Override
    public String getDescription() {
        return "Medical Names";
    }

    /* returns true if the name looks valid and the length of the medical title */

    private Tuple<Boolean, Tuple<Boolean, Integer>> validateMedicalName(String data, int offset) {
        try {
            data = data.substring(offset);

            if (data.isEmpty()) {
                return new Tuple<>(false, null);
            }

            String[] tokens = data.split("[\\s+|,]");

            if (tokens.length == 0) {
                return new Tuple<>(false, null);
            }

            boolean[] isTokenAMedicalTitle = new boolean[tokens.length];
            boolean containsMedicalTitle = false;

            for (int i = 0; i < tokens.length; i++) {
                isTokenAMedicalTitle[i] = isMedicalTitle(tokens[i]);
                if (isTokenAMedicalTitle[i]) {
                    if (i == 0) {
                        return new Tuple<>(false, null);
                    }
                    containsMedicalTitle = true;
                }
            }

            int totalTC = 0;
            int totalUC = 0;

            for (int i = 0; i < tokens.length; i++) {
                if (isTokenAMedicalTitle[i]) {
                    continue;
                }

                if (tokens[i].isEmpty()) {
                    continue;
                }

                if (containsIllegalCharacters(tokens[i])) {
                    return new Tuple<>(false, null);
                }

                if (isSpecialNamePrefix(tokens[i])) {
                    continue;
                }

                boolean tc = isTitlecase(tokens[i]);
                boolean uc = isAllUppercase(tokens[i]);

                if (!tc && !uc) {
                    return new Tuple<>(false, null);
                }

                totalTC += tc ? 1 : 0;

                if (!isAbbreviation(tokens[i])) {
                    totalUC += uc ? 1 : 0;
                }
            }

            if (totalTC > 0 && totalUC > 0) {
                return new Tuple<>(false, null);
            }

            int suffixLength = 0;
            int startIndex = isTokenAMedicalTitle.length - 1;

            while (startIndex >= 0 && isTokenAMedicalTitle[startIndex]) {
                suffixLength += tokens[startIndex].length();

                int whitespaces = 0;

                for (int i = data.length() - suffixLength - 1; i >= 0; i--) {
                    char ch = data.charAt(i);
                    if (!Character.isWhitespace(ch) && !(ch == ',')) {
                        break;
                    }

                    whitespaces++;
                }

                suffixLength += whitespaces;
                startIndex--;
            }

            return new Tuple<>(true, new Tuple<>(containsMedicalTitle, suffixLength));

        } catch (IllegalArgumentException ignore) {
            return new Tuple<>(false, null);
        }
    }

    private boolean isSpecialNamePrefix(String token) {
        return specialNamePrefixes.contains(token);
    }

    private boolean containsIllegalCharacters(String token) {
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);

            if (ch == '/') {
                return true;
            }
        }

        return false;
    }

    private boolean isAbbreviation(String token) {
        //John J. Smith
        //John H Smith
        return (token.length() >= 2 && token.length() <= 3 && token.endsWith("."))
                || (token.length() == 1 && Character.isUpperCase(token.charAt(0)));
    }


    public static boolean isMedicalTitle(String data) {
        if (titlesWithTrailingDots.contains(data)) {
            return true;
        }

        if (data.endsWith(".")) {
            data = data.substring(0, data.length() - 1);
        }

        String[] tokens = data.split("/");

        for (String token : tokens) {
            if (titles.contains(token)) {
                return true;
            }
        }

        return false;
    }

    private final static Pattern invalidCharactersPatterns = Pattern.compile("[^, \\.\\-a-zA-Z\\(\\)\\/']+");

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data) {
        if (data.endsWith(":")) {
            data = data.substring(0, data.length() - 1);
        }

        if (invalidCharactersPatterns.matcher(data).find()) {
            return new Tuple<>(false, null);
        }

        int offset = 0;

        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (Character.isWhitespace(ch) || ch == ',') {
                offset += 1;
            } else {
                break;
            }
        }

        int depth = data.length() - offset;
        boolean hasPrefix = false;

        //In case it starts with a prefix, like "Dr. John Doe, MD" or "Dr.John Doe" 
        for (String prefix : prefixes) {
            if ((data.length() - offset) <= prefix.length()) {
                continue;
            }

            if (data.substring(offset, offset + prefix.length()).equalsIgnoreCase(prefix)) {
                int prefixOffset = prefix.length();

                for (int i = offset + prefixOffset; i < data.length(); i++) {
                    if (Character.isWhitespace(data.charAt(i))) {
                        prefixOffset++;
                    } else {
                        break;
                    }
                }

                offset += prefixOffset;
                depth -= prefixOffset;
                hasPrefix = true;
                break;
            }
        }

        Tuple<Boolean, Tuple<Boolean, Integer>> titleLocation = validateMedicalName(data, offset);
        if (!titleLocation.getFirst()) {
            return new Tuple<>(false, null);
        }

        boolean containsTitle = titleLocation.getSecond().getFirst();

        if (!hasPrefix && !containsTitle) {
            return new Tuple<>(false, null);
        }

        depth -= titleLocation.getSecond().getSecond();

        for (int i = data.length() - 1; i >= 0; i--) {
            char ch = data.charAt(i);
            if (Character.isWhitespace(ch) || ch == ',') {
                depth -= 1;
            } else {
                break;
            }
        }


        if (data.endsWith("'s")) {
            depth -= 2;
        } else if (data.endsWith("'")) {
            depth -= 1;
        }

        return new Tuple<>(true, new Tuple<>(offset, depth));
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public int getMinimumLength() {
        return 3;
    }

    @Override
    public int getMaximumLength() {
        return 42;
    }

}
