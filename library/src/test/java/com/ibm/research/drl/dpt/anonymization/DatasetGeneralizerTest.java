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

import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DatasetGeneralizerTest {

    @Test
    public void testMaintainsOrder() throws Exception {
        // 0:0:1:2:2

        IPVDataset originalDataset = IPVDataset.load(DatasetGeneralizerTest.class.getResourceAsStream("/random1_height_weight_with_index.txt"), false, ',', '"', false);

        GeneralizationHierarchy heightHierarchy = GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.HEIGHT);
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new DefaultColumnInformation()); //index, 0
        columnInformation.add(new DefaultColumnInformation()); // 1
        columnInformation.add(new DefaultColumnInformation()); // 2
        columnInformation.add(new DefaultColumnInformation()); // 3
        columnInformation.add(new DefaultColumnInformation()); // 4
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.YOB), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); //zipcode, 6
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.GENDER), ColumnType.QUASI));
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.RACE), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation()); // 9
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getDefaultHierarchy(ProviderType.MARITAL_STATUS), ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new CategoricalInformation(heightHierarchy, ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());

        int raceIndex = 8;

        IPVDataset anonymizedDataset = DatasetGeneralizer.generalize(originalDataset, columnInformation, new int[]{0, 0, 1, 2, 2});

        assertEquals(anonymizedDataset.getNumberOfRows(), originalDataset.getNumberOfRows());

        for (int i = 0; i < anonymizedDataset.getNumberOfRows(); i++) {
            String originalIndex = originalDataset.get(i, 0);
            String anonIndex = anonymizedDataset.get(i, 0);

            assertEquals(originalIndex, anonIndex);

            String originalRace = originalDataset.get(i, raceIndex);
            String anonymizedRace = anonymizedDataset.get(i, raceIndex);

            assertNotEquals(originalRace, anonymizedRace);
        }
    }

    @Test
    public void testShouldPropagateHeaders() throws Exception {
        IPVDataset originalDataset;
        try (InputStream inputStream = this.getClass().getResourceAsStream("/olaAges.csv")) {
            originalDataset = IPVDataset.load(inputStream, true, ',', '"', false);
        }

        String originalHeaders = originalDataset.getSchema().getFields().stream().map(IPVSchemaField::getName).collect(Collectors.joining(","));

        IPVDataset anonymizedDataset = DatasetGeneralizer.generalize(originalDataset, Collections.singletonList(
                new CategoricalInformation(null, ColumnType.QUASI)
        ), new int[]{0});
        String anonymizedHeaders = anonymizedDataset.getSchema().getFields().stream().map(IPVSchemaField::getName).collect(Collectors.joining(","));

        assertThat(anonymizedDataset.hasColumnNames(), is(originalDataset.hasColumnNames()));
        assertThat(anonymizedDataset.getNumberOfColumns(), is(originalDataset.getNumberOfColumns()));
        assertThat(anonymizedDataset.getNumberOfRows(), is(originalDataset.getNumberOfRows()));

        assertThat(anonymizedHeaders, is(originalHeaders));
    }
}
