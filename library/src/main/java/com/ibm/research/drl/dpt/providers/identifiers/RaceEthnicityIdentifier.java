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

import com.ibm.research.drl.dpt.managers.Manager;
import com.ibm.research.drl.dpt.managers.RaceManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

/**
 * The type Race ethnicity identifier.
 *
 */
public class RaceEthnicityIdentifier extends AbstractManagerBasedIdentifier {
    private static final RaceManager raceManager = RaceManager.getInstance();
    private static final String[] appropriateNames = {"Race", "Ethnicity"};

    @Override
    public ProviderType getType() {
        return ProviderType.RACE;
    }

    @Override
    protected Manager getManager() {
        return raceManager;
    }

    @Override
    public String getDescription() {
        return "Race/Ethnicity identification of most popular ethnic groups";
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
