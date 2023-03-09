/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The interface Masking provider
 *
 */
public interface MaskingProvider extends Serializable {
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
    default byte[] mask(byte[] data) {
        return mask(new String(data)).getBytes();
    }

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

    default boolean supportsObject() {
        return false;
    }

    default byte[] mask(Object complex, String fieldName) {
        throw new UnsupportedOperationException();
    }

    default String maskWithKey(String identifier, String key) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskLinked(String identifier, String linkedValue, ProviderType providerType) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskProduct(String identifier, String product) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskLess(String identifier, String greaterValue, String originalGreaterValue) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskEqual(String identifier, String equalValue) {
        return equalValue;
    }

    default String maskGreater(String identifier, String lesserValue, String originalLesserValue) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskDistance(String identifier, String original, String masked) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskGrepAndMask(String identifier, List<String> targetToken) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }

    default String maskWithRatio(String identifier, String operandMasked, String operandOriginal) {
        throw new UnsupportedOperationException("This relationship operation is not supported");
    }
}
