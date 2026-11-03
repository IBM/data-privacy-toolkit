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


/**
 * Utility that applies masking providers to a list of NLP-identified entities.
 */
public class MaskIdentifiedEntities {
    private final MaskingProviderFactory factory;
    private final ConfigurationManager configurationManager;
    private final DataMaskingOptions dataMaskingOptions;
    private final Map<String, MaskingProvider> cachedProviders;

    /**
     * Constructs a MaskIdentifiedEntities instance.
     *
     * @param configurationManager the configuration manager providing masking configurations
     * @param dataMaskingOptions   the options describing which entity types to mask
     * @param factory              the masking provider factory
     */
    public MaskIdentifiedEntities(final ConfigurationManager configurationManager, final DataMaskingOptions dataMaskingOptions,
                                  final MaskingProviderFactory factory) {
        this.factory = factory;
        this.configurationManager = configurationManager;
        this.dataMaskingOptions = dataMaskingOptions;
        this.cachedProviders = new HashMap<>();
    }

    /**
     * Applies masking to all identified entities that match a masking target.
     *
     * @param entities the list of identified entities to mask
     * @return the list of entities with sensitive values replaced
     */
    public List<IdentifiedEntity> maskEntities(final List<IdentifiedEntity> entities) {
        Map<String, DataMaskingTarget> toBeMasked = dataMaskingOptions.getToBeMasked();

        return entities.parallelStream().map( entity -> {
            final String type = entity.getType().iterator().next().getSubtype();

            String maskedValue = (toBeMasked.containsKey(type)) ?
                    mask(entity.getText(), configurationManager.getFieldConfiguration(type),
                            toBeMasked.get(type).getProviderType(), type) : entity.getText();

            return new IdentifiedEntity(maskedValue, entity.getStart(), entity.getEnd(), entity.getType(), entity.getPos());
        }).toList();
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
