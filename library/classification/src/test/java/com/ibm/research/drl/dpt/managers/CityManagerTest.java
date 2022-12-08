/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.managers;

import com.ibm.research.drl.dpt.models.City;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CityManagerTest {

    @Test
    public void testCityNeighbors() {
        CityManager manager = CityManager.getInstance();
        System.out.println("++++ " + manager.getClosestCity("Boston", 1));
        List<String> expected = Arrays.asList("Boston", "South Boston", "Worcester", "Providence", "Springfield");
        List<String> found = manager.getKey("Boston").getNeighbors().stream().limit(5).map(City::getName).collect(Collectors.toList());
        System.out.println("++++ Found: " + found);
        System.out.println("++++ Expected: " + expected);
        assertEquals(expected, found);

        // CityManagerBak managerBak = CityManagerBak.getInstance();
        //System.out.println("---- " + managerBak.getClosestCity("Boston", 1));
        //System.out.println("---- " + managerBak.getKey("Boston").getNeighbors().stream().limit(5).map(City::getName).collect(Collectors.toList()));
    }
}
