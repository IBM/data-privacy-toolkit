/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;


public class MonetaryMaskingProvider implements MaskingProvider {
    private final String replacingCharacter;
    private final boolean preserveSize;

    public MonetaryMaskingProvider(MaskingConfiguration configuration) {

        replacingCharacter = configuration.getStringValue("monetary.replacing.character");
        preserveSize = configuration.getBooleanValue("monetary.preserve.size");
    }

    @Override
    public String mask(String identifier) {
        final StringBuilder builder = new StringBuilder();

        if (preserveSize) {
            for (final char c : identifier.toCharArray()) {
                if (Character.isDigit(c)) {
                    builder.append(replacingCharacter);
                } else {
                    builder.append(c);
                }
            }
        } else {
            for (final char c : identifier.toCharArray()) {
                if (!Character.isAlphabetic(c)) {
                    builder.append(c);
                }
            }
        }

        return builder.toString();
    }
}
