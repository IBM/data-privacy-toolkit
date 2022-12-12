/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.anonymization.constraints.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class PrivacyConstraintTest {
    private final ObjectMapper mapper = new ObjectMapper();

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
                    mapper.writeValueAsString(constraint)
            );
        }

        for (String serializedConstraint : serializedConstraints) {
            PrivacyConstraint constraint = mapper.readValue(serializedConstraint, PrivacyConstraint.class);
            assertNotNull(constraint);
        }
    }
}