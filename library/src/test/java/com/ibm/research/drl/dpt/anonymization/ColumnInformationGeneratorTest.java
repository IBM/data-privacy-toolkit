/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
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
