/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.ola;

import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

public class IntervalGeneratorTest {

    @Test
    public void testGenerator() throws Exception {

        IPVDataset original = IPVDataset.load(getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        MaterializedHierarchy hierarchy = IntervalGenerator.generateHierarchy(original, 0);
    }
}

