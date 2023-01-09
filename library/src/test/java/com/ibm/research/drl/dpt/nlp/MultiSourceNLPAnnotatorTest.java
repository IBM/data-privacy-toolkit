/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;


public class MultiSourceNLPAnnotatorTest {

    MultiSourceNLPAnnotator identifier;

    @BeforeEach
    public void setUp() {
        identifier = new MultiSourceNLPAnnotator() {
            @Override
            public List<IdentifiedEntity> identify(String text, Language language) throws IOException {
                throw new RuntimeException("Not supported");
            }

            @Override
            public String getName() {
                throw new RuntimeException("Not supported");
            }
        };
    }

    @Test
    public void testMergeDoesNotMergeDistinct() throws Exception {
        List<IdentifiedEntity> merged = identifier.mergeEntityListsAndOverlappingEntities(
                List.of(new IdentifiedEntity("foo", 20, 30, Collections.singleton(new IdentifiedEntityType("A", "A", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.UNKNOWN))),
                List.of(new IdentifiedEntity("foo", 0, 10, Collections.singleton(new IdentifiedEntityType("B", "B", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.UNKNOWN))));

        assertNotNull(merged);

        assertThat(merged.size(), is(2));
    }

    @Test
    public void testMergeSamePosition() throws Exception {
        List<IdentifiedEntity> merged = identifier.mergeEntityListsAndOverlappingEntities(
                List.of(new IdentifiedEntity("fooba", 5, 10, Collections.singleton(new IdentifiedEntityType("A", "A", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.UNKNOWN))),
                List.of(new IdentifiedEntity("fooba", 5, 10, Collections.singleton(new IdentifiedEntityType("B", "B", IdentifiedEntityType.UNKNOWN_SOURCE)), Collections.singleton(PartOfSpeechType.UNKNOWN))));

        assertNotNull(merged);

        assertThat(merged.size(), is(1));

        Set<IdentifiedEntityType> mergedTypes = merged.get(0).getType();

        assertThat(mergedTypes.size(), is(2));
        assertTrue(mergedTypes.contains(new IdentifiedEntityType("A", "A", IdentifiedEntityType.UNKNOWN_SOURCE)));
        assertTrue(mergedTypes.contains(new IdentifiedEntityType("B", "B", IdentifiedEntityType.UNKNOWN_SOURCE)));
    }

    @Test
    public void testOverlap() throws Exception {
        List<IdentifiedEntity> merged = identifier.mergeEntityListsAndOverlappingEntities(
                List.of(new IdentifiedEntity("stomach ulcer", 146, 159, Collections.singleton(new IdentifiedEntityType("SYMPTOM", "SYMPTOM", IdentifiedEntityType.UNKNOWN_SOURCE)),
                        Collections.singleton(PartOfSpeechType.UNKNOWN))),
                List.of(new IdentifiedEntity("ulcer", 154, 159, Collections.singleton(new IdentifiedEntityType("SYMPTOM", "SYMPTOM", IdentifiedEntityType.UNKNOWN_SOURCE)),
                        Collections.singleton(PartOfSpeechType.UNKNOWN))));

        assertNotNull(merged);
        assertThat(merged.size(), is(1));
    }

    @Test
    public void testOverlapInverse() throws Exception {
        List<IdentifiedEntity> merged = identifier.mergeEntityListsAndOverlappingEntities(
                List.of(new IdentifiedEntity("ulcer", 154, 159, Collections.singleton(new IdentifiedEntityType("SYMPTOM", "SYMPTOM", IdentifiedEntityType.UNKNOWN_SOURCE)),
                        Collections.singleton(PartOfSpeechType.UNKNOWN))),
                List.of(new IdentifiedEntity("stomach ulcer", 146, 159, Collections.singleton(new IdentifiedEntityType("SYMPTOM", "SYMPTOM", IdentifiedEntityType.UNKNOWN_SOURCE)),
                        Collections.singleton(PartOfSpeechType.UNKNOWN))));

        assertNotNull(merged);
        assertThat(merged.size(), is(1));
    }

    @Test
    public void testMergesInclusion() throws Exception {
        List<IdentifiedEntity> merged = identifier.mergeEntityListsAndOverlappingEntities(
                List.of(new IdentifiedEntity("Health Center of Washington.", 11, 39,
                        Collections.singleton(new IdentifiedEntityType("ORGANIZATION", "ORGANIZATION", IdentifiedEntityType.UNKNOWN_SOURCE)), new HashSet<>(Arrays.asList(PartOfSpeechType.valueOf("NNP"), PartOfSpeechType.valueOf("."))))),
                List.of(new IdentifiedEntity("Washington.", 28, 38,
                        Collections.singleton(new IdentifiedEntityType("LOCATION", "LOCATION", IdentifiedEntityType.UNKNOWN_SOURCE)), new HashSet<>(Collections.singletonList(PartOfSpeechType.valueOf("NNP"))))));

        assertNotNull(merged);
        assertThat(merged.size(), is(1));
    }
    
    @Test
    public void testMergeWithComma() throws Exception {
        /*
::IdentifiedEntity{text='Tovey', start=612, end=617, type=NAME:Stanford, pos=[PartOfSpeechType.NNP]}, 
::IdentifiedEntity{text=',', start=617, end=618, type=O:Stanford, pos=[PartOfSpeechType.,]}, 
         */

        IdentifiedEntity dr = new IdentifiedEntity("Dr.", 608, 611,
                new HashSet<>(List.of(new IdentifiedEntityType("O", "O", "Stanford"))), new HashSet<>(Collections.singletonList(PartOfSpeechType.valueOf("NNP"))));
        IdentifiedEntity first = new IdentifiedEntity("Tovey", 612, 617, 
                new HashSet<>(List.of(new IdentifiedEntityType("NAME", "NAME", "Stanford"))), new HashSet<>(Collections.singletonList(PartOfSpeechType.valueOf("NNP"))));
        IdentifiedEntity second = new IdentifiedEntity(",", 617, 618,
                new HashSet<>(List.of(new IdentifiedEntityType("O", "O", "Stanford"))), new HashSet<>(Collections.singletonList(PartOfSpeechType.valueOf(""))));


        List<IdentifiedEntity> merged = identifier.mergeEntityListsAndOverlappingEntities(Arrays.asList(dr, first, second));
        assertEquals(3, merged.size()); 
    }
}