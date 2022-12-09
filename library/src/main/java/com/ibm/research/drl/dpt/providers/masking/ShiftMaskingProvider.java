/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.FailMode;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.util.NumberUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;;

import java.security.SecureRandom;

/**
 * The type Shift masking provider.
 *
 */
public class ShiftMaskingProvider extends AbstractMaskingProvider {
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
        }
        catch (Exception e) {
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
