/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NameIdentifierTest {

    @Test
    @Disabled("Used to debug JP with Security BU")
    public void fixWeirdDetection() {
        String[] incorrect = {
          "Figures"
        };

        NameIdentifier identifier = new NameIdentifier();


        for (String term : incorrect) {
            assertFalse(identifier.isOfThisType(term), term);
        }
    }

    @Test
    public void testIsOfThisType() throws Exception {
        NameIdentifier identifier = new NameIdentifier();

        assertFalse(identifier.isOfThisType("12308u499234802"));
        assertFalse(identifier.isOfThisType("84032-43092-3242"));

        assertFalse(identifier.isOfThisType("32 Carter Avn."));
        assertFalse(identifier.isOfThisType("15 Kennedy Avenue"));
        assertFalse(identifier.isOfThisType("Thompson Avn 1000"));

        assertTrue(identifier.isOfThisType("Carrol, John J."));
        assertTrue(identifier.isOfThisType("Carrol, John"));
        assertTrue(identifier.isOfThisType("Patrick K. Fitzgerald"));
        assertTrue(identifier.isOfThisType("Patrick Fitzgerald"));
        assertTrue(identifier.isOfThisType("Kennedy John"));
    }

    @Test
    public void testIsOfThisTypeWithOffset() throws Exception {
        NameIdentifier identifier = new NameIdentifier();

        String value = "Carrol, John";
        Tuple<Boolean, Tuple<Integer, Integer>> result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(value.length(), result.getSecond().getSecond().intValue());

        value = "Bhan,";
        result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(value.length() - 1, result.getSecond().getSecond().intValue());

        value = "Carrol, John,";
        result = identifier.isOfThisTypeWithOffset(value);
        assertTrue(result.getFirst());
        assertEquals(0, result.getSecond().getFirst().intValue());
        assertEquals(value.length() - 1, result.getSecond().getSecond().intValue());

    }

    public List<String> fileContentsAsList(InputStream inputStream) throws Exception{
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream));

        String line = reader.readLine();
        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        reader.close();

        return lines;
    }

    @Test
    public void testSurnamesTop1000US() throws Exception {
        NameIdentifier identifier = new NameIdentifier();
        String filename = "/top1KsurnamesUS.csv";
        List<String> surnames = fileContentsAsList(this.getClass().getResourceAsStream(filename));

        int totalSurnames = 0;
        int totalMatches = 0;

        for(String surname: surnames) {
            if (identifier.isOfThisType(surname)) {
                totalMatches += 1;
            }
        }

        assertTrue(((double)totalMatches/(double)totalSurnames) > 0.95);
    }

    @Test
    public void testNamesTop1000US() throws Exception {
        NameIdentifier identifier = new NameIdentifier();
        String[] filenames = {"/top1200maleNamesUS.csv", "/top1000femaleNamesUS.csv"};

        for (String filename: filenames) {
            List<String> names = fileContentsAsList(this.getClass().getResourceAsStream(filename));

            int totalNames = 0;
            int totalMatches = 0;

            for (String name : names) {
                totalNames += 1;
                if (identifier.isOfThisType(name)) {
                    totalMatches += 1;
                }
            }

            assertTrue(((double) totalMatches / (double) totalNames) > 0.70);
        }
    }
}
