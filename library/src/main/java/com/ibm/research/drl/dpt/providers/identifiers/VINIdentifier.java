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
import com.ibm.research.drl.dpt.managers.VINManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;

/*
    VIN: classification
    There are at least four competing standards used to calculate VIN.

    FMVSS 115, Part 565: Used in United States and Canada[2]
    ISO Standard 3779: Used in Europe and many other parts of the world
    SAE J853: Very similar to the ISO standard
    ADR 61/2 used in Australia, referring back to ISO 3779 and 3780.[3]

    https://en.wikipedia.org/wiki/Vehicle_identification_number
 */
public class VINIdentifier extends AbstractManagerBasedIdentifier {
    private static final VINManager vinManager = new VINManager();
    private static final String[] appropriateNames = {"Vehicle Identification Number", "VIN"};

    @Override
    public ProviderType getType() {
        return ProviderType.VIN;
    }

    @Override
    public String getDescription() {
        return "Vehicle identification number identification. Supports world manufacturer identification";
    }

    @Override
    protected Manager getManager() {
        return vinManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return Arrays.asList(appropriateNames);
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }
}
