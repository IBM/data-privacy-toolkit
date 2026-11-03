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
package com.ibm.research.drl.dpt.providers.masking.persistence.causal;

import java.io.IOException;
import java.util.List;

/**
 * Interface for retrieving and appending to a dictionary chain for causal consistency masking.
 */
public interface ChainRetrieval {
    /**
     * Retrieves all dictionary entries from the chain.
     *
     * @return list of dictionary entries
     * @throws IOException if the chain cannot be read
     */
    List<DictionaryEntry> retrieveChain() throws IOException;

    /**
     * Appends a hashed term to the chain.
     *
     * @param hashedTerm the hashed term to append
     * @throws Exception if the append operation fails
     */
    void append(String hashedTerm) throws Exception;

    /**
     * Shuts down the chain retrieval connection.
     */
    void shutDown();
}

