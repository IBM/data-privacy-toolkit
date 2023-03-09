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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LotusNotesIdentifier extends AbstractRegexBasedIdentifier implements IdentifierWithOffset {
    private static final String validIdentifier = "(?:\\w(?:[\\w\\s-_])*)";
    private static final List<Pattern> patterns = List.of(
            Pattern.compile(
                    "("
                            + validIdentifier + ")"
                            + "/"
                            + validIdentifier
                            + "/"
                            + validIdentifier
                            + "(?:@" + validIdentifier + ")*"
                    , Pattern.CASE_INSENSITIVE
            )
    );

    @Override
    protected Collection<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.PERSON;
    }

    @Override
    public String getDescription() {
        return "Identifier for Lotus Notes addresses";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.SLASH;
    }

    @Override
    public int getMinimumLength() {
        return 5;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isPOSIndependent() {
        return true;
    }

    @Override
    public Tuple<Boolean, Tuple<Integer, Integer>> isOfThisTypeWithOffset(String identifier) {
        for (Pattern pattern : getPatterns()) {
            Matcher matcher = pattern.matcher(identifier);
            if (matcher.matches()) {
                String person = matcher.group(1);

                return new Tuple<>(true, new Tuple<>(
                        identifier.indexOf(person),
                        person.length()
                ));
            }
        }
        return new Tuple<>(false, null);
    }
}
