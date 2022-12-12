/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
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

