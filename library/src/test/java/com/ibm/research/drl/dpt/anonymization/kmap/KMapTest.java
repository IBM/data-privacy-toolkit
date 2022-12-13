/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmap;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.PrivacyConstraint;
import com.ibm.research.drl.dpt.anonymization.constraints.ReidentificationRisk;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchy;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.dpt.linkability.LinkInfo;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KMapTest {
    
    @Test
    public void testBasic() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/kmap_test_input.csv");
        IPVDataset dataset = IPVDataset.load(inputStream, false, ',', '"', false);

        InputStream populationDataset = this.getClass().getResourceAsStream("/kmap_test_population.csv");
        
        GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("22", "25", "27", "28", "29"));
        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));
        
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0));
        
        double riskThreshold = 0.25;
        
        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        PrivacyConstraint rr = new ReidentificationRisk(populationDataset, linkInformation, columnInformationList, riskThreshold);
        privacyConstraints.add(rr);
        
        KMap kMap = new KMap();
        kMap.initialize(dataset, columnInformationList, privacyConstraints, new KMapOptions(0.0));
        
        IPVDataset anonymized = kMap.apply();
        //IPVDataset.printDataset(anonymized);
        
        assertEquals(7, anonymized.getNumberOfRows());
        assertEquals(0.0, kMap.reportSuppressionRate(), 0.0001);
    }

    @Test
    public void testBasicWithSuppression() throws Exception {

        InputStream inputStream = this.getClass().getResourceAsStream("/kmap_test_input.csv");
        IPVDataset dataset = IPVDataset.load(inputStream, false, ',', '"', false);

        InputStream populationDataset = this.getClass().getResourceAsStream("/kmap_test_population.csv");

        GeneralizationHierarchy hierarchy = GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("22", "25", "27", "28", "29"));
        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(hierarchy, ColumnType.QUASI));

        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0));

        double riskThreshold = 0.25;

        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        PrivacyConstraint rr = new ReidentificationRisk(populationDataset, linkInformation, columnInformationList, riskThreshold);
        privacyConstraints.add(rr);

        KMap kMap = new KMap();
        kMap.initialize(dataset, columnInformationList, privacyConstraints, new KMapOptions(30.0));

        IPVDataset anonymized = kMap.apply();
        //IPVDataset.printDataset(anonymized);

        assertEquals(5, anonymized.getNumberOfRows());
        assertEquals(200.0 / 7.0, kMap.reportSuppressionRate(), 0.0001);
    }
}

