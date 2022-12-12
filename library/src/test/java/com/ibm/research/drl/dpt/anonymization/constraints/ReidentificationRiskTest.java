/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.constraints;

import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.linkability.LinkInfo;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReidentificationRiskTest {
    
    @Test
    public void testRiskConstraintNoAnonymization() {

        InputStream population = this.getClass().getResourceAsStream("/testRiskConstraintData.csv");

        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, "*"));

        double riskThreshold = 0.4;
       
        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")), 
                ColumnType.QUASI, true));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());
        
        PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

        List<List<String>> anonValues = new ArrayList<>();
        anonValues.add(Arrays.asList("a", "2", "w"));
        anonValues.add(Arrays.asList("a", "2", "w"));
        anonValues.add(Arrays.asList("a", "2", "w"));
        
        Partition partition = new InMemoryPartition(anonValues);
        assertTrue(privacyConstraint.check(partition, null));
    }

    @Test
    public void testRiskConstraintNoAnonymizationNoMatch() throws Exception {

        InputStream population = this.getClass().getResourceAsStream("/testRiskConstraintData.csv");

        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, "*"));

        double riskThreshold = 0.2;

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

        List<List<String>> anonValues = new ArrayList<>();
        anonValues.add(Arrays.asList("a", "2", "w"));

        Partition partition = new InMemoryPartition(anonValues);
        assertFalse(privacyConstraint.check(partition, null));
    }

    @Test
    public void testRiskConstraintWithAnonymization() throws Exception {

        InputStream population = this.getClass().getResourceAsStream("/testRiskConstraintData.csv");

        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, "*"));

        double riskThreshold = 0.4;

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

        List<List<String>> anonValues = new ArrayList<>();
        anonValues.add(Arrays.asList("*", "2", "w"));

        Partition partition = new InMemoryPartition(anonValues);
        assertTrue(privacyConstraint.check(partition, null));
    }

    @Test
    public void testRiskConstraintNoMatches() throws Exception {

        InputStream population = this.getClass().getResourceAsStream("/testRiskConstraintData.csv");

        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, "*"));

        double riskThreshold = 0.4;

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("a", "b", "c")),
                ColumnType.QUASI));
        columnInformation.add(new DefaultColumnInformation());
        columnInformation.add(new DefaultColumnInformation());

        PrivacyConstraint privacyConstraint = new ReidentificationRisk(population, linkInformation, columnInformation, riskThreshold);

        List<List<String>> anonValues = new ArrayList<>();
        anonValues.add(Arrays.asList("b", "2", "w"));

        Partition partition = new InMemoryPartition(anonValues);
        assertTrue(privacyConstraint.check(partition, null));
    }
}

