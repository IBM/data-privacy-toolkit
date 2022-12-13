/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.Age;
import com.ibm.research.drl.dpt.models.AgePortion;
import com.ibm.research.drl.dpt.models.AgePortionFormat;
import com.ibm.research.drl.dpt.providers.identifiers.AgeIdentifier;
import com.ibm.research.drl.dpt.util.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;

public class AgeMaskingProvider extends AbstractMaskingProvider {
    private static final AgeIdentifier AGE_IDENTIFIER = new AgeIdentifier();
    private static final Logger log = LogManager.getLogger(AgeMaskingProvider.class);
    private final boolean redactNumbers;
    private final boolean randomNumbers;
    private final SecureRandom random;
    private final int failMode;

    public AgeMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public AgeMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }

    public AgeMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this.redactNumbers = maskingConfiguration.getBooleanValue("age.mask.redactNumbers");
        this.randomNumbers = maskingConfiguration.getBooleanValue("age.mask.randomNumbers");
        this.random = random;
        this.failMode = maskingConfiguration.getIntValue("fail.mode");
        if (this.failMode == FailMode.GENERATE_RANDOM) {
            String msg = "Random generation fail mode not supported";
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public String mask(String identifier, Age age) {
        AgePortion[] portions = new AgePortion[]{age.getYearPortion(), age.getMonthPortion(), age.getWeeksPortion(), age.getDaysPortion()};
        int[] upperBound = new int[]{100, 12, 48, 365};
        int[] lowerBound = new int[]{1, 1, 0, 0};

        StringBuilder builder = new StringBuilder();
        int lastEnd = 0;

        for (int i = 0; i < portions.length; i++) {
            AgePortion portion = portions[i];
            if (!portion.exists()) {
                continue;
            }

            int start = portion.getStart();

            builder.append(identifier, lastEnd, start);

            if (this.redactNumbers) {
                builder.append("XX");
            } else if (this.randomNumbers) {
                String randomYear = generateRandomNumber(lowerBound[i], upperBound[i], portion.getFormat());
                builder.append(randomYear);
            }

            lastEnd = portion.getEnd();
        }

        builder.append(identifier.substring(lastEnd));

        return builder.toString();
    }

    private String generateRandomNumber(int lower, int upper, AgePortionFormat format) {
        int random = lower + this.random.nextInt(upper - lower);

        if (format == AgePortionFormat.NUMERICAL) {
            return "" + random;
        }

        return NumberUtils.createWords(random);
    }

    @Override
    public String mask(String identifier) {
        Age age = AGE_IDENTIFIER.parseAge(identifier);

        if (age == null) {
            switch (failMode) {
                case FailMode.THROW_ERROR:
                    log.error("invalid age");
                    throw new RuntimeException("invalid age");
                case FailMode.RETURN_ORIGINAL:
                    return identifier;
                case FailMode.RETURN_EMPTY:
                default:
                    return "";
            }
        }

        return mask(identifier, age);
    }

}

