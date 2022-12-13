/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.kmeans;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.NumericalRange;
import com.ibm.research.drl.dpt.anonymization.hierarchies.GeneralizationHierarchyFactory;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KMeansTest {
    
    @Test
    public void testKMeans() throws Exception {
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/kmeans_simple.txt"), false, ',', '"', false);

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new NumericalRange(Collections.emptyList(), ColumnType.QUASI));
        columnInformation.add(new NumericalRange(Collections.emptyList(), ColumnType.QUASI));
        
        KMeans kMeans = new KMeans(dataset, 2, 1000, columnInformation, Arrays.asList(0, 1));
       
        List<KMeansCluster> partitions = kMeans.apply();
       
        assertTrue(partitions.size() > 0);
    
        int originalMembers = dataset.getNumberOfRows();
        
        int clusteredMembers = 0;
        
        for(KMeansCluster p: partitions) {
            clusteredMembers += p.getOriginalData().size();
        }
        
        assertEquals(clusteredMembers, originalMembers);
    }

    @Test
    public void testKMeansWithCategorical() throws Exception {
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/kmeans_simple_categorical.txt"), false, ',', '"', false);

        List<ColumnInformation> columnInformation = new ArrayList<>();
        columnInformation.add(new CategoricalInformation(
                GeneralizationHierarchyFactory.getGenericFromFixedSet(Arrays.asList("Male", "Female")), ColumnType.QUASI)); 
        columnInformation.add(new NumericalRange(Collections.emptyList(), ColumnType.QUASI));

        KMeans kMeans = new KMeans(dataset, 2, 1000, columnInformation, Arrays.asList(0, 1));

        List<KMeansCluster> partitions = kMeans.apply();

        assertTrue(partitions.size() > 0);

        int originalMembers = dataset.getNumberOfRows();

        int clusteredMembers = 0;

        for(KMeansCluster p: partitions) {
            clusteredMembers += p.getOriginalData().size();
        }

        assertEquals(clusteredMembers, originalMembers);
    }
}
