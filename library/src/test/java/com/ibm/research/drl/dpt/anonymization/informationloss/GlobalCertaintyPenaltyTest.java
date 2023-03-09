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

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.anonymization.mondrian.Mondrian;
import com.ibm.research.drl.dpt.anonymization.mondrian.MondrianOptions;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalCertaintyPenaltyTest {
    private List<String> toString(Long[] svs) {
        List<String> values = new ArrayList<>();
        for(Long v: svs) {
            values.add(Long.toString(v));
        }

        return values;
    }

    @Test
    @Disabled
    public void testGCPNumeric() throws Exception {
        List<List<String>> values = new ArrayList<>();
        values.add(toString(new Long[]{1L, 0L, 2L, 4L}));
        values.add(toString(new Long[]{5L, 1L, 11L, 4L}));
        values.add(toString(new Long[]{6L, 2L, 12L, 4L}));
        values.add(toString(new Long[]{10L, 3L, 5L, 4L}));
        values.add(toString(new Long[]{10L, 4L, 11L, 4L}));

        IPVDataset dataset = new IPVDataset(values, null, false);

        List<ColumnInformation> columnInformationList = new ArrayList<>(4);
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 0, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(ColumnInformationGenerator.generateNumericalRange(dataset, 2, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());

        Mondrian mondrian = new Mondrian();

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        InformationMetric globalCertaintyPenalty = new GlobalCertaintyPenalty().initialize(dataset, anonymizedDataset,
                mondrian.getOriginalPartitions(), mondrian.getAnonymizedPartitions(), columnInformationList, null);

        //partition 1: NCP0 = 4/9, NCP1 = 9/10
        //partition 2: NCP0 = 4/9, NCP1 = 7/10
        //NCP(p1) = 1 * 4/9 + 1 * 9/10 = 4/9 + 9/10 = 1.34
        //NCP(p2) = 1 * 4/9 + 1 * 7/10 = 4/9 + 7/10 = 1.14
        //GCP = (2 * NCP(p1) + 3 * NCP(p2)) / 2 * 5 = (2* 1.34 + 3 * 1.14) / 10 = 6.10 / 10 = 0.61

        Double gcp = globalCertaintyPenalty.report();
//        System.out.println("GCP = " + gcp);

        double ncpP1 = 4.0/9.0 + 9.0/10.0;
        double ncpP2 = 4.0/9.0 + 7.0/10.0;
        assertEquals(gcp, ((2 * ncpP1 + 3 * ncpP2) / (2.0 * 5.0)));
    }

    @Test
    public void testCategoricalNoLoss() throws Exception {

        IPVDataset dataset = IPVDataset.load(getClass().getResourceAsStream("/testCategoricalOriginal2anon.csv"), false, ',', '"', false);


        List<ColumnInformation> columnInformationList = new ArrayList<ColumnInformation>();

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "Worker", "*");
        hierarchy.add("Manager", "Worker", "*");
        hierarchy.add("Director", "Worker", "*");

        CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
        columnInformationList.add(categoricalInformation);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        InformationMetric globalCertaintyPenalty = new GlobalCertaintyPenalty().initialize(dataset, anonymizedDataset,
                mondrian.getOriginalPartitions(), mondrian.getAnonymizedPartitions(), columnInformationList, null);


        double gcp = globalCertaintyPenalty.report();


        assertEquals(0.0, gcp);
    }

    @Test
    public void testCategoricalWithLoss() throws Exception {

        IPVDataset dataset = IPVDataset.load(getClass().getResourceAsStream("/testCategoricalOriginalunique.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<>();

        MaterializedHierarchy hierarchy = new MaterializedHierarchy();
        hierarchy.add("Scientist", "*");
        hierarchy.add("Manager", "*");
        hierarchy.add("Director", "*");

        CategoricalInformation categoricalInformation = new CategoricalInformation(hierarchy, ColumnType.QUASI);
        columnInformationList.add(categoricalInformation);

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(2));

        Mondrian mondrian = new Mondrian();
        mondrian.initialize(dataset, columnInformationList, privacyConstraints, new MondrianOptions()); //dataset, 2, 1, columnInformationList);

        IPVDataset anonymizedDataset = mondrian.apply();

        InformationMetric globalCertaintyPenalty = new GlobalCertaintyPenalty().initialize(dataset, anonymizedDataset,
                mondrian.getOriginalPartitions(), mondrian.getAnonymizedPartitions(), columnInformationList, null);

        double gcp = globalCertaintyPenalty.report();

        assertEquals(1.0, gcp);
    }
}
