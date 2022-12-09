/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence.causal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class DBChainRetrievalTest {

    @Test
    @Disabled("Require mocked DB")
    public void testRetrieval() throws Exception {
        String host = "jdbc:postgresql://localhost/postgres";
        String username = "postgres";
        String password = "";

        DBChainRetrieval chainRetrieval = new DBChainRetrieval(host, username, password, "chain");

        System.out.println(chainRetrieval.retrieveChain().size());

        chainRetrieval.append("fooo");
    }

}
