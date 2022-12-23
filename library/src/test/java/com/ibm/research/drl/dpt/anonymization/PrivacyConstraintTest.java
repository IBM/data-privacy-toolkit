/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.constraints.*;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class PrivacyConstraintTest {
    @Test
    public void testSerializationDeSerialization() throws JsonProcessingException {
        List<PrivacyConstraint> constraints = Arrays.asList(
                new KAnonymity(10),
                new DistinctLDiversity(5),
                new EntropyLDiversity(5),
                new RecursiveCLDiversity(5, 5.5),
                new TCloseness(0.9)
                //, new ReidentificationRisk()
        );

        List<String> serializedConstraints = new ArrayList<>();

        for (PrivacyConstraint constraint : constraints) {
            serializedConstraints.add(
                    JsonUtils.MAPPER.writeValueAsString(constraint)
            );
        }

        for (String serializedConstraint : serializedConstraints) {
            PrivacyConstraint constraint = JsonUtils.MAPPER.readValue(serializedConstraint, PrivacyConstraint.class);
            assertNotNull(constraint);
        }
    }
}