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
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.HospitalMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.NameMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.RandomMaskingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;

public class LOMaskingProvider implements MaskingProvider {
    private static final Logger logger = LogManager.getLogger(LOMaskingProvider.class);

    private final RandomMaskingProvider randomMaskingProvider;
    private final MaskingProvider nameMaskingProvider;
    private final MaskingProvider hospitalMaskingProvider;

    private final DicomEntityType entityType;

    /**
     * Instantiates a new Lo masking provider.
     */
    public LOMaskingProvider(MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this.randomMaskingProvider = new RandomMaskingProvider(maskingConfiguration);
        this.nameMaskingProvider = new NameMaskingProvider(maskingConfiguration, factory);
        this.hospitalMaskingProvider = new HospitalMaskingProvider(maskingConfiguration);
        this.entityType = DicomEntityType.valueOf(maskingConfiguration.getStringValue("dicom.lo.entityType"));
    }

    public LOMaskingProvider(SecureRandom ignored, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this(maskingConfiguration, factory);
    }

    @Override
    public String mask(String identifier) {
        switch (entityType) {
            case HOSPITAL:
                return hospitalMaskingProvider.mask(identifier);
            case NAME:
                return nameMaskingProvider.mask(identifier);
            case GENERIC:
                return randomMaskingProvider.mask(identifier);
            default:
                logger.warn("Unexpected value: {}", entityType);
        }

        return randomMaskingProvider.mask(identifier);
    }

    public String toString() {
        return "LO," + this.entityType.toString();
    }
}

