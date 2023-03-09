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
import com.ibm.research.drl.dpt.managers.MaritalStatusManager;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MaritalStatusIdentifier extends AbstractManagerBasedIdentifier {
    private final Collection<String> appropriateNames = List.of("Marital Status");
    private final MaritalStatusManager maritalStatusManager = MaritalStatusManager.getInstance();

    @Override
    protected Manager getManager() {
        return maritalStatusManager;
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return this.appropriateNames;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.MARITAL_STATUS;
    }

    @Override
    public String getDescription() {
        return "Marital status identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.ALPHA;
    }
}
