/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
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

