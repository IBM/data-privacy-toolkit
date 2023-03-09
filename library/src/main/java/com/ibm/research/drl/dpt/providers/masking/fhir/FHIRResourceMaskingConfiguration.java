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
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FHIRResourceMaskingConfiguration {
    private final String basePath;
    private final List<FHIRResourceField> fields;
    private final MaskingConfiguration maskingConfiguration;

    public String getBasePath() {
        return basePath;
    }

    public List<FHIRResourceField> getFields() {
        return fields;
    }

    private FHIRResourceField buildResourceField(String conf) {
        String[] tokens = conf.split(":");
        String path = tokens[0];
        String fhirType = tokens[1];

        if (fhirType.equals("null")) {
            fhirType = maskingConfiguration.getStringValue("default.masking.provider");
        }

        return new FHIRResourceField(path, fhirType);
    }

    private List<FHIRResourceField> buildFieldList(Collection<String> configurations) {
        List<FHIRResourceField> resourceFields = new ArrayList<>();

        for (String conf : configurations) {
            FHIRResourceField resourceField = buildResourceField(conf);
            resourceFields.add(resourceField);
        }

        return resourceFields;
    }

    public FHIRResourceMaskingConfiguration(String basePath, Collection<String> configuration) {
        this(basePath, configuration, new DefaultMaskingConfiguration());
    }

    public FHIRResourceMaskingConfiguration(String basePath, Collection<String> configurations, MaskingConfiguration maskingConfiguration) {
        this.basePath = basePath;
        this.maskingConfiguration = maskingConfiguration;
        this.fields = buildFieldList(configurations);
    }

}


