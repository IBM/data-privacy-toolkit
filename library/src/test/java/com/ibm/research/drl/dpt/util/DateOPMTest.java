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
package com.ibm.research.drl.dpt.util;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateOPMTest {

    @Test
    public void testLoadMapping() {
        InputStream is  = this.getClass().getResourceAsStream("/dateOPM.csv");

        Map<String, String> map = DateOPM.loadMapping(is);

        assertEquals(2, map.size());
        assertEquals("20130101", map.get("20140101"));
        assertEquals("20141101", map.get("20150101"));
    }

    @Test
    public void testCreation() throws Exception {
        String dateFormatPattern = "yyyyMMdd";
        String startDate = "20140101";
        String endDate = "20141231";

        DateFormat df = new SimpleDateFormat(dateFormatPattern);

        Map<String, Date> map = DateOPM.createOPM(startDate, endDate, dateFormatPattern, 365);

        Date start = df.parse(startDate);
        Date end = df.parse(endDate);

        Calendar cal= Calendar.getInstance();
        cal.setTime(start);
        cal.add(Calendar.DATE, 1);

        Date lastMappedValue = map.get(startDate);

        int count = 0;
        int sameDay = 0;

        while(cal.getTime().before(end)) {
            count++;

            String d = df.format(cal.getTime());
            Date mappedValue = map.get(d);

            if(mappedValue.equals(cal.getTime())) {
                sameDay++;
            }

            assertTrue(mappedValue.after(lastMappedValue));
            lastMappedValue = mappedValue;

            cal.add(Calendar.DATE, 1);
        }

        assertTrue(sameDay < count);

    }
}
