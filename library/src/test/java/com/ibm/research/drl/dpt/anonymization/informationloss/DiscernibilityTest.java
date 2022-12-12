/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.informationloss;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.ola.OLA;
import com.ibm.research.drl.dpt.anonymization.ola.OLAOptions;
import com.ibm.research.drl.dpt.anonymization.ola.OLAUtils;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiscernibilityTest {
    private List<String> toString(Long[] svs) {
        List<String> values = new ArrayList<>();
        for(Long v: svs) {
            values.add(Long.toString(v));
        }

        return values;
    }

    @Test
    public void testDiscernibilityNumericNoSuppression() throws Exception {
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

        IPVDataset anonymizedDataset = IPVDataset.load(this.getClass().getResourceAsStream("/discernibilityAnonymized.csv"), false, ',', '"', false);

        Tuple<List<Partition>, List<Partition>> bothPartitions = OLAUtils.generatePartitions(dataset, anonymizedDataset, columnInformationList);
        List<Partition> originalPartitions = bothPartitions.getFirst();
        List<Partition> anonymizedPartitions = bothPartitions.getSecond();

        for(Partition p: originalPartitions) p.setAnonymous(true);
        for(Partition p: anonymizedPartitions) p.setAnonymous(true);
        
        InformationMetric discernibility = new Discernibility().initialize(dataset, anonymizedDataset,
                originalPartitions, anonymizedPartitions, columnInformationList, null);

        double discernibilityValue = discernibility.report();

        double expected = 2*2 + 3*3;
        assertEquals(expected, discernibilityValue, 0.001);
    }

    @Test
    public void testDiscernibilityWithSuppression() throws Exception {
        int  k = 3;
        MaterializedHierarchy dateHierarchy = new MaterializedHierarchy();
        dateHierarchy.add("01/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("02/01/2008", "Jan_2008", "2008");
        dateHierarchy.add("03/01/2008", "Jan_2008", "2008");

        MaterializedHierarchy genderHierarchy = new MaterializedHierarchy();
        genderHierarchy.add("M", "Person");
        genderHierarchy.add("F", "Person");

        MaterializedHierarchy ageHierarchy = new MaterializedHierarchy();
        ageHierarchy.add("13", "10-14", "10-19", "0-49", "0-99");
        ageHierarchy.add("18", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("19", "15-19", "10-19", "0-49", "0-99");
        ageHierarchy.add("21", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("22", "20-24", "20-29", "0-49", "0-99");
        ageHierarchy.add("23", "20-24", "20-29", "0-49", "0-99");

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(dateHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(genderHierarchy, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(ageHierarchy, ColumnType.QUASI));

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/testOLA.csv"), false, ',', '"', false);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(k));

        OLAOptions olaOptions = new OLAOptions(20.0d);

        OLA ola = new OLA();
        ola.initialize(original, columnInformationList, privacyConstraints, olaOptions);

        IPVDataset anonymized = ola.apply();

        InformationMetric discernibility = new Discernibility().initialize(original, anonymized,
                ola.getOriginalPartitions(), ola.getAnonymizedPartitions(), columnInformationList, null);

        Double discernibilityValue = discernibility.report();

        // the anonymized dataset contains two clusters: one with 3 rows and another with 5 rows
        // and also two suppressed partitions, each one with one row

        double expected = 3.0*3.0 + 5.0*5.0 + 10.0*1.0*1.0 + 10.0*1.0*1.0;
        assertTrue(discernibilityValue == expected);
    }
}

