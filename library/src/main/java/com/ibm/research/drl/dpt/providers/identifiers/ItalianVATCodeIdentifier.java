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

import java.util.regex.Pattern;

public class ItalianVATCodeIdentifier extends AbstractIdentifier {
    private final static Pattern identifier = Pattern.compile("\\d{11}");

    @Override
    public ProviderType getType() {
        return ProviderType.valueOf("ITALIAN_VAT");
    }

    @Override
    public boolean isOfThisType(String data) {
        return identifier.matcher(data).matches();
    }

    @Override
    public String getDescription() {
        return "Identifier for Italian VAT (Partita IVA)";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.DIGIT;
    }

    @Override
    public int getMinimumLength() {
        return 11;
    }

    @Override
    public int getMaximumLength() {
        return 11;
    }
}
