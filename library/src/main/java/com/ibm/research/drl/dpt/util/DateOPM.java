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

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateOPM {

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar date = Calendar.getInstance();
        date.setTime(startDate);

        long daysBetween = 0;
        while (date.getTime().before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Map<String, Date> createOPM(String startDate, String endDate,
                                              String dateFormatPattern, int daysBudget) throws ParseException {

        Map<String, Date> map = new HashMap<>();
        SecureRandom random = new SecureRandom();

        DateFormat df = new SimpleDateFormat(dateFormatPattern);

        Date start = df.parse(startDate);
        Date end = df.parse(endDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);

        long originalSpaceSize = daysBetween(start, end);
        long mappedSpaceSize = originalSpaceSize + daysBudget;

        int maxStep = (int) Math.floor((double) mappedSpaceSize / (double) originalSpaceSize);

        Date mappedValue = addDays(start, -daysBudget);

        cal.setTime(start);
        while (cal.getTime().before(end)) {
            String key = df.format(cal.getTime());

            int nextStep = 1 + random.nextInt(maxStep);

            mappedValue = addDays(mappedValue, nextStep);
            map.put(key, mappedValue);

            cal.add(Calendar.DATE, 1);
        }

        return map;
    }

    public static Map<String, String> loadMapping(InputStream is) {
        Map<String, String> map = new HashMap<>();

        try (CSVParser reader = Readers.createCSVReaderFromStream(is)) {
            for (CSVRecord line : reader) {
                String key = line.get(0);
                String value = line.get(1);
                map.put(key, value);
            }

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }


    public static Map<String, String> loadMapping(String file) throws IOException {
        return loadMapping(new FileInputStream(file));
    }
}
