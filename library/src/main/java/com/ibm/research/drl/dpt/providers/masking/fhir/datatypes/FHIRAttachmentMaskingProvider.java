/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRAttachment;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

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
            FHIRAttachment cc = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRAttachment.class);
            FHIRAttachment maskedCc = mask(cc);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedCc);
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

