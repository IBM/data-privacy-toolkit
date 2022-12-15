/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.OccupationManager;
import com.ibm.research.drl.dpt.models.Occupation;

import java.security.SecureRandom;
import java.util.List;

public class OccupationMaskingProvider extends AbstractMaskingProvider {
    /**
     * The constant occupationManager.
     */
    public static final OccupationManager occupationManager = OccupationManager.getInstance();
    private final boolean generalizeToCategory;

    /**
     * Instantiates a new Occupation masking provider.
     */
    public OccupationMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    /**
     * Instantiates a new Occupation masking provider.
     *
     * @param configuration the configuration
     */
    public OccupationMaskingProvider(MaskingConfiguration configuration) {
        this(new SecureRandom(), configuration);
    }

    /**
     * Instantiates a new Occupation masking provider.
     *
     * @param random        the random
     * @param configuration the configuration
     */
    public OccupationMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.generalizeToCategory = configuration.getBooleanValue("occupation.mask.generalize");
        this.random = random;
    }


    @Override
    public String mask(String identifier) {
        Occupation occupation = occupationManager.getKey(identifier);

        if (this.generalizeToCategory) {
            if (occupation == null) {
                /* TODO: return a random category */
                return occupationManager.getRandomKey();
            }
            List<String> categories = occupation.getCategories();
            int randomIndex = random.nextInt(categories.size());
            return categories.get(randomIndex);
        } else {
            if (occupation == null) {
                return occupationManager.getRandomKey();
            }

            return occupationManager.getRandomKey(occupation.getNameCountryCode());
        }
    }
}

