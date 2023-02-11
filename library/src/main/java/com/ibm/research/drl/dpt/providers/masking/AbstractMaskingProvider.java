/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Map;

public abstract class AbstractMaskingProvider implements MaskingProvider, Serializable {
    protected SecureRandom random;

    @Override
    public String[] mask(String[] data) {
        final String[] maskedData = new String[data.length];

        for (int i = 0; i < data.length; ++i)
            maskedData[i] = mask(data[i]);

        return maskedData;
    }

    @Override
    public String mask(String identifier, String fieldName) {
        return mask(identifier);
    }

    @Override
    public byte[] mask(byte[] data) {
        return mask(new String(data)).getBytes();
    }
}
