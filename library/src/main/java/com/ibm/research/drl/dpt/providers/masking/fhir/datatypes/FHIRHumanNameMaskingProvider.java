/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRHumanName;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class FHIRHumanNameMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> implements Serializable {
    private final MaskingProvider nameMaskingProvider;
    private final boolean removeExtensions;
    private final boolean removePrefix;
    private final boolean removeSuffix;

    private final String FAMILY_FIELD_PATH;
    private final String GIVEN_FIELD_PATH;

    public FHIRHumanNameMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.FAMILY_FIELD_PATH = fieldPath + "/family";
        this.GIVEN_FIELD_PATH = fieldPath + "/given";

        this.nameMaskingProvider = this.factory.get(ProviderType.NAME, maskingConfiguration);
        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.humanName.removeExtensions");
        this.removePrefix = maskingConfiguration.getBooleanValue("fhir.humanName.removePrefix");
        this.removeSuffix = maskingConfiguration.getBooleanValue("fhir.humanName.removeSuffix");
    }

    private Collection<String> maskNames(Collection<String> originalNames) {
        if (originalNames == null) {
            return null;
        }

        Collection<String> maskedNames = new ArrayList<>();
        for (String name : originalNames) {
            maskedNames.add(nameMaskingProvider.mask(name));
        }

        return maskedNames;
    }

    private String buildName(FHIRHumanName name) {
        StringBuilder builder = new StringBuilder();

        Collection<String> givenNames = name.getGiven();
        if (givenNames != null) {
            for (String givenName : givenNames) {
                builder.append(givenName);
                builder.append(" ");
            }
        }

        Collection<String> familyNames = name.getFamily();
        if (familyNames != null) {
            for (String familyName : familyNames) {
                builder.append(familyName);
                builder.append(" ");
            }
        }

        if (builder.length() == 0) {
            return "";
        }

        return builder.toString().trim();
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRHumanName obj = JsonUtils.MAPPER.treeToValue(node, FHIRHumanName.class);
            FHIRHumanName maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRHumanName mask(FHIRHumanName name) {
        if (name == null) {
            return null;
        }

        if (this.removeSuffix) {
            name.setSuffix(null);
        }

        if (this.removePrefix) {
            name.setPrefix(null);
        }

        if (!isAlreadyMasked(FAMILY_FIELD_PATH)) {
            name.setFamily(maskNames(name.getFamily()));
        }

        if (!isAlreadyMasked(GIVEN_FIELD_PATH)) {
            name.setGiven(maskNames(name.getGiven()));
        }

        name.setText(buildName(name));

        if (this.removeExtensions) {
            name.setExtension(null);
        }

        return name;
    }


}


