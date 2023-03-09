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

import java.util.Calendar;

public class YOBIdentifier extends AbstractIdentifier {
    private final int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.YOB;
    }

    @Override
    public boolean isOfThisType(String data) {
        int digits = 0;

        for (int i = 0; i < data.length(); i++) {
            if (Character.isDigit(data.charAt(i))) {
                digits++;
            }
        }

        if (digits == 0) {
            return false;
        }

        try {
            int yob = Integer.parseInt(data);

            return yob >= (currentYear - 100) && yob <= currentYear;
        } catch (Exception ignored) {
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "Year of birth";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 4;
    }

    @Override
    public int getMaximumLength() {
        return getMinimumLength();
    }
}
