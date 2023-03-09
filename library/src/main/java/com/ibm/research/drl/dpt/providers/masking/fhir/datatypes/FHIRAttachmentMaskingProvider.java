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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.Serializable;
import java.util.Set;

public class FHIRAttachmentMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {

    private final boolean removeExtensions;
    private final boolean removeData;
    private final boolean removeURI;
    private final boolean removeTitle;

    public FHIRAttachmentMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.attachment.removeExtensions");
        this.removeData = maskingConfiguration.getBooleanValue("fhir.attachment.removeData");
        this.removeURI = maskingConfiguration.getBooleanValue("fhir.attachment.removeURI");
        this.removeTitle = maskingConfiguration.getBooleanValue("fhir.attachment.removeTitle");
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRAttachment cc = JsonUtils.MAPPER.treeToValue(node, FHIRAttachment.class);
            FHIRAttachment maskedCc = mask(cc);
            return JsonUtils.MAPPER.valueToTree(maskedCc);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRAttachment mask(FHIRAttachment attachment) {
        if (attachment == null) {
            return null;
        }

        if (this.removeExtensions) {
            attachment.setExtension(null);
        }

        if (this.removeTitle) {
            attachment.setTitle("");
        }

        if (this.removeURI) {
            attachment.setUrl("");
        }

        if (this.removeData) {
            attachment.setData(null);
            attachment.setSize("0");
            attachment.setHash(null);
        }

        return attachment;
    }
}

