/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.GenderManager;
import com.ibm.research.drl.dpt.models.Sex;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.SecureRandom;

public class GenderMaskingProvider extends AbstractMaskingProvider {
    private static final Logger log = LogManager.getLogger(GenderMaskingProvider.class);

    private static final GenderManager genderManager = GenderManager.getInstance();
    private final int failMode;

    public GenderMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public GenderMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.failMode = maskingConfiguration.getIntValue("fail.mode");
    }

    @Override
    public String mask(String identifier) {
        Sex sex = genderManager.getKey(identifier);
        if (sex == null) {
            switch (failMode) {
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.THROW_ERROR:
                    log.error("invalid gender identifier");
                    throw new RuntimeException("invalid gender identifier");
                case FailMode.GENERATE_RANDOM:
                    return genderManager.getRandomKey();
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        return genderManager.getRandomKey(sex.getNameCountryCode());
    }
}


