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

import com.ibm.research.drl.dpt.configuration.MaskingConfiguration;
import com.ibm.research.drl.dpt.providers.masking.HashMaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.MaskingProvider;
import com.ibm.research.drl.dpt.providers.masking.persistence.AbstractPersistentMaskingProvider;

import java.io.IOException;
import java.util.*;

public class CausalOrderingConsistentMaskingProvider extends AbstractPersistentMaskingProvider {

    private final static HashMaskingProvider HASH_MASKING_PROVIDER = new HashMaskingProvider();
    private final Map<String, List<DictionaryEntry>> cache;
    private final ChainRetrieval chainRetrieval;

    public static String privateHash(String term) {
        return HASH_MASKING_PROVIDER.mask(term);
    }

    public static List<DictionaryEntry> reconstructDictionary(ChainRetrieval chainRetrieval, String term) {
        List<DictionaryEntry> entries = new ArrayList<>();
        Set<String> entriesToRemove = new HashSet<>();

        String hashedTerm = privateHash(term);

        List<DictionaryEntry> chainEntries;
        try {
            chainEntries = chainRetrieval.retrieveChain();
        } catch (IOException e) {
            throw new RuntimeException("unable to retrieve entries from chain");
        }

        boolean valueFound = false;

        for (DictionaryEntry entry : chainEntries) {
            if (entry.getType() == DictionaryEntryType.VALUE && entry.getValue().equals(hashedTerm)) {
                valueFound = true;
                break;
            }

            if (entry.getType() == DictionaryEntryType.INSERT) {
                entries.add(entry);
            } else if (entry.getType() == DictionaryEntryType.DELETE) {
                entriesToRemove.add(entry.getValue());
            }
        }


        if (!valueFound) {
            try {
                chainRetrieval.append(hashedTerm);
            } catch (Exception e) {
                throw new RuntimeException("unable to append entry to chain");
            }
        }

        if (!entriesToRemove.isEmpty()) {
            int totalEntries = entries.size();

            for (int i = (totalEntries - 1); i >= 0; i--) {
                DictionaryEntry entry = entries.get(i);
                if (entry.getType() != DictionaryEntryType.INSERT) {
                    continue;
                }

                if (entriesToRemove.contains(entry.getValue())) {
                    entries.remove(i);
                }
            }
        }

        return entries;

    }

    public CausalOrderingConsistentMaskingProvider(MaskingProvider maskingProvider, MaskingConfiguration configuration) {
        super(maskingProvider, configuration);
        this.cache = new HashMap<>();

        String causalBackend = configuration.getStringValue("persistence.causal.backend");

        if (causalBackend.equals("ethereum")) {
            throw new RuntimeException("Blockchain backend not supported yet");
        } else {
            String connectionString = configuration.getStringValue("persistence.database.connectionString");
            String username = configuration.getStringValue("persistence.database.username");
            String password = configuration.getStringValue("persistence.database.password");
            String namespace = configuration.getStringValue("persistence.namespace");

            this.chainRetrieval = new DBChainRetrieval(connectionString, username, password, namespace);
        }
    }

   /* @Override
    public String mask(String identifier) {
        List<DictionaryEntry> dictionaryEntries = this.cache.getOrDefault(identifier, reconstructDictionary(this.chainRetrieval, identifier));

        int idx = (int)Math.abs(HashUtils.longFromHash(privateHash(identifier))) % identifier.length();

        return dictionaryEntries.get(idx).getValue();
    }*/

    @Override
    protected boolean isCached(String value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected String getCachedValue(String value) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected void cacheValue(String value, String maskedValue) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

