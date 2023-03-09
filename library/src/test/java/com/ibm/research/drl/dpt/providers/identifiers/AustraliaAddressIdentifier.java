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

public class AustraliaAddressIdentifier extends AbstractIdentifier {
    private static final String STATE = "(?:" +
            "(?:NSW|Nsv|New South Wales)" +
            "|(?:ACT|Act|Australian Capital Territory)" +
            "|(?:VIC|Vic|Victoria)" +
            "|(?:QLD|Qld|Queensland)" +
            "|(?:SA|South Australia)" +
            "|(?:WA|Western Australia)" +
            "|(?:TAS|Tas|Tasmania)" +
            "|(?:NT|Northern Territory)" +
            ")";
    private static final String POSTCODE = "(?:[0-8]\\d{3})";

    private final Collection<Pattern> patterns = Arrays.asList(
            Pattern.compile("\\d+(?:\\s+\\p{Alpha}{2,})+(?:,?(?:\\s+[A-Za-z]{2,})+(?:,?\\s+" + STATE + "(?:\\s+" + POSTCODE + ")?)?)?"),
            Pattern.compile("PO\\s+Box\\s+\\d+(:?\\s+\\p{Alpha}{3,})+\\s+" + STATE + "\\s+" + POSTCODE)
    );

    @Override
    public ProviderType getType() {
        return ProviderType.LOCATION;
    }

    @Override
    public boolean isOfThisType(String data) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(data).matches()) return true;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "Australian approved format for mail post";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA & CharacterRequirements.DIGIT & CharacterRequirements.SPACE;
    }

    @Override
    public int getMinimumLength() {
        return 0;
    }

    @Override
    public int getMaximumLength() {
        return 0;
    }

    @Override
    public boolean isPOSIndependent() {
        return false;
    }
}
