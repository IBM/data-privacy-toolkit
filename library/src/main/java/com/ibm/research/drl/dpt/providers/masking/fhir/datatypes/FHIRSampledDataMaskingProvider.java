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
import com.ibm.research.drl.dpt.models.fhir.datatypes.FHIRSampledData;
import com.ibm.research.drl.dpt.providers.masking.AbstractComplexMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.util.JsonUtils;

import java.util.Set;

/** FHIRSampledDataMaskingProvider FHIR datatype. */
public class FHIRSampledDataMaskingProvider extends AbstractComplexMaskingProvider<JsonNode> {

    /** Whether to mask the origin element. */
    private final boolean maskOrigin;
    /** Whether to mask the period element. */
    private final boolean maskPeriod;
    /** Whether to mask the factor element. */
    private final boolean maskFactor;
    /** Whether to mask the lower limit element. */
    private final boolean maskLowerLimit;
    /** Whether to mask the upper limit element. */
    private final boolean maskUpperLimit;
    /** Whether to mask the dimensions element. */
    private final boolean maskDimensions;
    /** Whether to mask the data element. */
    private final boolean maskData;

    /** JSON path to the origin field. */
    private final String ORIGIN_PATH;
    /** JSON path to the period field. */
    private final String PERIOD_PATH;
    /** JSON path to the factor field. */
    private final String FACTOR_PATH;
    /** JSON path to the lowerLimit field. */
    private final String LOWERLIMIT_PATH;
    /** JSON path to the upperLimit field. */
    private final String UPPERLIMIT_PATH;
    /** JSON path to the dimensions field. */
    private final String DIMENSIONS_PATH;
    /** JSON path to the data field. */
    private final String DATA_PATH;

    /** Masking provider for the origin element. */
    private final FHIRQuantityMaskingProvider originMaskingProvider;
    /** Masking provider for the period element. */
    private final MaskingProvider periodMaskingProvider;
    /** Masking provider for the factor element. */
    private final MaskingProvider factorMaskingProvider;
    /** Masking provider for the lower limit element. */
    private final MaskingProvider lowerLimitMaskingProvider;
    /** Masking provider for the upper limit element. */
    private final MaskingProvider upperLimitMaskingProvider;
    /** Masking provider for the dimensions element. */
    private final MaskingProvider dimensionsMaskingProvider;
    /** Masking provider for the data element. */
    private final MaskingProvider dataMaskingProvider;

    /**
     * Constructs a FHIRSampledDataMaskingProvider.
     * @param maskingConfiguration the maskingConfiguration
     * @param maskedFields the maskedFields
     * @param fieldPath the fieldPath
     * @param factory the factory
     */
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

    @Override
    /**
     * Masks a JsonNode object.
     * @param node the JsonNode to mask
     * @return the masked JsonNode
     */
    public JsonNode mask(JsonNode node) {
        try {
            FHIRSampledData obj = JsonUtils.MAPPER.treeToValue(node, FHIRSampledData.class);
            FHIRSampledData maskedObj = mask(obj);
            return JsonUtils.MAPPER.valueToTree(maskedObj);
        } catch (Exception e) {
            return NullNode.getInstance();
        }
    }

    /**
     * Masks a FHIR SampledData object.
     * @param sampledData the FHIRSampledData to mask
     * @return the masked FHIRSampledData
     */
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


