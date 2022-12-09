/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.util;


import com.ibm.research.drl.dpt.configuration.IdentificationConfiguration;
import com.ibm.research.drl.dpt.configuration.IdentificationStrategy;
import com.ibm.research.drl.dpt.providers.identifiers.CharacterRequirements;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;
import com.ibm.research.drl.dpt.providers.identifiers.IdentifierFactory;
import com.ibm.research.drl.dpt.schema.IdentifiedType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;


public class IdentifierUtilsTest {

    @Test
    public void identifiesCorrectlyPotentialFreeTextValues() {
        for (String value : new String[]{
                "this is a decently formatted text",
                "John Doe The third",
                "John                               THE            SAVIOR",
                "abc def ghi",
        }) {
            assertTrue(IdentifierUtils.looksLikeFreeText(value), value);
        }
    }

    @Test
    public void rejectsNonFreeTextLikeValues() {
        for (String value : new String[]{
                "123",
                "thisisnotadecentlyformattedtext",
                "John Doe",
                "John                                             Doe",
                "11111111111111111111111111111111111111111111111111111",
                "abcdef",
                "abc def",
                "abc  def",
                "abcdef  ",
                " abcdef  ",

        }) {
            assertFalse(IdentifierUtils.looksLikeFreeText(value), value);
        }
    }

    @Test
    public void testFrequencyBased() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 10));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 10));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);
        priorities.put("ADDRESS", 89);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 5, false, IdentificationStrategy.FREQUENCY_BASED, priorities, Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 15L, identificationConfiguration);
        //email wins since it has higher priority
        assertEquals("EMAIL", bestType.getTypeName());
    }

    @Test
    public void testFrequencyBasedHigherFrequencyWins() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 10));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 11));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);
        priorities.put("ADDRESS", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 5, false, IdentificationStrategy.FREQUENCY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 15L, identificationConfiguration);
        //address wins since it has higher frequency 
        assertEquals("ADDRESS", bestType.getTypeName());
    }


    @Test
    public void testFrequencyBasedThresholdNotExceeded() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 10));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 10));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);
        priorities.put("ADDRESS", 89);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 90, false, IdentificationStrategy.FREQUENCY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 15L, identificationConfiguration);

        assertNotNull(bestType);
        assertThat(bestType.getCount(), is(-1L));
        assertThat(bestType.getTypeName(), is("UNKNOWN"));
    } 
    
    @Test
    public void testPriorityBased() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 10));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 10));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);
        priorities.put("ADDRESS", 89);
        
        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 50, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());
       
        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 10L, identificationConfiguration);
        assertEquals("EMAIL", bestType.getTypeName());
    }

    @Test
    public void testPriorityBasedSamePriority() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 100));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 10));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);
        priorities.put("ADDRESS", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 50, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 100L, identificationConfiguration);
        assertEquals("EMAIL", bestType.getTypeName());
    }

    @Test
    public void testPriorityBasedSamePriority2() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 100));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 1000));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);
        priorities.put("ADDRESS", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 50, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 1000L, identificationConfiguration);
        assertEquals("ADDRESS", bestType.getTypeName());
    }

    @Test
    public void testPriorityBasedMissingPriority() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 100));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 1000));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 0, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 1000L, identificationConfiguration);
        assertEquals("EMAIL", bestType.getTypeName());
    }

    @Test
    public void testPriorityBasedMissingPriorityHigherDefault() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 100));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 100));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(95, 50, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 100L, identificationConfiguration);
        assertEquals("ADDRESS", bestType.getTypeName());
    }
    
    @Test
    @Disabled
    public void testDumpIdentifiers() {

        for(Identifier identifier: IdentifierFactory.defaultIdentifiers()) {
            System.out.println("|" + identifier.getType().getName() + "|" + identifier.getDescription() + "|" );
        }
    }

    @Test
    public void testPriorityBasedDoesNotExceedFrequencyThreshold() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 100));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 100));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 50, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 1000L, identificationConfiguration);

        assertNotNull(bestType);
        assertThat(bestType.getCount(), is(-1L));
        assertThat(bestType.getTypeName(), is("UNKNOWN"));
    }

    @Test
    public void testPriorityBasedDoesNotExceedFrequencyThreshold2() {
        List<IdentifiedType> identifiedTypeList = new ArrayList<>();
        identifiedTypeList.add(new IdentifiedType("EMAIL", 100));
        identifiedTypeList.add(new IdentifiedType("ADDRESS", 1000));

        Map<String, Integer> priorities = new HashMap<>();
        priorities.put("EMAIL", 90);

        IdentificationConfiguration identificationConfiguration = new IdentificationConfiguration(50, 50, false, IdentificationStrategy.PRIORITY_BASED, priorities,
                Collections.emptyMap());

        IdentifiedType bestType = IdentifierUtils.findBestType(identifiedTypeList, 1000L, identificationConfiguration);
        assertEquals("ADDRESS", bestType.getTypeName());
    }

    @Test
    public void testFillCharacterMap() {
        int[] counters = new int[256];
        Arrays.fill(counters, 0);
        
        String input = "aabc@def.com";
     
        int nA = IdentifierUtils.fillCharacterMap(input, counters);
        
        assertEquals(0, nA);
        assertEquals(2, counters['a']);
        assertEquals(1, counters['b']);
        assertEquals(1, counters['@']);
        assertEquals(0, counters['w']);
    }
    
    @Test
    public void testCreateCharacterProfile() {
        assertEquals(CharacterRequirements.ALPHA | CharacterRequirements.DIGIT, IdentifierUtils.createCharacterProfile("abc123"));
        assertEquals(CharacterRequirements.DIGIT, IdentifierUtils.createCharacterProfile("23"));
        assertEquals(CharacterRequirements.ALPHA | CharacterRequirements.AT, IdentifierUtils.createCharacterProfile("abc@"));
        assertEquals(CharacterRequirements.ALPHA | CharacterRequirements.AT | CharacterRequirements.DOT, IdentifierUtils.createCharacterProfile("abc@def.com"));
        assertEquals(CharacterRequirements.ALPHA | CharacterRequirements.AT | CharacterRequirements.DOT | CharacterRequirements.DIGIT, 
                IdentifierUtils.createCharacterProfile("abc@def123.com"));
    }
}
