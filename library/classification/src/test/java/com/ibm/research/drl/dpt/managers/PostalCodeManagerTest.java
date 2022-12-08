/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostalCodeManagerTest {

    @Test
    public void testLookupSuccessful() {
        PostalCodeManager postalCodeManager = PostalCodeManager.getInstance();
        String code = "99503";

        assertTrue(postalCodeManager.isValidKey(code));
    }

    @Test
    public void testRandomCodeGenerator() {
        PostalCodeManager postalCodeManager = PostalCodeManager.getInstance();
        assertTrue(postalCodeManager.isValidKey(postalCodeManager.getRandomKey()));
    }

    @Test
    @Disabled
    public void testPerformance() {
        PostalCodeManager postalCodeManager = PostalCodeManager.getInstance();
        int N = 1000000;
        long startMillis = System.currentTimeMillis();
        String code = "99503";

        for (int i = 0; i < N; i++) {
            String maskedValue = postalCodeManager.getClosestPostalCode(code, 10);
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.printf("%d operations took %d milliseconds (%f per op)%n",
                N, diff, (double) diff / N);
    }

    @Test
    public void testClosestCode() {
        PostalCodeManager postalCodeManager = PostalCodeManager.getInstance();
        String originalCode = "99529";
        String[] neighbors = {
                "99501",
                "99502",
                "99503",
                "99507",
                "99510",
                "99515",
                "99517",
                "99518",
                "99529",
                "99530",
                "99599"
        };

        List<String> neighborsList = Arrays.asList(neighbors);


        for(int i =0; i < 100; i++) {
            String randomCode = postalCodeManager.getClosestPostalCode(originalCode, 10);
            assertTrue(neighborsList.contains(randomCode.toUpperCase()));
        }
    }
}
