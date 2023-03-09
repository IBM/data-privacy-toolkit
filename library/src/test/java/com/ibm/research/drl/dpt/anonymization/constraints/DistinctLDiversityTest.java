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

import com.ibm.research.drl.dpt.anonymization.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DistinctLDiversityTest {

    @Test
    public void testMetricCheck() {
        List<String> sensitiveValues = new ArrayList<>();
        sensitiveValues.add("a");

        LDiversityMetric metric = new LDiversityMetric(sensitiveValues);

        DistinctLDiversity distinctLDiversity = new DistinctLDiversity(2);

        assertFalse(distinctLDiversity.check(metric));

        metric.update(metric);
        assertFalse(distinctLDiversity.check(metric));

        List<String> sensitiveValues2 = new ArrayList<>();
        sensitiveValues2.add("b");
        LDiversityMetric other = new LDiversityMetric(sensitiveValues2);

        metric.update(other);
        assertTrue(distinctLDiversity.check(metric));
    }

    @Test
    public void testDistinctLDiversity() {
        List<List<String>> values = new ArrayList<>();

        List<String> row1 = new ArrayList<>();
        row1.add("test1");
        List<String> row2 = new ArrayList<>();
        row2.add("test2");

        values.add(row1);
        values.add(row2);

        InMemoryPartition partition = new InMemoryPartition(values);

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new SensitiveColumnInformation());

        List<Integer> sensitiveColumns = AnonymizationUtils.getColumnsByType(columnInformationList, ColumnType.SENSITIVE);

        DistinctLDiversity distinctLDiversity = new DistinctLDiversity(3);

        assertFalse(distinctLDiversity.check(partition, sensitiveColumns));

        //we add one more row but with the same value
        values.add(row2);
        assertFalse(distinctLDiversity.check(partition, sensitiveColumns));

        //we add one more row but with capitalized letters
        List<String> row4 = new ArrayList<>();
        row4.add("TEST2");
        values.add(row4);
        assertFalse(distinctLDiversity.check(partition, sensitiveColumns));

        //we add a new row
        List<String> row5 = new ArrayList<>();
        row5.add("TEST3");
        values.add(row5);
        assertTrue(distinctLDiversity.check(partition, sensitiveColumns));

    }
}

