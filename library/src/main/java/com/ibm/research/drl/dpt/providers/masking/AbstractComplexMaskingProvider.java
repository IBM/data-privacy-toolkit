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

/**
 * Abstract base class for complex masking providers that handle structured types.
 *
 * @param <K> the type of object to be masked
 */
public abstract class AbstractComplexMaskingProvider<K> implements MaskingProvider {
    /** The prefix GUID used to construct subfield names. */
    private final String prefixGUID;
    /** The set of field paths that have already been masked. */
    private final Set<String> maskedFields;
    /** The masking provider factory used to create sub-providers. */
    protected final MaskingProviderFactory factory;

    /**
     * Masks the given object. By default, returns the object unchanged.
     *
     * @param obj the object to mask
     * @return the masked object
     */
    public K mask(K obj) {
        return obj;
    }

    private String getSubfieldName(String declaredName) {
        return prefixGUID + declaredName;
    }

    /**
     * Returns the masking configuration for a given subfield.
     *
     * @param declaredName         the declared subfield name
     * @param maskingConfiguration the parent masking configuration
     * @return the masking configuration for the subfield
     */
    protected MaskingConfiguration getConfigurationForSubfield(String declaredName, MaskingConfiguration maskingConfiguration) {
        final String subfieldName = getSubfieldName(declaredName);

        final ConfigurationManager manager = maskingConfiguration.getConfigurationManager();

        if (null == manager) {
            return maskingConfiguration;
        } else {
            return manager.getFieldConfiguration(subfieldName);
        }
    }

    /**
     * Returns the masking provider for the given path.
     *
     * @param path                 the field path
     * @param maskingConfiguration the masking configuration
     * @param factory              the masking provider factory
     * @return the masking provider
     */
    protected MaskingProvider getMaskingProvider(String path, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        MaskingConfiguration valueMaskingConfiguration = getConfigurationForSubfield(path, maskingConfiguration);
        String defaultMaskingProvider = valueMaskingConfiguration.getStringValue("default.masking.provider");
        return factory.get(ProviderType.valueOf(defaultMaskingProvider), valueMaskingConfiguration);
    }

    /**
     * Constructs an AbstractComplexMaskingProvider.
     *
     * @param complexType          the complex type identifier used to look up the prefix GUID
     * @param maskingConfiguration the masking configuration
     * @param maskedFields         the set of already-masked field paths
     * @param factory              the masking provider factory
     */
    public AbstractComplexMaskingProvider(String complexType, MaskingConfiguration maskingConfiguration, Set<String> maskedFields, MaskingProviderFactory factory) {
        this.prefixGUID = maskingConfiguration.getStringValue(complexType + ".prefixGUID");
        this.maskedFields = maskedFields;
        this.factory = factory;
    }

    /**
     * Returns whether the given field path has already been masked.
     *
     * @param fieldPath the field path to check
     * @return true if already masked, false otherwise
     */
    public boolean isAlreadyMasked(String fieldPath) {
        return maskedFields.contains(fieldPath);
    }

    @Override
    public String mask(String identifier) {
        return null;
    }
}


