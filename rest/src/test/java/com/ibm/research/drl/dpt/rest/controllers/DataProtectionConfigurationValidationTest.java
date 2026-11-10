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
package com.ibm.research.drl.dpt.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DefaultMaskingConfiguration;
import com.ibm.research.drl.dpt.rest.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataProtectionConfigurationValidationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private DefaultMaskingConfiguration parse(String json) throws Exception {
        return mapper.readValue(json, DefaultMaskingConfiguration.class);
    }

    @Test
    void rejectsDictionaryFilename() throws Exception {
        DefaultMaskingConfiguration configuration = parse("{\"options\":{\"dictionaryBased.mask.filename\":{\"value\":\"/etc/passwd\"}}}");

        assertThrows(InvalidRequestException.class, () -> DataProtection.validateClientConfiguration(configuration));
    }

    @Test
    void rejectsPersistenceOptions() throws Exception {
        DefaultMaskingConfiguration configuration = parse("{\"options\":{"
                + "\"persistence.export\":{\"value\":true},"
                + "\"persistence.type\":{\"value\":\"file\"},"
                + "\"persistence.file\":{\"value\":\"/tmp/dpt-persist\"},"
                + "\"persistence.namespace\":{\"value\":\"../dpt-escaped.txt\"}}}");

        assertThrows(InvalidRequestException.class, () -> DataProtection.validateClientConfiguration(configuration));
    }

    @Test
    void rejectsUnknownOptions() throws Exception {
        DefaultMaskingConfiguration configuration = parse("{\"options\":{\"does.not.exist\":{\"value\":\"x\"}}}");

        assertThrows(InvalidRequestException.class, () -> DataProtection.validateClientConfiguration(configuration));
    }

    @Test
    void acceptsDefaultsAndNonResourceOptions() throws Exception {
        DefaultMaskingConfiguration defaults = new DefaultMaskingConfiguration();
        assertDoesNotThrow(() -> DataProtection.validateClientConfiguration(defaults));

        DefaultMaskingConfiguration configuration = parse("{\"options\":{\"replace.mask.preserve\":{\"value\":4}}}");
        assertDoesNotThrow(() -> DataProtection.validateClientConfiguration(configuration));
    }

    @Test
    void rejectsTypeMismatches() throws Exception {
        for (String options : new String[]{
                "\"replace.mask.preserve\":{\"value\":\"four\"}",       // number expected
                "\"fail.mode\":{\"value\":true}",                         // number expected
                "\"persistence.normalize.toLower\":{\"value\":\"yes\"}", // boolean expected
                "\"default.masking.provider\":{\"value\":5}",            // string expected
                "\"replace.mask.testValues\":{\"value\":[\"a\",\"b\"]}", // collections never accepted
                "\"replace.mask.offset\":{\"value\":{\"a\":1}}",         // objects never accepted
                "\"replace.mask.preserve\":null"                          // null option
        }) {
            DefaultMaskingConfiguration configuration = parse("{\"options\":{" + options + "}}");

            assertThrows(InvalidRequestException.class, () -> DataProtection.validateClientConfiguration(configuration), options);
        }
    }

    @Test
    void rejectsOversizedStrings() throws Exception {
        DefaultMaskingConfiguration configuration = parse("{\"options\":{\"default.masking.provider\":{\"value\":\"" + "A".repeat(5000) + "\"}}}");

        assertThrows(InvalidRequestException.class, () -> DataProtection.validateClientConfiguration(configuration));
    }

    @Test
    void roundTripOfDefaultsIsAccepted() throws Exception {
        String json = mapper.writeValueAsString(new DefaultMaskingConfiguration());

        assertDoesNotThrow(() -> DataProtection.validateClientConfiguration(parse(json)));
    }
}
