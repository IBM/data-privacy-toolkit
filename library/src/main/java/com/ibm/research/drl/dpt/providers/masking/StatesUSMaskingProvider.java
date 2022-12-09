/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.managers.StatesUSManager;
import com.ibm.research.drl.dpt.models.State;

import java.security.SecureRandom;

public class StatesUSMaskingProvider extends AbstractMaskingProvider {
    private final static StatesUSManager statesUSManager = StatesUSManager.getInstance();

    public StatesUSMaskingProvider() {
        this(new SecureRandom(), new DefaultMaskingConfiguration());
    }

    public StatesUSMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration) {

    }

    @Override
    public String mask(String identifier) {
        State state = statesUSManager.getKey(identifier);

        if (state == null) {
            return statesUSManager.getRandomKey();
        }

        State randomState = statesUSManager.getRandomValue(state.getNameCountryCode());

        return randomState.toString(state.getNameFormat());
    }
}
