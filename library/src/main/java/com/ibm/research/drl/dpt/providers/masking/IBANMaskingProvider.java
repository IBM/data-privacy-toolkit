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

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.identifiers.IBANIdentifier;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.security.SecureRandom;

public class IBANMaskingProvider implements MaskingProvider {
    private static final IBANIdentifier ibanIdentifier = new IBANIdentifier();
    private final boolean preserveCountry;

    /**
     * Instantiates a new Iban masking provider.
     */
    public IBANMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Iban masking provider.
     *
     * @param random the random
     */
    public IBANMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Iban masking provider.
     *
     * @param configuration the configuration
     */
    public IBANMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Iban masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public IBANMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.preserveCountry = configuration.getBooleanValue("iban.mask.preserveCountry");
    }

    @Override
    public String mask(String identifier) {
        if (ibanIdentifier.isOfThisType(identifier) && this.preserveCountry) {
            return Iban.random(CountryCode.valueOf(identifier.substring(0, 2))).toString();
        }

        return Iban.random().toString();
    }
}
