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
package com.ibm.research.drl.dpt.anonymization;

import com.fasterxml.jackson.core.JsonProcessingException;
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