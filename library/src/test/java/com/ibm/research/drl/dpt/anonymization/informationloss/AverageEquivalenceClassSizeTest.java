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
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformationGenerator;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.anonymization.InMemoryPartition;
import com.ibm.research.drl.dpt.anonymization.Partition;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class AverageEquivalenceClassSizeTest {
    private List<String> toString(Long[] svs) {
        List<String> values = new ArrayList<>();
        for(Long v: svs) {
            values.add(Long.toString(v));
        }

        return values;
    }

    @Test
    public void testAECSNumeric() {
        List<List<String>> values = new ArrayList<>();
        values.add(toString(new Long[]{1L, 7L, 2L, 4L}));
        values.add(toString(new Long[]{6L, 7L, 12L, 4L}));
        values.add(toString(new Long[]{5L, 7L, 11L, 4L}));
        values.add(toString(new Long[]{10L, 7L, 5L, 4L}));
        values.add(toString(new Long[]{10L, 7L, 11L, 4L}));

        IPVDataset dataset = new IPVDataset(values, null, false);

        List<ColumnInformation> columnInformationList = new ArrayList<>(4);
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 2, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());

        List<List<String>> anonValues = new ArrayList<>();
        List<String> row1 = Arrays.asList("1.0-5.0","7","2.0-11.0","4");
        List<String> row2 = Arrays.asList("1.0-5.0","7","2.0-11.0","4");
        anonValues.add(row1);
        anonValues.add(row2);

        List<String> row3 = Arrays.asList("6.0-10.0","7","5.0-12.0","4");
        List<String> row4 = Arrays.asList("6.0-10.0","7","5.0-12.0","4");
        List<String> row5 = Arrays.asList("6.0-10.0","7","5.0-12.0","4");
        anonValues.add(row3);
        anonValues.add(row4);
        anonValues.add(row5);

        IPVDataset anonymizedDataset = new IPVDataset(anonValues, null, false);

        List<Partition> partitions = new ArrayList<>();
        List<List<String>> p1values = new ArrayList<>();
        p1values.add(row1);
        p1values.add(row2);
        InMemoryPartition p1 = new InMemoryPartition(p1values);

        List<List<String>> p2values = new ArrayList<>();
        p2values.add(row3);
        p2values.add(row4);
        p2values.add(row5);
        InMemoryPartition p2 = new InMemoryPartition(p2values);

        partitions.add(p1);
        partitions.add(p2);

        InformationMetric aecs = new AverageEquivalenceClassSize().initialize(dataset, anonymizedDataset,
                null, partitions, columnInformationList, new AECSOptions(false, 2));

        double aecsValue = aecs.report();

        double expected = 5.0 / 2.0;
        assertEquals(aecsValue, expected, Double.MIN_VALUE);

        //check normalized
        aecs = new AverageEquivalenceClassSize().initialize(dataset, anonymizedDataset,
                null, partitions, columnInformationList, new AECSOptions(true, 2));

        aecsValue = aecs.report();

        expected = (5.0 / 2.0) / 2.0;
        assertThat(aecsValue, is(expected));
    }

}
