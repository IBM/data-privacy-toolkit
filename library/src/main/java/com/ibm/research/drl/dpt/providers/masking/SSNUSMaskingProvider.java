/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.SSNUS;
import com.ibm.research.drl.dpt.providers.identifiers.SSNUSIdentifier;

import java.security.SecureRandom;

public class SSNUSMaskingProvider extends AbstractMaskingProvider {
    private static final SSNUSIdentifier ssnUSIdentifier = new SSNUSIdentifier();

    private final boolean preserveAreaNumber;
    private final boolean preserveGroup;

    /**
     * Instantiates a new Ssnus masking provider.
     */
    public SSNUSMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Ssnus masking provider.
     *
     * @param random the random
     */
    public SSNUSMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Ssnus masking provider.
     *
     * @param configuration the configuration
     */
    public SSNUSMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Ssnus masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public SSNUSMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveAreaNumber = configuration.getBooleanValue("ssnus.mask.preserveAreaNumber");
        this.preserveGroup = configuration.getBooleanValue("ssnus.mask.preserveGroup");
    }

    /*
    The Social Security number is a nine-digit number in the format "AAA-GG-SSSS".[27]
    The number is divided into three parts.
    The area number, the first three digits, is assigned by geographical region.
    The middle two digits are the group number
    The last four digits are serial numbers
     */
    @Override
    public String mask(String identifier) {
        SSNUS ssn = ssnUSIdentifier.parseSSNUS(identifier);
        String areaNumber;
        String group;
        String serialNumber = String.format("%04d", random.nextInt(9999));

        if (ssn != null) {
            if (this.preserveAreaNumber) {
                areaNumber = ssn.getAreaNumber();
            } else {
                while(true) {
                    int areaNumberInt = random.nextInt(999);
                    if (areaNumberInt == 0 || areaNumberInt == 666) {
                        continue;
                    }
                    areaNumber = String.format("%03d", areaNumberInt);
                    break;
                }
            }

            if (this.preserveGroup) {
                group = ssn.getGroup();
            } else {
                group = String.format("%02d", random.nextInt(99));
            }
        } else {
            areaNumber = String.format("%03d", random.nextInt(999));
            group = String.format("%02d", random.nextInt(99));
        }

        return (new SSNUS(areaNumber, group, serialNumber)).toString();

    }
}

