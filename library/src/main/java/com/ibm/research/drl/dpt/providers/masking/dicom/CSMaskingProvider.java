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
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.RandomMaskingProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;

/**
 * DICOM CS (Code String) masking provider, supporting GENDER and SEX_NEUTERED entity types.
 */
public class CSMaskingProvider implements MaskingProvider {
    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(CSMaskingProvider.class);
    /** Fallback random masking provider. */
    private final RandomMaskingProvider randomMaskingProvider;
    /** Valid gender code characters. */
    private final char[] genders = "FMO".toCharArray();
    /** Valid sex-neutered code values. */
    private final String[] sexNeutered = {"ALTERED", "UNALTERED"};

    /** The DICOM entity type driving the masking logic. */
    private final DicomEntityType entityType;
    /** Secure random source. */
    private final SecureRandom random;

    /**
     * Constructs a CSMaskingProvider with the given configuration.
     *
     * @param maskingConfiguration the masking configuration
     */
    public CSMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    /**
     * Constructs a CSMaskingProvider with the given random source and configuration.
     *
     * @param random               the secure random source
     * @param maskingConfiguration the masking configuration
     */
    public CSMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.randomMaskingProvider = new RandomMaskingProvider(maskingConfiguration);
        this.entityType = DicomEntityType.valueOf(maskingConfiguration.getStringValue("dicom.cs.entityType"));
        this.random = random;
    }

    @Override
    public String mask(String identifier) {
        switch (entityType) {
            case GENDER:
                return "" + genders[random.nextInt(genders.length)];
            case SEX_NEUTERED:
                return sexNeutered[random.nextInt(sexNeutered.length)];
            default:
                logger.warn("Unexpected value: {}", entityType);
        }

        return randomMaskingProvider.mask(identifier);
    }

    @Override
    public String toString() {
        return "CS," + this.entityType.toString();
    }
}

