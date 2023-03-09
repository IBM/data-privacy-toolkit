/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package com.ibm.research.drl.dpt.anonymization.hierarchies.datatypes;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DateHierarchyTest {
    @Test
    public void testGeneralization() {
        String originalValue = "2016-10-12";

        DateHierarchy hierarchy = new DateHierarchy();

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("2016-10"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("2016"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }
    @Test
    public void testGeneralizationYYYYMMDD() {
        String originalValue = "2016-10-12";

        DateHierarchy hierarchy = new DateHierarchy("yyyy-MM-dd");

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("2016-10"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("2016"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }
    @Test
    public void testGeneralizationDDMMYYYY() {
        String originalValue = "12-10-2016";

        DateHierarchy hierarchy = new DateHierarchy("dd-MM-yyyy");

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("10-2016"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("2016"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }
    @Test
    public void testGeneralizationMMDDYYYY() {
        String originalValue = "10-12-2016";

        DateHierarchy hierarchy = new DateHierarchy("MM-dd-yyyy");

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("10-2016"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("2016"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }

    @Test
    public void testEnumerateLeavesForYear() {
        DateHierarchy hierarchy = new DateHierarchy();

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
    }
    @Test
    public void testEnumerateLeavesForYearYYYYMMDD() {
        DateHierarchy hierarchy = new DateHierarchy("yyyy-MM-dd");

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
    }
    @Test
    public void testEnumerateLeavesForYearDDMMYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("dd-MM-yyyy");

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
    }
    @Test
    public void testEnumerateLeavesForYearMMDDYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("MM-dd-yyyy");

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
    }

    @Test
    public void testEnumerateLeavesForYearForLeapYear() {
        DateHierarchy hierarchy = new DateHierarchy();

        assertThat(hierarchy.getNodeLeaves("2020").size(), is(366));
    }
    @Test
    public void testEnumerateLeavesForYearForLeapYearYYYYMMDD() {
        DateHierarchy hierarchy = new DateHierarchy("yyyy-MM-dd");

        assertThat(hierarchy.getNodeLeaves("2020").size(), is(366));
    }
    @Test
    public void testEnumerateLeavesForYearForLeapYearDDMMYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("dd-MM-yyyy");

        assertThat(hierarchy.getNodeLeaves("2020").size(), is(366));
    }
    @Test
    public void testEnumerateLeavesForYearForLeapYearMMDDYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("MM-dd-yyyy");

        assertThat(hierarchy.getNodeLeaves("2020").size(), is(366));
    }

    @Test
    public void testEnumerateLeavesForYearMonthLeadingZero() {
        DateHierarchy hierarchy = new DateHierarchy();

        assertThat(hierarchy.getNodeLeaves("1981-01").size(), is(31));
    }
    @Test
    public void testEnumerateLeavesForYearMonthLeadingZeroYYYYMMDD() {
        DateHierarchy hierarchy = new DateHierarchy("yyyy-MM-dd");

        assertThat(hierarchy.getNodeLeaves("1981-01").size(), is(31));
    }
    @Test
    public void testEnumerateLeavesForYearMonthLeadingZeroDDMMYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("dd-MM-yyyy");

        assertThat(hierarchy.getNodeLeaves("01-1981").size(), is(31));
    }
    @Test
    public void testEnumerateLeavesForYearMonthLeadingZeroMMDDYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("MM-dd-yyyy");

        assertThat(hierarchy.getNodeLeaves("01-1981").size(), is(31));
    }

    @Test
    public void testEnumerateLeavesForYearMonth() {
        DateHierarchy hierarchy = new DateHierarchy();

        assertThat(hierarchy.getNodeLeaves("1981-1").size(), is(31));
        assertThat(hierarchy.getNodeLeaves("1981-2").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("1981-4").size(), is(30));
    }
    @Test
    public void testEnumerateLeavesForYearMonthYYYYMMDD() {
        DateHierarchy hierarchy = new DateHierarchy("yyyy-MM-dd");

        assertThat(hierarchy.getNodeLeaves("1981-1").size(), is(31));
        assertThat(hierarchy.getNodeLeaves("1981-2").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("1981-4").size(), is(30));
    }
    @Test
    public void testEnumerateLeavesForYearMonthDDMMYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("dd-MM-yyyy");

        assertThat(hierarchy.getNodeLeaves("1-1981").size(), is(31));
        assertThat(hierarchy.getNodeLeaves("2-1981").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("4-1981").size(), is(30));
    }
    @Test
    public void testEnumerateLeavesForYearMonthMMDDYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("MM-dd-yyyy");

        assertThat(hierarchy.getNodeLeaves("1-1981").size(), is(31));
        assertThat(hierarchy.getNodeLeaves("2-1981").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("4-1981").size(), is(30));
    }

    @Test
    public void testEnumerateLeavesForYearMonthForLeapYear() {
        DateHierarchy hierarchy = new DateHierarchy();

        assertThat(hierarchy.getNodeLeaves("2020-02").size(), is(29));
    }
    @Test
    public void testEnumerateLeavesForYearMonthForLeapYearYYYYMMDD() {
        DateHierarchy hierarchy = new DateHierarchy("yyyy-MM-dd");

        assertThat(hierarchy.getNodeLeaves("2020-02").size(), is(29));
    }
    @Test
    public void testEnumerateLeavesForYearMonthForLeapYearDDMMYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("dd-MM-yyyy");

        assertThat(hierarchy.getNodeLeaves("02-2020").size(), is(29));
    }
    @Test
    public void testEnumerateLeavesForYearMonthForLeapYearMMDDYYYY() {
        DateHierarchy hierarchy = new DateHierarchy("MM-dd-yyyy");

        assertThat(hierarchy.getNodeLeaves("02-2020").size(), is(29));
    }

    @Test
    public void testSlashSeparator() {
        String originalValue = "01/02/1981";

        DateHierarchy hierarchy = new DateHierarchy("dd/MM/yyyy");

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
        assertThat(hierarchy.getNodeLeaves("02/1981").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("01/02/1981").size(), is(1));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("02/1981"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("1981"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }

    @Test
    public void testNoLeadingZeros() {
        String originalValue = "1/2/1981";

        DateHierarchy hierarchy = new DateHierarchy("d/M/y");

        assertThat(hierarchy.getHeight(), is(4));

        assertThat(hierarchy.getNodeLeaves("1981").size(), is(365));
        assertThat(hierarchy.getNodeLeaves("2/1981").size(), is(28));
        assertThat(hierarchy.getNodeLeaves("1/2/1981").size(), is(1));

        assertThat(hierarchy.encode(originalValue, 0, false), is(originalValue));
        assertThat(hierarchy.encode(originalValue, 1, false), is("2/1981"));
        assertThat(hierarchy.encode(originalValue, 2, false), is("1981"));
        assertThat(hierarchy.encode(originalValue, 3, false), is(hierarchy.getTopTerm()));
    }
}