/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;
import java.util.Map;

public class RatioBasedMaskingProvider extends AbstractMaskingProvider {
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
    public String mask(String identifier, String fieldName,
                       FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> values) {

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

        if (fieldRelationship.getRelationshipType() == RelationshipType.KEY) {
            return maskWithRatioAsOperand(value, fieldRelationship, values, identifier);
        }


        RelationshipOperand[] operands = fieldRelationship.getOperands();
        String baseValueField = operands[0].getName();

        OriginalMaskedValuePair pair = values.get(baseValueField);
        if (pair == null) {
            return mask(identifier);
        }

        String originalBaseValue = pair.getOriginal();
        String maskedBaseValue = pair.getMasked();

        try {
            Double originalBase = Double.parseDouble(originalBaseValue);
            Double maskedBase = Double.parseDouble(maskedBaseValue);

            double ratio = originalBase / value;

            Double masked = maskedBase / ratio;
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
            value = Double.valueOf(identifier);
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

        Double masked = value * this.ratio;
        return formatResult(masked);
    }

}


