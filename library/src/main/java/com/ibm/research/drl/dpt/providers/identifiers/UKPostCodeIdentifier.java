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

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class UKPostCodeIdentifier extends AbstractRegexBasedIdentifier {
    private static final String AREA = "(?:" +
            "(?:A[BL])|" +
            "(?:B[ABDHLNRST]?)|" +
            "(?:C[ABFHMORTVW])|" +
            "(?:D[ADEGHLNTY])|" +
            "(?:E[CHNX]?)|" +
            "(?:F[KY])|" +
            "(?:G[LUY]?)|" +
            "(?:H[ADGPRSUX])|" +
            "(?:I[GPVM])|" +
            "(?:JE)|" +
            "(?:K[ATWY])|" +
            "(?:L[ADELNSU]?)|" +
            "(?:M[EKL]?)|" +
            "(?:N[EGNPRW]?)|" +
            "(?:O[LX])|" +
            "(?:P[AEHLOR])|" +
            "(?:R[GHM])|" +
            "(?:S[AEGKLMNOPRSTWY]?)|" +
            "(?:T[ADFNQRSW])|" +
            "(?:UB)|" +
            "(?:W[ACDFNRSV]?)|" +
            "(?:YO)|" +
            "(?:ZE)" +
            ")";
    private static final String DISTRICT = "(?:" +
            "(?:\\d{1,2})|" +
            "(?:\\d\\w)" +
            ")";
    private static final String SECTOR = "(?:\\d)";
    private static final String UNIT = "(?:\\w{2})";

    private static final String OUTWARD = "(?:" +
            AREA + "\\s?" + DISTRICT +
            ")";

    private static final String INWARD = "(?:" +
            SECTOR + "\\s?" + UNIT +
            ")";

    private final Collection<Pattern> patterns = Collections.singletonList(
            Pattern.compile(OUTWARD + "\\s?" + INWARD, Pattern.CASE_INSENSITIVE)
    );

    @Override
    protected Collection<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.POSTCODE;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT + CharacterRequirements.ALPHA;
    }

    @Override
    public int getMinimumLength() {
        return 7;
    }

    @Override
    public int getMaximumLength() {
        return 8;
    }
}
