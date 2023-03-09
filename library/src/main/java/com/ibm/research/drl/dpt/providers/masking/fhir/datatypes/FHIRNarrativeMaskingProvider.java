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
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRNarrative;

import java.io.Serializable;

public class FHIRNarrativeMaskingProvider implements Serializable {

    private final boolean removeDiv;
    private final boolean removeExtensions;

    public FHIRNarrativeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this.removeDiv = maskingConfiguration.getBooleanValue("fhir.narrative.removeDiv");
        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.narrative.removeExtensions");
    }

    public FHIRNarrative mask(FHIRNarrative narrative) {
        if (narrative == null) {
            return null;
        }

        if (this.removeDiv) {
            narrative.setDiv(null);
        }

        if (this.removeExtensions) {
            narrative.setExtension(null);
        }

        return narrative;
    }
}


