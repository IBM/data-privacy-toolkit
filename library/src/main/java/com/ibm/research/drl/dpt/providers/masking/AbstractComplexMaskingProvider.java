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
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.ProviderType;

import java.util.Set;

public abstract class AbstractComplexMaskingProvider<K> implements MaskingProvider {
    private final String prefixGUID;
    private final Set<String> maskedFields;
    protected final MaskingProviderFactory factory;

    public K mask(K obj) {
        return obj;
    }

    private String getSubfieldName(String declaredName) {
        return prefixGUID + declaredName;
    }

    protected MaskingConfiguration getConfigurationForSubfield(String declaredName, MaskingConfiguration maskingConfiguration) {
        final String subfieldName = getSubfieldName(declaredName);

        final ConfigurationManager manager = maskingConfiguration.getConfigurationManager();

        if (null == manager) {
            return maskingConfiguration;
        } else {
            return manager.getFieldConfiguration(subfieldName);
        }
    }

    protected MaskingProvider getMaskingProvider(String path, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        MaskingConfiguration valueMaskingConfiguration = getConfigurationForSubfield(path, maskingConfiguration);
        String defaultMaskingProvider = valueMaskingConfiguration.getStringValue("default.masking.provider");
        return factory.get(ProviderType.valueOf(defaultMaskingProvider), valueMaskingConfiguration);
    }

    public AbstractComplexMaskingProvider(String complexType, MaskingConfiguration maskingConfiguration, Set<String> maskedFields, MaskingProviderFactory factory) {
        this.prefixGUID = maskingConfiguration.getStringValue(complexType + ".prefixGUID");
        this.maskedFields = maskedFields;
        this.factory = factory;
    }

    public boolean isAlreadyMasked(String fieldPath) {
        return maskedFields.contains(fieldPath);
    }

    @Override
    public String mask(String identifier) {
        return null;
    }
}


