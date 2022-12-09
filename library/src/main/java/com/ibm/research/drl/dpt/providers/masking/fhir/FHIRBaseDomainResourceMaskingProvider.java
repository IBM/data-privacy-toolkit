/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;

import java.util.Set;

public class FHIRBaseDomainResourceMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final boolean maskId;
    private final boolean preserveIdPrefix;
    private final boolean removeMeta;
    private final boolean removeText;
    private final boolean removeContained;
    private final boolean removeExtension;

    private final MaskingProvider idMaskingProvider;

    private final String idPath;

    public FHIRBaseDomainResourceMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.idPath = fieldPath + "/id";

        this.idMaskingProvider = getMaskingProvider(idPath, maskingConfiguration, this.factory);

        this.maskId = maskingConfiguration.getBooleanValue("fhir.resource.maskId");
        this.preserveIdPrefix = maskingConfiguration.getBooleanValue("fhir.resource.preserveIdPrefix");
        this.removeMeta = maskingConfiguration.getBooleanValue("fhir.resource.removeMeta");
        this.removeText = maskingConfiguration.getBooleanValue("fhir.domainresource.removeText");
        this.removeContained = maskingConfiguration.getBooleanValue("fhir.domainresource.removeContained");
        this.removeExtension = maskingConfiguration.getBooleanValue("fhir.domainresource.removeExtension");
    }



    public JsonNode mask(JsonNode obj) {
        if (obj == null || obj.isNull()) {
            return NullNode.getInstance();
        }

        ObjectNode object = (ObjectNode)obj;

        if (this.removeExtension) {
            object.set("extension", null);
        }

        if (this.removeContained) {
            object.set("contained", null);
        }

        if (this.removeText) {
            object.set("text", null);
        }

        if (this.removeMeta) {
            object.set("meta", null);
        }

        if (this.maskId && !isAlreadyMasked(idPath)) {
            JsonNode idNode = object.get("id");
            if (idNode != null) {
                String id = idNode.asText();
                if (id != null) {
                    String maskedID = FHIRMaskingUtils.maskResourceId(id, this.preserveIdPrefix, idMaskingProvider);
                    object.set("id", new TextNode(maskedID));
                }
            }
        }

        return object;
    }
}


