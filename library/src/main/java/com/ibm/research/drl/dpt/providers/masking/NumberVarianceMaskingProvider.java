/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.RandomGenerators;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.security.SecureRandom;

public class NumberVarianceMaskingProvider extends AbstractMaskingProvider {
    private final static Logger log = LogManager.getLogger(NumberVarianceMaskingProvider.class);

    private final double limitDown;
    private final double limitUp;
    private final int precisionDigits;
    private final int failMode;
    
    /**
     * Instantiates a new Number variance masking provider.
     */
    public NumberVarianceMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Number variance masking provider.
     *
     * @param configuration the configuration
     */
    public NumberVarianceMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Number variance masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public NumberVarianceMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.limitDown = configuration.getDoubleValue("numvariance.mask.limitDown");
        this.limitUp = configuration.getDoubleValue("numvariance.mask.limitUp");
        this.precisionDigits = configuration.getIntValue("numvariance.mask.precisionDigits");
        this.failMode = configuration.getIntValue("fail.mode");

        if (this.limitDown < 0) {
            String msg = "numvariance.mask.limitDown cannot be a negative number";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        
        if (this.limitUp < 0) {
            String msg = "numvariance.mask.limitUp cannot be a negative number";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        
        if (this.precisionDigits < -1) {
            String msg = "precisionDigits must be either -1 or >=0";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        if (this.failMode == FailMode.GENERATE_RANDOM) {
            String msg = "Random generation fail mode not supported";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        
    }

    @Override
    public String mask(String identifier) {
        Double number;
        try {
            number = Double.valueOf(identifier);
        } catch(NumberFormatException e) {
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
        
        double percentage = RandomGenerators.randomWithinRange(0.0, limitDown, limitUp);
        number += number * percentage / 100.0;
        
        if (this.precisionDigits == -1) {
            return number.toString();
        }

        return String.format("%." + this.precisionDigits + "f", number);
    }
}
