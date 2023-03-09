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
