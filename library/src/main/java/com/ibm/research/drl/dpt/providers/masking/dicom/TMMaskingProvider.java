/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.DateTimeMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;

import java.security.SecureRandom;

public class TMMaskingProvider implements MaskingProvider {
    private final DateTimeMaskingProvider dateTimeMaskingProvider;

    /**
     * Instantiates a new Tm masking provider.
     */
    public TMMaskingProvider(MaskingConfiguration maskingConfiguration) {
        /*
        One or more of the components MM, SS, or FFFFFF may be unspecified as long as every component
        to the right of an unspecified component is also unspecified,
        which indicates that the value is not precise to the precision of those unspecified components.
        The FFFFFF component, if present, shall contain 1 to 6 digits. If FFFFFF is unspecified the preceding "."
        shall not be included.

        Examples:
            "070907.0705 " represents a time of 7 hours, 9 minutes and 7.0705 seconds.
            "1010" represents a time of 10 hours, and 10 minutes.
            "021 " is an invalid value.
        */
        /*TODO: see how we can model this, maybe datetime.format.fixed might need to be extended */
        maskingConfiguration.setValue("datetime.format.fixed", "HHmmss.SSSS");
        dateTimeMaskingProvider = new DateTimeMaskingProvider(maskingConfiguration);
    }

    public TMMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {
        this(maskingConfiguration);
    }

    @Override
    public String mask(String identifier) {
        return dateTimeMaskingProvider.mask(identifier);
    }
}

