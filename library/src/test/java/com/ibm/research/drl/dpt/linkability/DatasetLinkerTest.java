/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2017                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.linkability;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatasetLinkerTest {
    
    @Test
    @Disabled
    public void testBigDataset() throws Exception {
        
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(2, 2));
        linkInformation.add(new LinkInfo(3, 3));
        linkInformation.add(new LinkInfo(4, 4));

        long start = System.currentTimeMillis();
        InputStream input = this.getClass().getResourceAsStream("/florida_original.txt");
        DatasetLinker datasetLinker = new DatasetLinker(input, linkInformation);
        long end = System.currentTimeMillis();

        System.out.println("building took: " + ((end - start) / 1000) + " seconds");
        
        List<String> row = Arrays.asList("foo","bar","32607","F","1989","5");
        
        start = System.currentTimeMillis();
        Integer matches = datasetLinker.matchRow(row, linkInformation);
        end = System.currentTimeMillis();
        
        System.out.println("row match took: " + (end - start) + " milliseconds");

        System.out.println("matches: " + matches);
    }
    
    @Test
    public void testNumerical() throws Exception {
        InputStream target = this.getClass().getResourceAsStream("/datasetLinkerNumericalTest.csv");
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, true));
        
        DatasetLinker datasetLinker = new DatasetLinker(target, linkInformation);
       
        Double value = 10.0;
        Set<Integer> rowIds = datasetLinker.matchValue(value, 0);
        
        assertEquals(2, rowIds.size());
        assertTrue(rowIds.contains(0));
        assertTrue(rowIds.contains(4));
    }

    @Test
    public void testNumericalRange() throws Exception {
        InputStream target = this.getClass().getResourceAsStream("/datasetLinkerNumericalTest.csv");
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, true));

        DatasetLinker datasetLinker = new DatasetLinker(target, linkInformation);

        Double minValue = 9.0;
        Double maxValue = 12.0;
        Set<Integer> rowIds = datasetLinker.matchValueRange(minValue, maxValue, 0);

        assertEquals(3, rowIds.size());
        assertTrue(rowIds.contains(0));
        assertTrue(rowIds.contains(3));
        assertTrue(rowIds.contains(4));

        minValue = 9.0;
        maxValue = 11.0;
        rowIds = datasetLinker.matchValueRange(minValue, maxValue, 0);
        
        assertEquals(3, rowIds.size());
        assertTrue(rowIds.contains(0));
        assertTrue(rowIds.contains(3));
        assertTrue(rowIds.contains(4));

        minValue = -1.0;
        maxValue = 100.0;
        rowIds = datasetLinker.matchValueRange(minValue, maxValue, 0);

        assertEquals(5, rowIds.size());
        assertTrue(rowIds.contains(0));
        assertTrue(rowIds.contains(1));
        assertTrue(rowIds.contains(2));
        assertTrue(rowIds.contains(3));
        assertTrue(rowIds.contains(4));
    }

    @Test
    public void testMix() throws Exception {
        InputStream target = this.getClass().getResourceAsStream("/datasetLinkerNumericalTest.csv");
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, true));
        linkInformation.add(new LinkInfo(1, 1));

        DatasetLinker datasetLinker = new DatasetLinker(target, linkInformation);
        
        List<String> row = Arrays.asList("10","e");
        Integer matches = datasetLinker.matchRow(row, linkInformation);
        assertEquals(1, matches.intValue());
        
        assertEquals(0, datasetLinker.matchRow(Arrays.asList("10", "ww"), linkInformation).intValue());
    }
    
    @Test
    public void testNumericalMatchRow() throws Exception {
        InputStream target = this.getClass().getResourceAsStream("/datasetLinkerNumericalTest.csv");
        List<LinkInfo> linkInformation = new ArrayList<>();
        linkInformation.add(new LinkInfo(0, 0, true));

        DatasetLinker datasetLinker = new DatasetLinker(target, linkInformation);

        List<String> row = List.of("10");
        
        Integer matches = datasetLinker.matchRow(row, linkInformation);
        assertEquals(2, matches.intValue());
    }
}

