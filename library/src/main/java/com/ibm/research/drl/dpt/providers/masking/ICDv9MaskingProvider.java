/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.ICDv9Manager;
import com.ibm.research.drl.dpt.models.ICD;
import com.ibm.research.drl.dpt.models.ICDFormat;

import java.security.SecureRandom;

/**
 * The type Ic dv 9 masking provider.
 *
 */
public class ICDv9MaskingProvider extends AbstractMaskingProvider {
    private final static ICDv9Manager icdV9Manager = ICDv9Manager.getInstance();
    private final boolean randomizeToCategory;
    private final boolean randomizeToRange;


    /**
     * Instantiates a new Ic dv 9 masking provider.
     */
    public ICDv9MaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Ic dv 9 masking provider.
     *
     * @param configuration the configuration
     */
    public ICDv9MaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Ic dv 9 masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public ICDv9MaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.random = random;
        this.randomizeToCategory = configuration.getBooleanValue("icd.randomize.category");
        this.randomizeToRange = configuration.getBooleanValue("icd.randomize.chapter");
    }

    @Override
    public String mask(String identifier) {
        ICD icd = icdV9Manager.lookupICD(identifier);
        if (icd == null) {
            return icdV9Manager.getRandomKey();
        }

        ICDFormat format = icd.getFormat();

        if (this.randomizeToRange) {
            if (format == ICDFormat.CODE) {
                return icd.getChapterCode();
            } else {
                return icd.getChapterName();
            }
        } else if (this.randomizeToCategory) {
            if (format == ICDFormat.CODE) {
                return icd.getCategoryCode();
            } else {
                return icd.getCategoryName();
            }
        }

        return icdV9Manager.getRandomKey();
    }
}
