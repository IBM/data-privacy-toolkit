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
import org.apache.commons.validator.routines.IBANValidator;

import java.util.Arrays;
import java.util.Collection;

public class IBANIdentifier extends AbstractIdentifier {
    private final static IBANValidator ibanValidator = IBANValidator.getInstance();
    private final static String[] appropriateNames = new String[]{"IBAN"};

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public ProviderType getType() {
        return ProviderType.IBAN;
    }

    @Override
    public boolean isOfThisType(String data) {
        try {
            return ibanValidator.isValid(data);
        } catch (Exception ignored) {
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "IBAN identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 8;
    }

    @Override
    public int getMaximumLength() {
        return 34;
    }
}
