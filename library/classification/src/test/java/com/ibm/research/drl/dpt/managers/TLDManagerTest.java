/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TLDManagerTest {

    @Test
    public void testTLD() {
        TLDManager tldManager = TLDManager.instance();
        String domain = "www.nba.com";
        assertTrue(tldManager.getTLD(domain).equals("com"));
        domain = "www.nba.co.uk";
        assertTrue(tldManager.getTLD(domain).equals("co.uk"));

        domain = "www.nba.COM";
        assertEquals("COM", tldManager.getTLD(domain));

        domain = "www.nba.pra";
        assertNull(tldManager.getTLD(domain));
    }

    @Test
    @Disabled
    public void testPerformanceGetTLD() {
        int N = 1000000;
        String hostname = "ie.ibm.com";
        TLDManager tldManager = TLDManager.instance();

        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            String tld = tldManager.getTLD(hostname);
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.println(String.format("%d operations took %d milliseconds (%f msec per op)", N, diff, (double) diff / N));
    }
}
