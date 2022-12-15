/******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.dicom;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.AbstractMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProviderFactory;
import com.ibm.research.drl.dpt.providers.masking.NameMaskingProvider;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PNMaskingProvider extends AbstractMaskingProvider {
    private final MaskingProvider nameMaskingProvider;

    public PNMaskingProvider(SecureRandom random, MaskingConfiguration maskingConfiguration, MaskingProviderFactory factory) {
        this.nameMaskingProvider = new NameMaskingProvider(random, maskingConfiguration, factory);
    }

    @Override
    public String mask(String identifier) {
        String[] tokens = identifier.split("\\^");
        List<String> anonymizedTokens = new ArrayList<>();

        if (tokens.length >= 1) {
            String name = tokens[0];
            if (name.length() > 64) {
                name = name.substring(0, 64);
            }
            anonymizedTokens.add(nameMaskingProvider.mask(name));
        }

        if (tokens.length >= 2) {
            String surname = tokens[1];
            if (surname.length() > 64) {
                surname = surname.substring(0, 64);
            }
            anonymizedTokens.add(nameMaskingProvider.mask(surname));
        }

        if (tokens.length >= 2) {
            anonymizedTokens.addAll(Arrays.asList(tokens).subList(2, tokens.length));
        }

        return StringUtils.join(anonymizedTokens, '^');

    }
}
