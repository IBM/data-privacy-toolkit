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
package com.ibm.research.drl.dpt.toolkit.dataset;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JSONIPVDatasetTest {
    @Test
    public void loadArrayOfObjects() throws Exception {
        try (
                InputStream inputStream = JSONIPVDatasetTest.class.getResourceAsStream("/json-dataset-array.json");
                Reader reader = new InputStreamReader(inputStream)) {
            IPVDataset dataset = JSONIPVDataset.load(reader);

            assertThat(dataset.getNumberOfColumns(), is(2));
            assertThat(dataset.getNumberOfRows(), is(3));
        }
    }

    @Test
    public void loadSequenceOfObjects() throws Exception {
        try (
                InputStream inputStream = JSONIPVDatasetTest.class.getResourceAsStream("/json-dataset-objects.json");
                Reader reader = new InputStreamReader(inputStream)) {
            IPVDataset dataset = JSONIPVDataset.load(reader);

            assertThat(dataset.getNumberOfColumns(), is(2));
            assertThat(dataset.getNumberOfRows(), is(3));
        }
    }

    @Test
    public void loadObjectWithMissingFields() throws Exception {
        try (
                InputStream inputStream = JSONIPVDatasetTest.class.getResourceAsStream("/json-dataset-missing-fields.json");
                Reader reader = new InputStreamReader(inputStream)) {
            IPVDataset dataset = JSONIPVDataset.load(reader);

            assertThat(dataset.getNumberOfColumns(), is(4));
            assertThat(dataset.getNumberOfRows(), is(3));
        }
    }
}