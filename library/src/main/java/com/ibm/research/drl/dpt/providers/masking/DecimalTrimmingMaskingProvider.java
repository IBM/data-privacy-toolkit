/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;


import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.NumberUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DecimalTrimmingMaskingProvider extends AbstractMaskingProvider {
    private final static Logger log = LogManager.getLogger(DecimalTrimmingMaskingProvider.class);

    public static class DecimalTrimmingRule {
        private final double lowerThreshold;
        private final double upperThreshold;
        private final int digitsToKeep;

        public double getLowerThreshold() {
            return lowerThreshold;
        }

        public double getUpperThreshold() {
            return upperThreshold;
        }

        public int getDigitsToKeep() {
            return digitsToKeep;
        }

        public DecimalTrimmingRule(double lowerThreshold, double upperThreshold, int digitsToKeep) {
            this.lowerThreshold = lowerThreshold;
            this.upperThreshold = upperThreshold;
            this.digitsToKeep = digitsToKeep;
        }
    }

    private final List<DecimalTrimmingRule> rules;
    private final int failMode;

    public DecimalTrimmingMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public DecimalTrimmingMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    public DecimalTrimmingMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    public DecimalTrimmingMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.rules = parseRules(maskingConfiguration.getStringValue("decimalrounding.mask.rules"));
        this.failMode = maskingConfiguration.getIntValue("fail.mode");
    }

    public static List<DecimalTrimmingRule> parseRules(String stringValue) {
        List<DecimalTrimmingRule> rules = new ArrayList<>();

        String[] parts = stringValue.split(";");
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }

            String[] tokens = part.split(",");
            double lowerThreshold = Double.parseDouble(tokens[0]);
            double upperThreshold = Double.parseDouble(tokens[1]);

            if (lowerThreshold > upperThreshold) {
                log.error("lower threshold is bigger than the upper threshold");
                throw new RuntimeException("lower threshold is bigger than the upper threshold");
            }

            int digitsToKeep = Integer.parseInt(tokens[2]);

            if (digitsToKeep < 0) {
                log.error("digits to keep cannot be less than zero");
                throw new RuntimeException("digits to keep cannot be less than zero");
            }


            rules.add(new DecimalTrimmingRule(lowerThreshold, upperThreshold, digitsToKeep));
        }

        return rules;
    }

    @Override
    public String mask(String identifier) {

        Double value;

        try {
            value = Double.parseDouble(identifier);
        } catch (NumberFormatException e) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    log.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        for (DecimalTrimmingRule rule : this.rules) {
            if (value >= rule.getLowerThreshold() && value < rule.getUpperThreshold()) {
                return NumberUtils.trimDecimalDigitis(identifier, rule.getDigitsToKeep());
            }
        }

        return identifier;
    }


}
