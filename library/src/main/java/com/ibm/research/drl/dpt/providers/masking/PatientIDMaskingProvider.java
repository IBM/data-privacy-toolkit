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
import com.ibm.research.drl.dpt.providers.identifiers.PatientIDIdentifier;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;

/**
 * The type Patient id masking provider.
 *
 */
public class PatientIDMaskingProvider implements MaskingProvider {

    private static final PatientIDIdentifier patientIDIdentifier = new PatientIDIdentifier();
    private final int preservedGroups;
    private final SecureRandom random;

    /**
     * Instantiates a new Patient id masking provider.
     */
    public PatientIDMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Patient id masking provider.
     *
     * @param random the random
     */
    public PatientIDMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Patient id masking provider.
     *
     * @param configuration the configuration
     */
    public PatientIDMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Patient id masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public PatientIDMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preservedGroups = configuration.getIntValue("patientID.groups.preserve");
    }

    private String randomPatientID() {
        String[] groups = new String[4];

        for (int i = 0; i < groups.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int k = 0; k < 3; k++) {
                builder.append((char) ('0' + random.nextInt(10)));
            }
            groups[i] = builder.toString();
        }

        return StringUtils.join(groups, '-');
    }

    @Override
    public String mask(String identifier) {
        if (!patientIDIdentifier.isOfThisType(identifier)) {
            return randomPatientID();
        }

        String[] groups = identifier.split("-");

        for (int i = preservedGroups; i < groups.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int k = 0; k < 3; k++) {
                builder.append((char) ('0' + random.nextInt(10)));
            }
            groups[i] = builder.toString();
        }

        return StringUtils.join(groups, '-');
    }
}
