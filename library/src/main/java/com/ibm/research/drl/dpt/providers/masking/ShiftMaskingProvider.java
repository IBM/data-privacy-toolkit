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

/**
 * The type Shift masking provider.
 */
public class ShiftMaskingProvider implements MaskingProvider {
    private static final Logger log = LogManager.getLogger(ShiftMaskingProvider.class);

    private final double shiftValue;
    private final int failMode;
    private final int digitsToKeep;

    /**
     * Instantiates a new Shift masking provider.
     */
    public ShiftMaskingProvider() {
        this(new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Shift masking provider.
     *
     * @param configuration the configuration
     */
    public ShiftMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    public ShiftMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.shiftValue = configuration.getDoubleValue("shift.mask.value");
        this.digitsToKeep = configuration.getIntValue("shift.mask.digitsToKeep");
        this.failMode = configuration.getIntValue("fail.mode");

        if (this.failMode == FailMode.GENERATE_RANDOM) {
            String msg = "Random generation fail mode not supported";
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public String mask(String identifier) {

        try {
            double k = Double.parseDouble(identifier);
            String maskedValue = String.format("%f", k + shiftValue);
            return NumberUtils.trimDecimalDigitis(maskedValue, this.digitsToKeep);
        } catch (Exception e) {
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
    }
}
