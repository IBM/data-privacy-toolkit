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
package com.ibm.research.drl.dpt.processors;

import com.ibm.research.drl.dpt.configuration.DataMaskingOptions;
import com.ibm.research.drl.dpt.configuration.DataMaskingTarget;
import com.ibm.research.drl.dpt.processors.records.JSONRecord;
import com.ibm.research.drl.dpt.processors.records.Record;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingProvider;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;

public class FHIRJsonFormatProcessor extends JSONFormatProcessor {
    private volatile FHIRMaskingProvider maskingProvider;

    @Override
    public Record maskRecord(Record record, MaskingProviderFactory maskingProvidersFactory, Set<String> alreadyMaskedFields, DataMaskingOptions dataMaskingOptions) {
        if (!(record instanceof JSONRecord)) throw new IllegalArgumentException("Record not of the right type");

        JSONRecord jsonRecord = (JSONRecord) record;

        FHIRMaskingProvider maskingProvider = getMaskingProvider(maskingProvidersFactory, alreadyMaskedFields, dataMaskingOptions.getToBeMasked());

        try {
            return new JSONRecord(
                    JsonUtils.MAPPER.readTree(maskingProvider.mask(jsonRecord.getNode()))
            );
        } catch (IOException e) {
            throw new RuntimeException("Error processing the response from the masking provider");
        }
    }

    private FHIRMaskingProvider getMaskingProvider(MaskingProviderFactory factory,
                                                   Set<String> maskedFields, Map<String, DataMaskingTarget> toBeMasked) {
        if (null == maskingProvider) {
            synchronized (this) {
                if (null == maskingProvider) {
                    maskingProvider = new FHIRMaskingProvider(new SecureRandom(), factory.getConfigurationForField(""), maskedFields, toBeMasked, factory);
                }
            }
        }

        return maskingProvider;
    }
}


