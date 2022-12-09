/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence.causal;

import java.io.IOException;
import java.util.List;

public interface ChainRetrieval {
   List<DictionaryEntry> retrieveChain() throws IOException;
   void append(String hashedTerm) throws Exception;
   void shutDown();
}

