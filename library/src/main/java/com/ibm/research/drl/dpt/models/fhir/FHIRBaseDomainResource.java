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
package com.ibm.research.drl.dpt.models.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRNarrative;

import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FHIRBaseDomainResource extends FHIRResource {

    public FHIRNarrative getText() {
        return text;
    }

    public void setText(FHIRNarrative text) {
        this.text = text;
    }

    public Collection<FHIRResource> getContained() {
        return contained;
    }

    public void setContained(Collection<FHIRResource> contained) {
        this.contained = contained;
    }

    public Collection<FHIRExtension> getExtension() {
        return extension;
    }

    public void setExtension(Collection<FHIRExtension> extension) {
        this.extension = extension;
    }

    public Collection<FHIRExtension> getModifierExtension() {
        return modifierExtension;
    }

    public void setModifierExtension(Collection<FHIRExtension> modifierExtension) {
        this.modifierExtension = modifierExtension;
    }

    private FHIRNarrative text;
    private Collection<FHIRResource> contained;
    private Collection<FHIRExtension> extension;
    private Collection<FHIRExtension> modifierExtension;
}


