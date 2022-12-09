/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2121                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRCoding;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.util.Set;

public class FHIRCodingMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final boolean maskVersion;
    private final boolean maskSystem;
    private final boolean maskCode;
    private final boolean maskDisplay;

    private final MaskingProvider versionMaskingProvider;
    private final MaskingProvider systemMaskingProvider;
    private final MaskingProvider displayMaskingProvider;
    private final MaskingProvider codeMaskingProvider;

    private final String DISPLAY_PATH;
    private final String VERSION_PATH;
    private final String CODE_PATH;
    private final String SYSTEM_PATH;


    public FHIRCodingMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, final String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.DISPLAY_PATH = fieldPath + "/display";
        this.VERSION_PATH = fieldPath + "/version";
        this.CODE_PATH = fieldPath + "/code";
        this.SYSTEM_PATH = fieldPath + "/system";

        this.maskVersion = maskingConfiguration.getBooleanValue("fhir.coding.maskVersion");
        this.maskDisplay = maskingConfiguration.getBooleanValue("fhir.coding.maskDisplay");
        this.maskSystem = maskingConfiguration.getBooleanValue("fhir.coding.maskSystem");
        this.maskCode = maskingConfiguration.getBooleanValue("fhir.coding.maskCode");

        this.versionMaskingProvider = getMaskingProvider(VERSION_PATH, maskingConfiguration, this.factory);
        this.systemMaskingProvider = getMaskingProvider(SYSTEM_PATH, maskingConfiguration, this.factory);
        this.displayMaskingProvider = getMaskingProvider(DISPLAY_PATH, maskingConfiguration, this.factory);
        this.codeMaskingProvider = getMaskingProvider(CODE_PATH, maskingConfiguration, this.factory);
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRCoding cc = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRCoding.class);
            FHIRCoding maskedCc = mask(cc);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedCc);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRCoding mask(FHIRCoding coding) {
        if (coding == null) {
            return null;
        }

        if (this.maskDisplay && !isAlreadyMasked(DISPLAY_PATH)) {
            String display = coding.getDisplay();
            if (display != null) {
                coding.setDisplay(displayMaskingProvider.mask(display));
            }
        }

        if (this.maskVersion && !isAlreadyMasked(VERSION_PATH)) {
            String version = coding.getVersion();
            if (version != null) {
                coding.setVersion(versionMaskingProvider.mask(version));
            }
        }

        if (this.maskSystem && !isAlreadyMasked(SYSTEM_PATH)) {
            String system = coding.getSystem();
            if (system != null) {
                coding.setSystem(systemMaskingProvider.mask(system));
            }
        }

        if (this.maskCode && !isAlreadyMasked(CODE_PATH)) {
            String code = coding.getCode();
            if (code != null) {
                coding.setCode(codeMaskingProvider.mask(code));
            }
        }

        return coding;
    }
}


