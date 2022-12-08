/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LevelGeneratorTest {

    @Test
    public void testLevelOneWithoutBan() {

        Set<ItemSet> items = new HashSet<>();
        LevelGenerator generator = new LevelGenerator(5, 1);

        int count = 0;
        while(generator.hasNext()) {
            count++;
            ItemSet itemSet = generator.next();
            assertEquals(1, itemSet.getItems().size());
            assertFalse(items.contains(itemSet));
            items.add(itemSet);
        }

        assertEquals(5, count);
    }

    @Test
    public void testLevelTwoWithoutBan() {

        Set<ItemSet> items = new HashSet<>();

        LevelGenerator generator = new LevelGenerator(5, 2);

        int count = 0;
        while(generator.hasNext()) {
            count++;
            ItemSet itemSet = generator.next();
            assertEquals(2, itemSet.getItems().size());
            assertFalse(items.contains(itemSet));
            items.add(itemSet);
        }

        assertEquals(10, count);
    }

    @Test
    public void testLevelTwoWithBan() {

        Set<ItemSet> banned = new HashSet<>();
        banned.add(new ItemSet(0));

        Set<ItemSet> items = new HashSet<>();
        LevelGenerator generator = new LevelGenerator(banned, 5, 2);

        int count = 0;
        while(generator.hasNext()) {
            count++;
            ItemSet itemSet = generator.next();
            assertEquals(2, itemSet.getItems().size());
            assertFalse(items.contains(itemSet));
            items.add(itemSet);

            Collection<Integer> columns = itemSet.getItems();
            for(Integer column: columns) {
                assertNotEquals(0, column.intValue());
            }

        }

        assertEquals(6, count);
    }

    @Test
    public void testLevelFiveWithoutBan() {

        Set<ItemSet> items = new HashSet<>();
        LevelGenerator generator = new LevelGenerator(5, 5);

        int count = 0;
        while(generator.hasNext()) {
            count++;
            ItemSet itemSet = generator.next();
            assertEquals(5, itemSet.getItems().size());
            assertFalse(items.contains(itemSet));
            items.add(itemSet);
        }

        assertEquals(1, count);
    }

}
