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

import com.ibm.research.drl.dpt.managers.ContinentManager;
import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

/**
 * The type Continent identifier.
 */
public class ContinentIdentifier extends AbstractManagerBasedIdentifier {
    private static final String[] appropriateNames = {"Continent"};
    private static final ContinentManager continentManager = ContinentManager.getInstance();

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.CONTINENT;
    }

    @Override
    public String getDescription() {
        return "Continent identification";
    }

    @Override
    protected Manager getManager() {
        return continentManager;
    }

    @Override
    public Collection<ProviderType> getLinkedTypes() {
        return Arrays.asList(ProviderType.COUNTRY, ProviderType.CITY);
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }
}
