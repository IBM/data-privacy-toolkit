/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        int K = 2;
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
//        System.out.println("AECS = " + aecsValue);

        double expected = 5.0 / 2.0;
        assertEquals(aecsValue, expected, Double.MIN_VALUE);

        //check normalized
        aecs = new AverageEquivalenceClassSize().initialize(dataset, anonymizedDataset,
                null, partitions, columnInformationList, new AECSOptions(true, 2));

        aecsValue = aecs.report();
//        System.out.println("AECS normalized = " + aecsValue);

        expected = (5.0 / 2.0) / 2.0;
        assertThat(aecsValue, is(expected));
    }

}
