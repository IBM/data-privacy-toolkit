/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.fhir.datatypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRSampledData;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.fhir.FHIRMaskingUtils;

import java.util.Set;

public class FHIRSampledDataMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    private final boolean maskOrigin;
    private final boolean maskPeriod;
    private final boolean maskFactor;
    private final boolean maskLowerLimit;
    private final boolean maskUpperLimit;
    private final boolean maskDimensions;
    private final boolean maskData;

    private final String ORIGIN_PATH;
    private final String PERIOD_PATH;
    private final String FACTOR_PATH;
    private final String LOWERLIMIT_PATH;
    private final String UPPERLIMIT_PATH;
    private final String DIMENSIONS_PATH;
    private final String DATA_PATH;

    private final FHIRQuantityMaskingProvider originMaskingProvider;
    private final MaskingProvider periodMaskingProvider;
    private final MaskingProvider factorMaskingProvider;
    private final MaskingProvider lowerLimitMaskingProvider;
    private final MaskingProvider upperLimitMaskingProvider;
    private final MaskingProvider dimensionsMaskingProvider;
    private final MaskingProvider dataMaskingProvider;

    public FHIRSampledDataMaskingProvider(MaskingConfiguration maskingConfiguration, Set<String> maskedFields, String fieldPath, MaskingProviderFactory factory) {
        super("fhir", maskingConfiguration, maskedFields, factory);

        this.maskOrigin = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskOrigin");
        this.maskPeriod = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskPeriod");
        this.maskFactor = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskFactor");
        this.maskLowerLimit = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskLowerLimit");
        this.maskUpperLimit = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskUpperLimit");
        this.maskDimensions = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskDimensions");
        this.maskData = maskingConfiguration.getBooleanValue("fhir.sampleddata.maskData");

        this.ORIGIN_PATH = fieldPath + "/origin";
        this.PERIOD_PATH = fieldPath + "/period";
        this.FACTOR_PATH = fieldPath + "/factor";
        this.LOWERLIMIT_PATH = fieldPath + "/lowerLimit";
        this.UPPERLIMIT_PATH = fieldPath + "/upperLimit";
        this.DIMENSIONS_PATH = fieldPath + "/dimensions";
        this.DATA_PATH = fieldPath + "/data";

        this.originMaskingProvider = new FHIRQuantityMaskingProvider(getConfigurationForSubfield(ORIGIN_PATH, maskingConfiguration),
                maskedFields, ORIGIN_PATH, factory);
        this.periodMaskingProvider = getMaskingProvider(PERIOD_PATH, maskingConfiguration, this.factory);
        this.factorMaskingProvider = getMaskingProvider(FACTOR_PATH, maskingConfiguration, this.factory);
        this.lowerLimitMaskingProvider = getMaskingProvider(LOWERLIMIT_PATH, maskingConfiguration, this.factory);
        this.upperLimitMaskingProvider = getMaskingProvider(UPPERLIMIT_PATH, maskingConfiguration, this.factory);
        this.dimensionsMaskingProvider = getMaskingProvider(DIMENSIONS_PATH, maskingConfiguration, this.factory);
        this.dataMaskingProvider = getMaskingProvider(DATA_PATH, maskingConfiguration, this.factory);
    }

    public JsonNode mask(JsonNode node) {
        try {
            FHIRSampledData obj = FHIRMaskingUtils.getObjectMapper().treeToValue(node, FHIRSampledData.class);
            FHIRSampledData maskedObj= mask(obj);
            return FHIRMaskingUtils.getObjectMapper().valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    public FHIRSampledData mask(FHIRSampledData sampledData) {

        if (this.maskOrigin && !isAlreadyMasked(ORIGIN_PATH)) {
            sampledData.setOrigin(originMaskingProvider.mask(sampledData.getOrigin()));
        }

        if (this.maskData && !isAlreadyMasked(DATA_PATH)) {
            String data = sampledData.getData();
            if (data != null) {
                sampledData.setData(dataMaskingProvider.mask(data));
            }
        }

        if (this.maskDimensions && !isAlreadyMasked(DIMENSIONS_PATH)) {
            String dimensions = sampledData.getDimensions();
            if (dimensions != null) {
                sampledData.setDimensions(dimensionsMaskingProvider.mask(dimensions));
            }
        }

        if (this.maskPeriod && !isAlreadyMasked(PERIOD_PATH)) {
            float period = sampledData.getPeriod();
            sampledData.setPeriod(Float.valueOf(periodMaskingProvider.mask(Float.toString(period))));
        }

        if (this.maskLowerLimit && !isAlreadyMasked(LOWERLIMIT_PATH)) {
            float lowerLimit = sampledData.getLowerLimit();
            sampledData.setLowerLimit(Float.valueOf(lowerLimitMaskingProvider.mask(Float.toString(lowerLimit))));
        }

        if (this.maskUpperLimit && !isAlreadyMasked(UPPERLIMIT_PATH)) {
            float upperLimit = sampledData.getUpperLimit();
            sampledData.setUpperLimit(Float.valueOf(upperLimitMaskingProvider.mask(Float.toString(upperLimit))));
        }

        if (this.maskFactor && !isAlreadyMasked(FACTOR_PATH)) {
            float factor = sampledData.getFactor();
            sampledData.setFactor(Float.valueOf(factorMaskingProvider.mask(Float.toString(factor))));
        }

        return sampledData;
    }
}


