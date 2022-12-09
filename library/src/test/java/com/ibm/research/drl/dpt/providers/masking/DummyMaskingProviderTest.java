/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking;

import com.ibm.research.drl.dpt.models.OriginalMaskedValuePair;
import com.ibm.research.drl.dpt.models.ValueClass;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.schema.FieldRelationship;
import com.ibm.research.drl.dpt.schema.RelationshipOperand;
import com.ibm.research.drl.dpt.schema.RelationshipType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


public class DummyMaskingProviderTest {
    @Test
    public void testThatDummyDoesNothing() {
        DummyMaskingProvider dummy = new DummyMaskingProvider(null);

        assertThat(dummy.mask("FOO"), equalTo("FOO"));
    }

    @Test
    public void testThatDummyCopiesValueOfLinkedField() {
        DummyMaskingProvider dummy = new DummyMaskingProvider(null);

        RelationshipOperand[] operands = {
                new RelationshipOperand("other", ProviderType.COUNTRY)
        };
        FieldRelationship fieldRelationship = new FieldRelationship(
                ValueClass.TEXT,
                RelationshipType.EQUALS,
                "dummy_name",
                operands);
        OriginalMaskedValuePair operandValuePair = new OriginalMaskedValuePair("FOO1", "BAR");
        Map<String, OriginalMaskedValuePair> maskedValues = Collections.singletonMap("other", operandValuePair);


        assertThat(dummy.mask("FOO", "dummy_name", fieldRelationship, maskedValues), equalTo("BAR"));
    }
}