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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class HashIntMaskingProvider implements MaskingProvider {
    private final static Logger log = LogManager.getLogger(HashIntMaskingProvider.class);
    private final String algorithm;
    private final boolean useBudget;
    private final int budgetAmount;
    private final boolean signCoherent;

    public HashIntMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.algorithm = configuration.getStringValue("hashint.algorithm.default");
        this.useBudget = configuration.getBooleanValue("hashint.budget.use");
        this.budgetAmount = configuration.getIntValue("hashint.budget.amount");
        this.signCoherent = configuration.getBooleanValue("hashint.sign.coherent");
    }

    public HashIntMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    @Override
    public String mask(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            md.update(value.getBytes());

            return Long.toString(checkBoundries(new BigInteger(md.digest()).longValue(), Long.parseLong(value)));
        } catch (NoSuchAlgorithmException e) {
            log.error("Impossible to retrieve an instance of " + algorithm, e);
            throw new Error("Impossible to retrieve an instance of " + algorithm);
        }
    }

    private Long checkBoundries(Long masked, Long original) {
        if (useBudget) {
            masked = Math.min(masked, original + budgetAmount);
            masked = Math.max(masked, original - budgetAmount);
        }

        if (signCoherent && (Long.signum(masked) != Long.signum(original))) {
            masked *= -1L;
        }

        return masked;
    }
}
