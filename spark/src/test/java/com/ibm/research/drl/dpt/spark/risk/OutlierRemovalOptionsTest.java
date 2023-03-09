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
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class OutlierRemovalOptionsTest {
    @Test
    public void testValidConfiguration() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/outlierRemovalValid.json")) {
            OutlierRemovalOptions options = new ObjectMapper().readValue(inputStream, OutlierRemovalOptions.class);

            assertNotNull(options);
            List<String> identities = options.getFilters().get(0).getIdentityFields();
            assertNotNull(identities);
            assertFalse(identities.isEmpty());
            assertThat(identities.size(), is(1));
            assertTrue(identities.contains("id"));
        }
    }

    @Test
    public void test() throws Exception {
        System.out.println(new ObjectMapper().writeValueAsString(new OutlierRemovalOptions(Collections.singletonList(new OutlierRemovalFilter(
                Collections.singletonList("id"),
                Arrays.asList(
                        new ThresholdCondition(AggregationType.SUM, Condition.GT, 100.0, "purchaseamount", false)
                )
        )), "outliers")));
    }
}