/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TransactionUniquenessOptionsTest {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Test
    public void testValidConfiguration() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/validTUconf.json");
        JsonNode configuration = OBJECT_MAPPER.readTree(inputStream);
        
        TransactionUniquenessOptions options = new TransactionUniquenessOptions(configuration);

        assertNotNull(options);
    }

    @Test
    public void testValidConfigurationWithJoin() throws Exception {
        InputStream inputStream = this.getClass().getResourceAsStream("/validTUconfWithJoin.json");
        JsonNode configuration = OBJECT_MAPPER.readTree(inputStream);

        TransactionUniquenessOptions options = new TransactionUniquenessOptions(configuration);
    }
}