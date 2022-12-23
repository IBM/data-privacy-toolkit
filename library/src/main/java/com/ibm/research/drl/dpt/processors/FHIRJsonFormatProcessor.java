/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
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


