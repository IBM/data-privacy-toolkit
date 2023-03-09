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
package com.ibm.research.drl.dpt.anonymization.constraints;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KAnonymityMetricTest {

    @Test
    public void testMetric() {
        KAnonymityMetric metric1 = new KAnonymityMetric();
        assertEquals(0L, metric1.getCount());

        metric1.update(new KAnonymityMetric());
        assertEquals(0L, metric1.getCount());

        metric1.update(new KAnonymityMetric(1));
        assertEquals(1L, metric1.getCount());
    }
}

