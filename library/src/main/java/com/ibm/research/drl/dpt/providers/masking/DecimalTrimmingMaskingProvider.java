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
import com.ibm.research.drl.dpt.util.NumberUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DecimalTrimmingMaskingProvider implements MaskingProvider {
    private final static Logger logger = LogManager.getLogger(DecimalTrimmingMaskingProvider.class);

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
                logger.error("lower threshold is bigger than the upper threshold");
                throw new RuntimeException("lower threshold is bigger than the upper threshold");
            }

            int digitsToKeep = Integer.parseInt(tokens[2]);

            if (digitsToKeep < 0) {
                logger.error("digits to keep cannot be less than zero");
                throw new RuntimeException("digits to keep cannot be less than zero");
            }


            rules.add(new DecimalTrimmingRule(lowerThreshold, upperThreshold, digitsToKeep));
        }

        return rules;
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

        for (DecimalTrimmingRule rule : this.rules) {
            if (value >= rule.getLowerThreshold() && value < rule.getUpperThreshold()) {
                return NumberUtils.trimDecimalDigits(identifier, rule.getDigitsToKeep());
            }
        }

        return identifier;
    }


}
