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
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.GenderManager;
import com.ibm.research.drl.dpt.models.Sex;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;

public class GenderMaskingProvider implements MaskingProvider {
    private static final Logger log = LogManager.getLogger(GenderMaskingProvider.class);

    private static final GenderManager genderManager = GenderManager.getInstance();
    private final int failMode;

    public GenderMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public GenderMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.failMode = maskingConfiguration.getIntValue("fail.mode");
    }

    @Override
    public String mask(String identifier) {
        Sex sex = genderManager.getKey(identifier);
        if (sex == null) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    log.error("invalid gender identifier");
                    throw new RuntimeException("invalid gender identifier");
                case FailMode.GENERATE_RANDOM:
                    return genderManager.getRandomKey();
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        return genderManager.getRandomKey(sex.getNameCountryCode());
    }
}


