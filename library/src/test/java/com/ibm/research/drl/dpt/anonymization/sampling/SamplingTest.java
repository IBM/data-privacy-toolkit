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
package com.ibm.research.drl.dpt.anonymization.sampling;

import com.ibm.research.drl.dpt.anonymization.CategoricalInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnInformation;
import com.ibm.research.drl.dpt.anonymization.ColumnType;
import com.ibm.research.drl.dpt.anonymization.DefaultColumnInformation;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SamplingTest {
    
    @Test
    public void testSampling() throws Exception {
        IPVDataset original = IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        List<ColumnInformation> columnInformationList = new ArrayList<>();
        columnInformationList.add(new CategoricalInformation(null, ColumnType.QUASI));
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());
        columnInformationList.add(new DefaultColumnInformation());

        Sampling sampling = new Sampling();
        sampling.initialize(original, columnInformationList, null, new SamplingOptions(0.2));

        IPVDataset anonymized = sampling.apply();
        assertEquals(20, anonymized.getNumberOfRows());
        
        assertEquals(2, sampling.getOriginalPartitions().size());
        assertEquals(2, sampling.getAnonymizedPartitions().size());
       
        assertNotEquals(sampling.getOriginalPartitions().get(0).isAnonymous(), sampling.getOriginalPartitions().get(1).isAnonymous());
        
        for(int i = 0; i < 2; i++) {
            assertEquals(sampling.getOriginalPartitions().get(i).size(), sampling.getAnonymizedPartitions().get(i).size());
            assertEquals(sampling.getOriginalPartitions().get(i).isAnonymous(), sampling.getAnonymizedPartitions().get(i).isAnonymous());
        }
    }
}

