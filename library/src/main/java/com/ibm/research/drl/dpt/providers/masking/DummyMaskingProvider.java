/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/

package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;

import java.util.Map;

/**
 * The type Dummy masking provider.
 *
 */
public class DummyMaskingProvider extends AbstractMaskingProvider {

    /**
     * Instantiates a new Dummy masking provider.
     */
    public DummyMaskingProvider() {

    }

    /**
     * Instantiates a new Dummy masking provider.
     *
     * @param configuration the configuration
     */
    public DummyMaskingProvider(MaskingConfiguration configuration) {
        this();
    }

    @Override
    public String mask(String identifier) {
        return identifier;
    }

    @Override
    public String mask(String identifier, String fieldName, FieldRelationship fieldRelationship, Map<String, OriginalMaskedValuePair> maskedValues) {
        if (fieldRelationship.getRelationshipType().equals(RelationshipType.EQUALS)) {
            RelationshipOperand operand = fieldRelationship.getOperands()[0];
            OriginalMaskedValuePair operandValues = maskedValues.get(operand.getName());

            return operandValues.getMasked();
        }

        return mask(identifier);
    }
}
