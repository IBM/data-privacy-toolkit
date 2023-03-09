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

import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PluggableLookupIdentifier extends AbstractIdentifier {
    private final Collection<String> appropriateNames;
    private final ProviderType providerType;
    private final ValueClass valueClass;
    private final Set<String> valueSet;
    private final boolean ignoreCase;
    private final boolean isPOSIndependent;

    /**
     * Instantiates a new Pluggable lookup identifier.
     *
     * @param providerTypeName the provider type name
     * @param appropriateNames the appropriate names
     * @param values           the values
     * @param ignoreCase       the ignore case
     * @param valueClass       the value class
     */

    public PluggableLookupIdentifier(String providerTypeName,
                                     Collection<String> appropriateNames,
                                     Collection<String> values,
                                     boolean ignoreCase,
                                     ValueClass valueClass) {
        this(providerTypeName, appropriateNames, values, ignoreCase, valueClass, true);
    }

    public PluggableLookupIdentifier(String providerTypeName,
                                     Collection<String> appropriateNames,
                                     Collection<String> values,
                                     boolean ignoreCase,
                                     ValueClass valueClass,
                                     boolean isPOSIndependent) {

        this.appropriateNames = appropriateNames;
        this.providerType = ProviderType.valueOf(providerTypeName);
        this.valueClass = valueClass;
        this.ignoreCase = ignoreCase;
        this.isPOSIndependent = isPOSIndependent;

        this.valueSet = new HashSet<>();
        for (String p : values) {
            if (ignoreCase) {
                this.valueSet.add(p.toUpperCase());
            } else {
                this.valueSet.add(p);
            }
        }
    }

    @Override
    protected Collection<String> getAppropriateNames() {
        return appropriateNames;
    }

    @Override
    public ProviderType getType() {
        return this.providerType;
    }

    @Override
    public boolean isOfThisType(String data) {
        if (ignoreCase) {
            return this.valueSet.contains(data.toUpperCase());
        } else {
            return this.valueSet.contains(data);
        }
    }

    @Override
    public String getDescription() {
        return "Pluggable lookup-based identifier";
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return CharacterRequirements.NONE;
    }

    @Override
    public int getMinimumLength() {
        return 1;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }

    public boolean isPOSIndependent() {
        return this.isPOSIndependent;
    }
}
