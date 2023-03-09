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

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class SortCodeIdentifier extends AbstractRegexBasedIdentifier {
    private final String BANK_ID = "(?:0[15789]" +
            "|1[0-8]" +
            "|2[0357]" +
            "|30" +
            "|4[02349]" +
            "|5\\d" +
            "|6[024]" +
            "|7[012347]" +
            "|8[023456789]" +
            "|9[0123589])";

    private final Collection<Pattern> patterns = Arrays.asList(
            Pattern.compile(BANK_ID + "\\d{4}"),
            Pattern.compile(BANK_ID + "\\s\\d{2}\\s\\d{2}"),
            Pattern.compile(BANK_ID + "-\\d{2}-\\d{2}")
    );

    @Override
    protected Collection<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SORT_CODE;
    }

    @Override
    public String getDescription() {
        return "Identify sort codes";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 6;
    }

    @Override
    public int getMaximumLength() {
        return 8;
    }
}
