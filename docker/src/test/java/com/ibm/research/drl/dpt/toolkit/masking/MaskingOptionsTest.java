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
package com.ibm.research.drl.dpt.toolkit.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaskingOptionsTest {
    @Test
    public void testShouldDeserializeCorrectly() throws IOException {
        try (InputStream inputStream = MaskingOptionsTest.class.getResourceAsStream("/configuration_masking_shift.json")) {
            JsonNode treeNode = JsonUtils.MAPPER.readTree(inputStream);
            JsonNode taskOptionsTreeNode = treeNode.get("taskOptions");

            MaskingOptions maskingOptions = JsonUtils.MAPPER.treeToValue(taskOptionsTreeNode, MaskingOptions.class);
            assertThat(maskingOptions, is(not(nullValue())));
        }
    }
}