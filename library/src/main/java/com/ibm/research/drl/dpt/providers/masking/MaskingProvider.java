/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface Masking provider.
 *
 */
public interface MaskingProvider extends Serializable {
    /**
     * Mask string [ ].
     *
     * @param data the data
     * @return the string [ ]
     */
    String[] mask(final String[] data);

    /**
     * Mask string.
     *
     * @param identifier the identifier to mask
     * @return the masked result
     */
    String mask(String identifier);

    /**
     * Mask byte [ ].
     *
     * @param data the data
     * @return the byte [ ]
     */
    byte[] mask(byte[] data);

    /**
     * Mask string.
     *
     * @param identifier the identifier
     * @param fieldName  the field name
     * @return the string
     */
    String mask(String identifier, String fieldName);

    /**
     * Mask string.
     *
     * @param identifier        the identifier
     * @param fieldName         the field name
     * @param fieldRelationship the field relationship
     * @param values            the values
     * @return the string
     */
    String mask(String identifier, String fieldName, FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> values);

    default boolean supportsObject() {
        return false;
    }

    default byte[] mask(Object complex, String fieldName) {
        throw new UnsupportedOperationException();
    }
}
