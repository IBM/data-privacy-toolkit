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
import java.util.List;
import java.util.regex.Pattern;

public class POBOXIdentifier extends AbstractRegexBasedIdentifier {

    private final static Pattern POBOX = Pattern.compile("\\bP\\.?O\\.?[\\s-]?BOX[\\s-]*(?:\\d+|\\p{Alpha})\\b", Pattern.CASE_INSENSITIVE);

    @Override
    protected Collection<Pattern> getPatterns() {
        return List.of(POBOX);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.ADDRESS;
    }

    @Override
    public String getDescription() {
        return "POBOX identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }

    @Override
    public int getMinimumLength() {
        return 7;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}
