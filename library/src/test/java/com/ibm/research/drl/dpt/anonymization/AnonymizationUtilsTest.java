/******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization;

import com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes.GenderHierarchy;
import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnonymizationUtilsTest {
    @Test
    public void generationOfPartitionsForLinkingWithNoColumnInformationThrows() {
        assertThrows(RuntimeException.class, () -> {
            AnonymizationUtils.generatePartitionsForLinking(new IPVDataset(Collections.emptyList(), null, false), Collections.emptyList());
        });
    }

    @Test
    public void generationOfPartitionsForLinkingWithLinkColumnInformationThrows() {
        assertThrows(RuntimeException.class, () -> {
            List<ColumnInformation> columnInformation = new ArrayList<>();

            for (int i = 0; i < 10; ++i) {
                columnInformation.add(new CategoricalInformation(GenderHierarchy.getInstance(), ColumnType.QUASI));
            }
    
            AnonymizationUtils.generatePartitionsForLinking(new IPVDataset(Collections.emptyList(), null, false), columnInformation);
        });
    }
}