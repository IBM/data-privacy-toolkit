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

import java.util.List;
import java.util.regex.Pattern;

public class SingaporeNRIC extends AbstractIdentifier {
    private static final Pattern pattern = Pattern.compile("^[SMTFG]\\d\\d\\d\\d\\d\\d\\d\\p{Alnum}$");

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("SG_NRIC");
    }

    @Override
    public boolean isOfThisType(String data) {
        if (data.length() != 9) {
            return false;
        }

        return pattern.matcher(data).matches() && isValid(data);
    }

    private boolean isValid(String data) {
        /* code from: https://gist.github.com/eddiemoore/7131781 */
        data = data.toUpperCase();

        List<Integer> icArray = List.of(
                0,
                (data.charAt(1) - '0') * 2,
                (data.charAt(2) - '0') * 7,
                (data.charAt(3) - '0') * 6,
                (data.charAt(4) - '0') * 5,
                (data.charAt(5) - '0') * 4,
                (data.charAt(6) - '0') * 3,
                (data.charAt(7) - '0') * 2,
                0
        );

        int weight = 0;

        for (int i = 0; i < 8; ++i)
            weight += icArray.get(i);

        int offset = (data.charAt(0) == 'T' || data.charAt(0) == 'G') ? 4 : 0;
        var temp = (offset + weight) % 11;

        Character[] st = new Character[] {'J','Z','I','H','G','F','E','D','C','B','A'};
        Character[] fg = new Character[] {'X','W','U','T','R','Q','P','N','M','L','K'};

        char theAlpha;
        if (data.charAt(0) == 'S' || data.charAt(0) == 'T') {
            theAlpha = st[temp];
        } else if (data.charAt(0) == 'F' || data.charAt(0) == 'G') {
            theAlpha = fg[temp];
        } else {
            throw new IllegalArgumentException("Impossible to validate the NRIC");
        }

        return (data.charAt(8) == theAlpha);

    }

    @Override
    public String getDescription() {
        return null;
    }



    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA & CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 9;
    }

    @Override
    public int getMaximumLength() {
        return 9;
    }
}

