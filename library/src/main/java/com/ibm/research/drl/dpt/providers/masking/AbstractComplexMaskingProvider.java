/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
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

    public String mask(String identifier) {
        return null;
    }
}


