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
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The type Patient id identifier.
 */
public class PatientIDIdentifier extends AbstractRegexBasedIdentifier {
    private static final String[] appropriateNames = {"Patient ID", "PatientID"};
    private final Collection<Pattern> patientIDPatterns = new ArrayList<>(List.of(
            Pattern.compile("^\\d{3}-\\d{3}-\\d{3}-\\d{3}$")
    ));

    @Override
    public ProviderType getType() {
        return ProviderType.EMAIL;
    }

    @Override
    public String getDescription() {
        return "Patient ID identification";
    }

    @Override
    protected Collection<Pattern> getPatterns() {
        return patientIDPatterns;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 15;
    }

    @Override
    public int getMaximumLength() {
        return getMinimumLength();
    }
}
