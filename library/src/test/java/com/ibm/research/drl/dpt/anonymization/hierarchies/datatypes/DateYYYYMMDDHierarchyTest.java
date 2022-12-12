/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DateYYYYMMDDHierarchyTest {
    @Test
    public void testGeneralization() {
        String originalValue = "2016-10-12";

        DateYYYYMMDDHierarchy hierarchy = new DateYYYYMMDDHierarchy();

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("2016-10"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("2016"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }

    @Test
    public void testEnumerateLeavesForYear() {
        DateYYYYMMDDHierarchy hierarchy = new DateYYYYMMDDHierarchy();

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
    }

    @Test
    public void testEnumerateLeavesForYearForLeapYear() {
        DateYYYYMMDDHierarchy hierarchy = new DateYYYYMMDDHierarchy();

        assertThat(hierarchy.getNodeLeaves("2020").size(), is(366));
    }

    @Test
    public void testEnumerateLeavesForYearMonthLeadingZero() {
        DateYYYYMMDDHierarchy hierarchy = new DateYYYYMMDDHierarchy();

        assertThat(hierarchy.getNodeLeaves("1981-01").size(), is(31));
    }

    @Test
    public void testEnumerateLeavesForYearMonth() {
        DateYYYYMMDDHierarchy hierarchy = new DateYYYYMMDDHierarchy();

        assertThat(hierarchy.getNodeLeaves("1981-1").size(), is(31));
        assertThat(hierarchy.getNodeLeaves("1981-2").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("1981-4").size(), is(30));
    }

    @Test
    public void testEnumerateLeavesForYearMonthForLeapYear() {
        DateYYYYMMDDHierarchy hierarchy = new DateYYYYMMDDHierarchy();

        assertThat(hierarchy.getNodeLeaves("2020-02").size(), is(29));
    }
}