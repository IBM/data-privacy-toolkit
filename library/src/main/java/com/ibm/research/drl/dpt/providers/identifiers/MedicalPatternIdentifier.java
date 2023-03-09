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
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedicalPatternIdentifier extends AbstractIdentifier implements IdentifierWithOffset {
    public static Collection<Pattern> patterns = Arrays.asList(
            // more
            Pattern.compile("MRN:?\\s*((?:CLM-)?(?:\\p{Alnum}+-)*\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),

            // Scubber: 11A
            Pattern.compile("Medical\\s+Record\\s+Number:?\\s+(\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Medical\\s+Record\\s*(?:#|No.):?\\s*(\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("mr[n#]\\s*:?\\s*#?\\s*((?:\\p{Alnum}+[_-])*\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("mr #?((?:\\d+[_-])+\\d+)", Pattern.CASE_INSENSITIVE),

            Pattern.compile("Patient\\s+ID(?: No)?\\s*:?\\s*([\\d-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Chart\\s+(?:number|No.|#)?:\\s*([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("account\\s+(?:number|No.|#)\\s*(#[\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            // Scubber: 11B
            Pattern.compile("Specimen\\s*(?:#|No.):?([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("NM\\s*(?:#|No.):?([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Accession\\s*(?:#|No.):?([\\p{javaDigit}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("DNA\\s*(?:#|No.):?([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            //
            Pattern.compile("order\\s*(?:#|No.):?\\s*([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("exam\\s*(?:#|No.):?\\s*(\\p{Digit}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("tracking\\s*(?:#|No.):?\\s*([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("dictation\\s*(?:#|No.):?\\s*([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("authorization\\s*(?:#|No.):?\\s*([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("prescription\\s*(?:#|No.):?\\s*([\\p{Alnum}_-]+)", Pattern.CASE_INSENSITIVE),

            // generic IDs
            Pattern.compile("NIV#\\s+\\(comments here\\):\\s+(\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("LOT\\s*#\\s+(\\p{Digit}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("rpt\\s*#\\s+(\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:NIA\\s+)?auth\\s?#\\s+(\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Visit Note ID:\\s+(\\p{Alnum}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Note ID:\\s*((?:\\p{Digit}+-)*\\p{Digit}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Claim #:\\s*((?:\\p{Digit}+-)*\\p{Digit}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("Member ID#\\s*(\\p{Digit}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("NIZ\\s*#\\s+(\\p{Digit}+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("approval\\s+number\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE)
    );

    @Override
    public ProviderType getType() {
        return ProviderType.MRN;
    }

    @Override
    public boolean isOfThisType(String data) {
        return isOfThisTypeWithOffset(data).getFirst();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String data) {
        Tuple<Boolean, Tuple<Integer, Integer>> response = patterns.stream()
                .map(
                        pattern -> pattern.matcher(data)
                )
                .filter(Matcher::matches)
                .map(matcher -> new Tuple<>(true, new Tuple<>(matcher.start(1), matcher.end(1) - matcher.start(1))))
                .reduce((a, b) -> a).orElse(new Tuple<>(false, null));

        if (response.getFirst() && !containsDigit(extractText(data, response.getSecond()))) {
            return new Tuple<>(false, null);
        }

        return response;
    }

    private boolean containsDigit(String text) {
        for (int i = 0; i < text.length(); ++i) {
            if (Character.isDigit(text.charAt(i))) return true;
        }
        return false;
    }

    private String extractText(String text, Tuple<Integer, Integer> range) {
        return text.substring(range.getFirst(), range.getFirst() + range.getSecond());
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
        return Integer.MAX_VALUE;
    }
}
