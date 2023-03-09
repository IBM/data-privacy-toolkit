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

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnInformationGeneratorTest {

    private List<String> toString(Double[] svs) {
        List<String> values = new ArrayList<>();
        for(Double v: svs) {
            values.add(Double.toString(v));
        }

        return values;
    }

    @Test
    public void testGenerator() {
        List<List<String>> values = new ArrayList<>();

        values.add(toString(new Double[] {1.0}));
        values.add(toString(new Double[] {10.0}));
        values.add(toString(new Double[] {11.0}));
        values.add(toString(new Double[] {21.0}));
        values.add(toString(new Double[] {5.0}));

        IPVDataset IPVDataset = new IPVDataset(values, null, false);

        NumericalRange numericalRange = ColumnInformationGenerator.generateNumericalRange(IPVDataset, 0, ColumnType.NORMAL);
        assertEquals(1.0, numericalRange.getLow(), 0.0);
        assertEquals(21.0, numericalRange.getHigh(), 0.0);
    }
}
