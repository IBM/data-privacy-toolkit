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

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.configuration.AnonymizationOptions;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;

public class GeneralizationMaskingProvider implements MaskingProvider {
    private static final Logger log = LogManager.getLogger(GeneralizationMaskingProvider.class);

    private final int hierarchyLevel;
    private final GeneralizationHierarchy hierarchy;
    private final boolean randomizeOnFail;

    public GeneralizationMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.hierarchyLevel = maskingConfiguration.getIntValue("generalization.masking.hierarchyLevel");

        if (this.hierarchyLevel < 0) {
            String msg = "generalization.masking.hierarchyLevel must be >= 0";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        this.randomizeOnFail = maskingConfiguration.getBooleanValue("generalization.masking.randomizeOnFail");
        String hierarchyName = maskingConfiguration.getStringValue("generalization.masking.hierarchyName");
        JsonNode hierarchyMapNode = Objects.requireNonNull(maskingConfiguration.getJsonNodeValue("generalization.masking.hierarchyMap"));

        Map<String, GeneralizationHierarchy> hierarchyMap = AnonymizationOptions.hierarchiesFromJSON(hierarchyMapNode);

        this.hierarchy = hierarchyMap.get(hierarchyName);

        if (this.hierarchy == null) {
            String msg = "Unable to find the hierarchy with name: " + hierarchyName;
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public String mask(String identifier) {
        return hierarchy.encode(identifier, this.hierarchyLevel, this.randomizeOnFail);
    }
}
