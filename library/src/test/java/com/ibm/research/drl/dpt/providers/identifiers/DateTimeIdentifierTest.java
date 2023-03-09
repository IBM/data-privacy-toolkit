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
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Because java 11 broke something, needs exploring")
public class DateTimeIdentifierTest {

    @Test
    @Disabled
    public void testFormatter() {
        String format = "dd/MM/yyyy K:mm a";
        
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(format)
                .parseDefaulting(ChronoField.HOUR_OF_AMPM, 0)
                .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.AMPM_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter()
                .withZone(ZoneOffset.systemDefault());

        TemporalAccessor t = formatter.withZone(ZoneOffset.systemDefault()).parse("16/04/2018 02:14 AM");
        System.out.println(((LocalDateTime) t).format(formatter)); 
    }

    @Test
    public void testInvalidPatterns() {
        DateTimeIdentifier identifier = new DateTimeIdentifier();

        String[] invalidDatetimes = {
                "8684     04-08-1951",
                "foobar",
                "01-01-1981 27:05:22",
                "Tue, 32 Apr 2017 10:35:17 GMT",
                ""
        };

        for (String date : invalidDatetimes) {
            assertFalse(identifier.isOfThisType(date), date);
        }
    }
    
    @Test
    public void testIsOfThisTypeForValidPatterns() {
        DateTimeIdentifier identifier = new DateTimeIdentifier();

        String[] validDatetimes = {
                "January 30, 2012 2:19 PM",
                "08-12-1981 00:00:11",
                "08/12/1981 00:00:11",
                "08-12-1981",
                "1981-12-08",
                "Jan 1, 1920",
                "Jan 1,1920",
                "January 1, 1920",
                "January 1,1920",
                "January 1 1920",
                "Jan 1 1920",
                "Tue, 25 Apr 2017 10:35:17 UTC",
                "Tue, 25 Apr 2017 10:35:17 GMT",
                "22-JULY-2008",
                "22-JUL-2008",
                "22-jul-2008",
                "January 23, 2006",
                "January 23rd, 2006",
                "Jan 23, 2006",
                "01/23/2006",
                "1/23/2006",
                "01-23-2006",
                "01.23.2006",
                "200601232216",
                "Wed Oct 04 16:26:52 2006",
                "Wed Sep 19, 2007 11:36 AM",
                "6/4/2006 12:01 PM",
                "11/9/2007 11:12:04 AM",
                "Thu Dec 4, 2008 9:18 AM",
                "11/01/2004 11:35:30",
                "Mon Apr 14 23:51:59 2003",
                "Thu Apr 3 23:51:59 2003",
                "jun 6, 2007 12:50 pm",
                "May 6, 2007 3:50 PM",
                "May 6, 2007 3:50 PM",
                "May 16, 2007 3:50 PM",
                "6/11/2004 at 4:00 PM",
                "jun 6, 2007 3:50 PM",
                "JUN 6, 2007 3:50 PM",
                "May 6, 2007 3:50 PM",
                "May 6, 2007 12:50 PM",
                "Wed Aug 29, 2007 1:55 PM",
               
                "Wed Aug 2, 2006 3:13 PM", 
                "1/20/2004 at 9:51 AM",
                "4/21/08",
                "4/14/98",
                "4/9/2007 9:34 am",
                "4/9/2007   9:34 am",
                
                "10/29/2009 1:53 PM",
                "12/28/2007 06:42:23",
                "Wed Apr 29, 2009  3:00 PM",
                
                "August of 2008",
                "February 2004",
                "3/22/01 at 2:30PM",
                "November 13 at 8am",
                "1/5/2013",
                "12/12/1948",
                "12-12-07",
                "4/9/2004 at 7:40 AM",
                
                "7 August '12",
                "'12-August",
                "January 2016",
                "22 January 2016",
                "31 Dec 2016",

                "02/26/2001,6:24 AM",
                "March of 1995",
                "January 3",
                "January 3rd of this year",
                "6-12-06",
                "2016-02-18T20:15:37.421Z",
                "2014-10-07T14:45:00Z",
                
                "2016-08-18 09:52:39.694821",
                "2016-08-18 09:52:39.694",
                
                "2013-01-29 23:00:00",
                
                "2009-09-25 23:00:00.0",
                "2016-02-21 23:00:00.0"
        };
        
        for (String date : validDatetimes) {
            assertTrue(identifier.isOfThisType(date), date);
        }
    }

    @Test
    public void singleValue() {
        DateTimeIdentifier identifier = new DateTimeIdentifier();

        assertTrue(identifier.isOfThisType("January 30, 2012 2:19 PM"));
    }

    @Test
    @Disabled
    public void testPerformance() {
        DateTimeIdentifier identifier = new DateTimeIdentifier();

        int N = 1000000;
        String[] validDatetimes = {
                "08-12-1981 00:00:11",
                "08/12/1981 00:00:11",
                "08-12-1981",
                "1981-12-08",
        };

        for (String originalDateTime : validDatetimes) {
            long startMillis = System.currentTimeMillis();

            for (int i = 0; i < N; i++) {
                boolean b = identifier.isOfThisType(originalDateTime);
            }

            long diff = System.currentTimeMillis() - startMillis;
            System.out.printf("%s: %d operations took %d milliseconds (%f per op)\n",
                    originalDateTime, N, diff, (double) diff / N);
        }
    }

    @Test
    @Disabled
    public void testFormat() {
        String data = "May 6, 2007 33:50 PM";
        String format = "MMM dd, yyyy H:mm a";
        
        DateFormat f = new SimpleDateFormat(format);
        f.setLenient(true);
        
        ParsePosition position = new ParsePosition(0);
        Date d = f.parse(data, position);
        int index = position.getIndex();
        System.out.println("\t\t" + index);
    }
    
    @Test
    @Disabled
    public void fromDemoFile() throws Exception {
        DateTimeIdentifier identifier = new DateTimeIdentifier();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/demo.csv")))) {
            for (String line = br.readLine(); null != line; line = br.readLine()) {
                final String value = line.split(",")[0];
                try {
                    assertTrue(identifier.isOfThisType(value), value);
                } catch (AssertionError e) {
                    System.out.println(" No: " + e.getMessage());
                }
            }
        }
    }
    
    @Test
    public void testTemporals() {
        DateTimeIdentifier identifier = new DateTimeIdentifier();
        
        String[] valid = {
                "two days ago",
                "last year"
        };
        
        for(String v: valid) {
            assertTrue(identifier.isTemporal(v));
        }
    }
    
    @Test
    public void testMatchingFormat() {
        DateTimeIdentifier dateTimeIdentifier = new DateTimeIdentifier();
        assertNull(dateTimeIdentifier.matchingFormat(""));
    }
}
