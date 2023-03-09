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
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;
import java.util.Map;

public class RatioBasedMaskingProvider implements MaskingProvider {
    private static final Logger logger = LogManager.getLogger(RatioBasedMaskingProvider.class);

    private final int failMode;
    private final double ratio;
    private final int precisionDigits;

    public RatioBasedMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public RatioBasedMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    public RatioBasedMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    public RatioBasedMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.ratio = maskingConfiguration.getDoubleValue("ratiobased.mask.ratio");
        this.precisionDigits = maskingConfiguration.getIntValue("ratiobased.mask.precisionDigits");

        this.failMode = maskingConfiguration.getIntValue("fail.mode");

        if (this.failMode == FailMode.GENERATE_RANDOM) {
            String msg = "Random generation fail mode not supported";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
    }

    private String formatResult(Double v) {
        if (this.precisionDigits == -1) {
            return v.toString();
        }

        return String.format("%." + this.precisionDigits + "f", v);
    }

    private String maskWithRatioAsOperand(double value, FieldRelationship fieldRelationship,
                                          Map<String, OriginalMaskedValuePair> values, String identifier) {
        String operandName = fieldRelationship.getOperands()[0].getName();

        String operandValueString = values.get(operandName).getOriginal();

        try {
            double operandValue = Double.parseDouble(operandValueString);
            double masked = value * operandValue;
            return formatResult(masked);
        } catch (NumberFormatException e) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    logger.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }
    }

    @Override
    public String maskWithKey(String value, String operandValueString) {
        try {
            double operandValue = Double.parseDouble(operandValueString);
            double masked = Double.parseDouble(value) * operandValue;
            return formatResult(masked);
        } catch (NumberFormatException e) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return value;
                case FailMode.THROW_ERROR:
                    logger.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }
    }

    @Override
    public String maskWithRatio(String identifier, String operandMasked, String operandOriginal) {
        double value;
        try {
            value = Double.parseDouble(identifier);
        } catch (NumberFormatException e) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    logger.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        try {
            double originalBase = Double.parseDouble(operandOriginal);
            double maskedBase = Double.parseDouble(operandMasked);

            double ratio = originalBase / value;

            double masked = maskedBase / ratio;
            return formatResult(masked);
        } catch (NumberFormatException e) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    logger.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

    }


    @Override
    public String mask(String identifier) {
        double value;
        try {
            value = Double.parseDouble(identifier);
        } catch (NumberFormatException e) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    logger.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        double masked = value * this.ratio;
        return formatResult(masked);
    }
}


