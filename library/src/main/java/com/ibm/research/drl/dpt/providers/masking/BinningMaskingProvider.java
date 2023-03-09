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
import com.ibm.research.drl.dpt.exceptions.MisconfigurationException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BinningMaskingProvider implements MaskingProvider {
    private static final Logger log = LogManager.getLogger(BinningMaskingProvider.class);

    private final int binSize;
    private final String format;
    private final boolean returnBinMean;
    private final int failMode;

    public BinningMaskingProvider() {
        this(new DefaultMaskingConfiguration());
    }

    public BinningMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this.binSize = maskingConfiguration.getIntValue("binning.mask.binSize");

        if (this.binSize <= 1) {
            String msg = "binning.mask.binSize must be >1";
            log.error(msg);
            throw new MisconfigurationException(msg);
        }

        this.format = maskingConfiguration.getStringValue("binning.mask.format");
        this.returnBinMean = maskingConfiguration.getBooleanValue("binning.mask.returnBinMean");
        this.failMode = maskingConfiguration.getIntValue("fail.mode");

        if (this.failMode == FailMode.GENERATE_RANDOM) {
            String msg = "Random generation fail mode not supported";
            log.error(msg);
            throw new MisconfigurationException(msg);
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
                    log.error("invalid numerical value");
                    throw new IllegalArgumentException("invalid numerical value");
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        int intValue = (int) value;

        int lowerBase = intValue - (intValue % binSize);
        int higherBase = lowerBase + binSize;

        if (returnBinMean) {
            return String.format("%f", ((double) lowerBase + (double) higherBase) / 2.0);
        }

        return String.format(this.format, lowerBase, higherBase);
    }

}


