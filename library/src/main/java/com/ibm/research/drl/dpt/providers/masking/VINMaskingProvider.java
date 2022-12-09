/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.VINManager;
import com.ibm.research.drl.dpt.providers.identifiers.VINIdentifier;
import com.ibm.research.drl.dpt.util.RandomGenerators;

import java.security.SecureRandom;

public class VINMaskingProvider extends AbstractMaskingProvider {
    private static final char[] allowedCharacters = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789".toCharArray();
    private final boolean preserveWMI;
    private final boolean preserveVDS;
    private final VINIdentifier vinIdentifier = new VINIdentifier();
    private final VINManager vinManager = new VINManager();

    /**
     * Instantiates a new Vin masking provider.
     */
    public VINMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Vin masking provider.
     *
     * @param random the random
     */
    public VINMaskingProvider(SecureRandom random) {
        this(random, new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Vin masking provider.
     *
     * @param configuration the configuration
     */
    public VINMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Vin masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public VINMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.preserveWMI = configuration.getBooleanValue("vin.wmi.preserve");
        this.preserveVDS = configuration.getBooleanValue("vin.vds.preserve");
    }

    private String randomVIN() {
        String wmi = vinManager.getRandomWMI();
        String vds = RandomGenerators.randomUIDGeneratorWithInclusions(6, allowedCharacters);
        String vis = RandomGenerators.randomUIDGeneratorWithInclusions(8, allowedCharacters);
        return String.format("%s%s%s", wmi, vds, vis);
    }

    @Override
    public String mask(String identifier) {
        if (!vinIdentifier.isOfThisType(identifier)) {
            return randomVIN();
        }

        String wmi = identifier.substring(0, 3);
        String vds = null;
        String vis = RandomGenerators.randomUIDGeneratorWithInclusions(8, allowedCharacters);

        if (!this.preserveWMI) {
            wmi = vinManager.getRandomWMI(wmi);
        }

        if(!this.preserveVDS) {
            vds = RandomGenerators.randomUIDGeneratorWithInclusions(6, allowedCharacters);
        }
        else {
            vds = identifier.substring(3, 9);
        }

        return wmi + vds + vis;
    }

}

