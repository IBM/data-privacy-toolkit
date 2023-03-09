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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class NumericRangeTest {

    @Test
    public void testNumericalRange() {
        List<Double> sortedValues = new ArrayList<>();

        sortedValues.add(5.0);
        sortedValues.add(10.0);
        sortedValues.add(10.0);
        sortedValues.add(11.0);

        NumericalRange numericalRange = new NumericalRange(sortedValues, ColumnType.NORMAL);

        assertFalse(numericalRange.isCategorical());
        assertEquals(4, numericalRange.getNumberOfValues());
        assertEquals(6L, numericalRange.getRange().longValue());

        String representation = String.format("[%f-%f]", numericalRange.getLow(), numericalRange.getHigh());
        assertEquals(representation, numericalRange.getRepresentation());
        assertEquals(0, numericalRange.getPosition(5.0));
        assertEquals(2, numericalRange.getPosition(10.0));
    }
}

