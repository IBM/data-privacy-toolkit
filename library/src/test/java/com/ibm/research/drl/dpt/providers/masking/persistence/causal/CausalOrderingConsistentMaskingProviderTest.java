/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.masking.persistence.causal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CausalOrderingConsistentMaskingProviderTest {

    @Test
    public void testReconstructsDictionaryNoValuesInserted() {
        ChainRetrieval chainRetrieval = new ChainRetrieval() {
            @Override
            public List<DictionaryEntry> retrieveChain() {
                return Arrays.asList(
                        new DictionaryEntry("A", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.INSERT),
                        new DictionaryEntry("C", DictionaryEntryType.INSERT),
                        new DictionaryEntry("D", DictionaryEntryType.INSERT)
                        );
            }

            @Override
            public void append(String hashedTerm) throws Exception {

            }

            @Override
            public void shutDown() {

            }
        };

        List<DictionaryEntry> dictionaryEntries =
                CausalOrderingConsistentMaskingProvider.reconstructDictionary(chainRetrieval, "foo");

        assertEquals(4, dictionaryEntries.size());
        assertEquals("A", dictionaryEntries.get(0).getValue());
        assertEquals("B", dictionaryEntries.get(1).getValue());
        assertEquals("C", dictionaryEntries.get(2).getValue());
        assertEquals("D", dictionaryEntries.get(3).getValue());
    }

    @Test
    public void testReconstructsDictionaryNoValuesInsertedWithDeletion() {
        ChainRetrieval chainRetrieval = new ChainRetrieval() {
            @Override
            public List<DictionaryEntry> retrieveChain() {
                return Arrays.asList(
                        new DictionaryEntry("A", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.INSERT),
                        new DictionaryEntry("C", DictionaryEntryType.INSERT),
                        new DictionaryEntry("D", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.DELETE)
                );
            }

            @Override
            public void append(String hashedTerm) throws Exception {

            }

            @Override
            public void shutDown() {

            }
        };

        List<DictionaryEntry> dictionaryEntries =
                CausalOrderingConsistentMaskingProvider.reconstructDictionary(chainRetrieval, "foo");

        assertEquals(3, dictionaryEntries.size());
        assertEquals("A", dictionaryEntries.get(0).getValue());
        assertEquals("C", dictionaryEntries.get(1).getValue());
        assertEquals("D", dictionaryEntries.get(2).getValue());
    }

    @Test
    public void testReconstructsDictionaryWithValueInserted() {
        String term = "foo";

        ChainRetrieval chainRetrieval = new ChainRetrieval() {
            @Override
            public List<DictionaryEntry> retrieveChain() {
                return Arrays.asList(
                        new DictionaryEntry("A", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.INSERT),
                        new DictionaryEntry(CausalOrderingConsistentMaskingProvider.privateHash(term), DictionaryEntryType.VALUE),
                        new DictionaryEntry("C", DictionaryEntryType.INSERT),
                        new DictionaryEntry("D", DictionaryEntryType.INSERT)
                );
            }

            @Override
            public void append(String hashedTerm) throws Exception {

            }

            @Override
            public void shutDown() {

            }
        };

        List<DictionaryEntry> dictionaryEntries =
                CausalOrderingConsistentMaskingProvider.reconstructDictionary(chainRetrieval, term);

        assertEquals(2, dictionaryEntries.size());
        assertEquals("A", dictionaryEntries.get(0).getValue());
        assertEquals("B", dictionaryEntries.get(1).getValue());
    }

    @Test
    public void testReconstructsDictionaryWithValueInsertedWithDelete() {
        String term = "foo";

        ChainRetrieval chainRetrieval = new ChainRetrieval() {
            @Override
            public List<DictionaryEntry> retrieveChain() {
                return Arrays.asList(
                        new DictionaryEntry("A", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.DELETE),
                        new DictionaryEntry(CausalOrderingConsistentMaskingProvider.privateHash(term), DictionaryEntryType.VALUE),
                        new DictionaryEntry("C", DictionaryEntryType.INSERT),
                        new DictionaryEntry("D", DictionaryEntryType.INSERT)
                );
            }

            @Override
            public void append(String hashedTerm) throws Exception {

            }

            @Override
            public void shutDown() {

            }
        };

        List<DictionaryEntry> dictionaryEntries =
                CausalOrderingConsistentMaskingProvider.reconstructDictionary(chainRetrieval, term);

        assertEquals(1, dictionaryEntries.size());
        assertEquals("A", dictionaryEntries.get(0).getValue());
    }

    @Test
    public void testReconstructsDictionaryWithValueInsertedWithDeleteTwoTerms() {
        String term = "foo";
        String term2 = "foo2";

        ChainRetrieval chainRetrieval = new ChainRetrieval() {
            @Override
            public List<DictionaryEntry> retrieveChain() {
                return Arrays.asList(
                        new DictionaryEntry("A", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.INSERT),
                        new DictionaryEntry("B", DictionaryEntryType.DELETE),
                        new DictionaryEntry(CausalOrderingConsistentMaskingProvider.privateHash(term), DictionaryEntryType.VALUE),
                        new DictionaryEntry("C", DictionaryEntryType.INSERT),
                        new DictionaryEntry("D", DictionaryEntryType.INSERT),
                        new DictionaryEntry(CausalOrderingConsistentMaskingProvider.privateHash(term2), DictionaryEntryType.VALUE)
                );
            }

            @Override
            public void append(String hashedTerm) throws Exception {

            }

            @Override
            public void shutDown() {

            }
        };

        List<DictionaryEntry> dictionaryEntries =
                CausalOrderingConsistentMaskingProvider.reconstructDictionary(chainRetrieval, term);

        assertEquals(1, dictionaryEntries.size());
        assertEquals("A", dictionaryEntries.get(0).getValue());

        dictionaryEntries =
                CausalOrderingConsistentMaskingProvider.reconstructDictionary(chainRetrieval, term2);

        assertEquals(3, dictionaryEntries.size());
        assertEquals("A", dictionaryEntries.get(0).getValue());
        assertEquals("C", dictionaryEntries.get(1).getValue());
        assertEquals("D", dictionaryEntries.get(2).getValue());
    }
}
