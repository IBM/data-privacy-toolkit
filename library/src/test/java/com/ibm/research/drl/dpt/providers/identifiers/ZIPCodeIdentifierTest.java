/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import com.ibm.research.drl.schema.IPVSchemaField;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZIPCodeIdentifierTest {

    @Test
    public void testZIPCodeIdentifier() {
        Identifier identifier = new ZIPCodeIdentifier();

        assertTrue(identifier.isOfThisType("01950"));
        assertFalse(identifier.isOfThisType("001950"));
        assertFalse(identifier.isOfThisType("0195000"));
        assertFalse(identifier.isOfThisType("000"));
    }

    @Test
    public void testAgainstKnownDataset() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/healthcare-dataset.csv")) {
            IPVDataset dataset = IPVDataset.load(inputStream, true, ',', '"', false);

            Identifier identifier = new ZIPCodeIdentifier();
            int idx = dataset.getSchema().getFields().stream().map(IPVSchemaField::getName).collect(Collectors.toList()).indexOf("ZIP");

            for (List<String> row: dataset) {
                if (!identifier.isOfThisType(row.get(idx))) {
                    System.out.println(row.get(idx));
                }
            }
        }
    }
}
