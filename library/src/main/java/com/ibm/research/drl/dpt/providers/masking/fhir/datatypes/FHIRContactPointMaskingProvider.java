/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRContactPoint;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.security.SecureRandom;
import java.util.Set;

public class FHIRContactPointMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final boolean randomizeUse;
    private final boolean removeExtensions;
    private final boolean maskValue;
    private final boolean maskPeriod;
    private final boolean removeSystem;

    private final MaskingProvider randomMaskingProvider;
    private final FHIRPeriodMaskingProvider periodMaskingProvider;
    private final MaskingProvider phoneMaskingProvider;
    private final MaskingProvider emailMaskingProvider;
    private final MaskingProvider urlMaskingProvider;

    private final String valuePath;
    private final String usePath;
    private final String periodPath;

    private final static SecureRandom random = new SecureRandom();
    /* https://www.hl7.org/fhir/valueset-contact-point-use.html */
    private final static String[] useValues = new String[] {"home", "work", "temp", "old", "mobile"};

    public FHIRContactPointMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.valuePath = fieldPath + "/value";
        this.usePath = fieldPath + "/use";
        this.periodPath = fieldPath + "/period";

        this.randomMaskingProvider = getMaskingProvider(valuePath, maskingConfiguration, this.factory);
        this.phoneMaskingProvider = this.factory.get(ProviderType.PHONE, getConfigurationForSubfield(valuePath, maskingConfiguration));
        this.emailMaskingProvider = this.factory.get(ProviderType.EMAIL, getConfigurationForSubfield(valuePath, maskingConfiguration));
        this.urlMaskingProvider = this.factory.get(ProviderType.URL, getConfigurationForSubfield(valuePath, maskingConfiguration));

        this.randomizeUse = maskingConfiguration.getBooleanValue("fhir.contactPoint.randomizeUse");
        this.maskValue = maskingConfiguration.getBooleanValue("fhir.contactPoint.maskValue");
        this.maskPeriod = maskingConfiguration.getBooleanValue("fhir.contactPoint.maskPeriod");
        this.removeExtensions = maskingConfiguration.getBooleanValue("fhir.contactPoint.removeExtensions");
        this.removeSystem = maskingConfiguration.getBooleanValue("fhir.contactPoint.removeSystem");

        this.periodMaskingProvider = new FHIRPeriodMaskingProvider(getConfigurationForSubfield(periodPath, maskingConfiguration), maskedFields, periodPath, this.factory);
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRContactPoint cc = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRContactPoint.class);
            FHIRContactPoint maskedCc = mask(cc);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedCc);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRContactPoint mask(FHIRContactPoint contactPoint) {

        if (contactPoint == null) {
            return null;
        }

        if (this.removeSystem) {
            contactPoint.setSystem(null);
        }

        String originalValue = contactPoint.getValue();

        if (this.maskValue && !isAlreadyMasked(valuePath) && originalValue != null) {
            String maskedValue;
            String system = contactPoint.getSystem();

            if (system == null) {
                maskedValue = randomMaskingProvider.mask(originalValue);
            }
            else {
                /* https://www.hl7.org/fhir/valueset-contact-point-system.html */
                switch (system) {
                    case "email":
                        maskedValue = emailMaskingProvider.mask(originalValue);
                        break;
                    case "other":
                        maskedValue = urlMaskingProvider.mask(originalValue);
                        break;
                    default:
                        maskedValue = phoneMaskingProvider.mask(originalValue);
                        break;
                }
            }

            contactPoint.setValue(maskedValue);
        }

        if (this.randomizeUse && !isAlreadyMasked(usePath)) {
            String randomUse = useValues[random.nextInt(useValues.length)];
            contactPoint.setUse(randomUse);
        }

        if (this.maskPeriod && !isAlreadyMasked(periodPath)) {
            contactPoint.setPeriod(periodMaskingProvider.mask(contactPoint.getPeriod()));
        }

        if (this.removeExtensions) {
            contactPoint.setExtension(null);
        }

        return contactPoint;
    }
}


