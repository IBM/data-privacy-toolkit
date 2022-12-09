/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2016                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;

import java.security.SecureRandom;


public class RedactMaskingProvider extends AbstractMaskingProvider {
    private final boolean preserveLength;
    private final String replacementCharacter;
    private final int replacementLength;

    public RedactMaskingProvider(MaskingConfiguration maskingConfiguration) {
        this(new SecureRandom(), maskingConfiguration);
    }
    
    public RedactMaskingProvider(SecureRandom random, MaskingConfiguration configuration) {
        this.preserveLength = configuration.getBooleanValue("redact.preserve.length");
        this.replacementCharacter = configuration.getStringValue("redact.replace.character");
        this.replacementLength = configuration.getIntValue("redact.replace.length");
    }

    @Override
    public String mask(String identifier) {
        final int length = (preserveLength ? identifier.length() : replacementLength);

        StringBuilder builder = new StringBuilder(identifier.length());

        for (int i = 0; i < length; ++i) {
            builder.append(replacementCharacter);
        }

        return builder.toString();

        //return replacementCharacter.repeat(length); //  bump to Java 11+
    }
}
