/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.providers.ProviderType;
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
    @Deprecated(forRemoval = true)
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
    default String mask(String identifier, String fieldName) {
        return mask(identifier);
    }

    /**
     * Mask string.
     *
     * @param identifier        the identifier
     * @param fieldName         the field name
     * @param fieldRelationship the field relationship
     * @param values            the values
     * @return the string
     */
    @Deprecated(forRemoval = true)
    default String mask(String identifier, String fieldName, FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> values) {
        return mask(identifier, fieldName);
    }

    default boolean supportsObject() {
        return false;
    }

    default byte[] mask(Object complex, String fieldName) {
        throw new UnsupportedOperationException();
    }

    default String maskWithKey(String identifier, String key) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskLinked(String identifier, String linkedValue) {
        return maskLinked(identifier, linkedValue, null);
    }

    default String maskLinked(String identifier, String linkedValue, ProviderType providerType) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskProduct(String identifier, String product) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskLess(String identifier, String lesserValue) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskEqual(String identifier, String equalValue) {
        return equalValue;
    }

    default String maskGreater(String identifier, String greaterValue) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskDistance(String identifier, String original, String masked) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }
}
