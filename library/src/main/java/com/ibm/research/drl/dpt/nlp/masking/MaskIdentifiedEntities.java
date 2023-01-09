/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp.masking;

import com.ibm.research.drl.dpt.configuration.ConfigurationManager;
import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.nlp.IdentifiedEntity;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MaskIdentifiedEntities {
    private final MaskingProviderFactory factory;
    private final ConfigurationManager configurationManager;
    private final DataMaskingOptions dataMaskingOptions;
    private final Map<String, MaskingProvider> cachedProviders;

    public MaskIdentifiedEntities(final ConfigurationManager configurationManager, final DataMaskingOptions dataMaskingOptions,
                                  final MaskingProviderFactory factory) {
        this.factory = factory;
        this.configurationManager = configurationManager;
        this.dataMaskingOptions = dataMaskingOptions;
        this.cachedProviders = new HashMap<>();
    }

    public List<IdentifiedEntity> maskEntities(final List<IdentifiedEntity> entities) {
        Map<String, DataMaskingTarget> toBeMasked = dataMaskingOptions.getToBeMasked();

        return entities.parallelStream().map( entity -> {
            final String type = entity.getType().iterator().next().getSubtype();

            String maskedValue = (toBeMasked.containsKey(type)) ?
                    mask(entity.getText(), configurationManager.getFieldConfiguration(type),
                            toBeMasked.get(type).getProviderType(), type) : entity.getText();

            return new IdentifiedEntity(maskedValue, entity.getStart(), entity.getEnd(), entity.getType(), entity.getPos());
        }).collect(Collectors.toList());
    }

    private String mask(final String text, final MaskingConfiguration configuration, ProviderType providerType, String fieldName) {
        return getMaskingProvider(providerType, configuration).mask(text, fieldName);
    }

    private MaskingProvider getMaskingProvider(ProviderType type, MaskingConfiguration configuration) {
        String typeName = type.getName();

        if (!cachedProviders.containsKey(typeName)) {
            MaskingProvider maskingProvider = factory.get(type, configuration);
            cachedProviders.put(typeName, maskingProvider);
        }

        return cachedProviders.get(typeName);
    }
}
