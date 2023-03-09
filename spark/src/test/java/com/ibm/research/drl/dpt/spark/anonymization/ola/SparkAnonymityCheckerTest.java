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
package com.ibm.research.drl.dpt.spark.anonymization.ola;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.ibm.research.drl.dpt.anonymization.*;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymity;
import com.ibm.research.drl.dpt.anonymization.constraints.KAnonymityMetric;
import com.ibm.research.drl.dpt.anonymization.hierarchies.MaterializedHierarchy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparkAnonymityCheckerTest {

    @Test
    @Disabled
    public void testParseAndGeneralize() throws Exception {
        String row = "1,8,-6,2,3,-6,7,-2,-1,0,1,-6,9,1,1,-1,-1,0,6,-1,-4,3,1,9,9,9,7,9,8";

        List<ColumnInformation> columnInformationList =  new ArrayList<>();

        MaterializedHierarchy col0 = new MaterializedHierarchy();
        col0.add("0", "0-1");
        col0.add("1", "0-1");

        MaterializedHierarchy col1 = new MaterializedHierarchy();
        col1.add("0", "0-2", "0-5", "0-10");
        col1.add("1", "0-2", "0-5", "0-10");
        col1.add("2", "2-4", "0-5", "0-10");
        col1.add("3", "2-4", "0-5", "0-10");
        col1.add("4", "4-6", "0-5", "0-10");
        col1.add("5", "4-6", "5-10", "0-10");
        col1.add("6", "6-8", "5-10", "0-10");
        col1.add("7", "6-8", "5-10", "0-10");
        col1.add("8", "8-10", "5-10", "0-10");
        col1.add("9", "8-10", "5-10", "0-10");

        MaterializedHierarchy col2 = new MaterializedHierarchy();
        col2.add("-0", "0--2",    "0--5",  "0--10", "-10-10");
        col2.add("-1", "0--2",    "0--5",  "0--10", "-10-10");
        col2.add("-2", "-2--4",   "0--5",  "0--10", "-10-10");
        col2.add("-3", "-2--4",   "0--5",  "0--10", "-10-10");
        col2.add("-4", "-4--6",   "0--5",  "0--10", "-10-10");
        col2.add("-5", "-4--6",  "-5--10", "0--10", "-10-10");
        col2.add("-6", "-6--8",  "-5--10", "0--10", "-10-10");
        col2.add("-7", "-6--8",  "-5--10", "0--10", "-10-10");
        col2.add("-8", "-8--10", "-5--10", "0--10", "-10-10");
        col2.add("-9", "-8--10", "-5--10", "0--10", "-10-10");
        col2.add("0", "0-2", "0-5", "0-10", "-10-10");
        col2.add("1", "0-2", "0-5", "0-10", "-10-10");
        col2.add("2", "2-4", "0-5", "0-10", "-10-10");
        col2.add("3", "2-4", "0-5", "0-10", "-10-10");
        col2.add("4", "4-6", "0-5", "0-10", "-10-10");
        col2.add("5", "4-6", "5-10", "0-10", "-10-10");
        col2.add("6", "6-8", "5-10", "0-10", "-10-10");
        col2.add("7", "6-8", "5-10", "0-10", "-10-10");
        col2.add("8", "8-10", "5-10", "0-10", "-10-10");
        col2.add("9", "8-10", "5-10", "0-10", "-10-10");

        columnInformationList.add(new CategoricalInformation(col0, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(col1, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(col2, ColumnType.QUASI));
        for(int i = 0; i < 26; i++) {
            columnInformationList.add(new DefaultColumnInformation());
        }

        long start = System.currentTimeMillis();
        System.out.println("memory before:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0);

        int[] levels = new int[]{1, 1, 2};
        int count = 0;

        CsvMapper  mapper = new CsvMapper();
        JsonNode csvRecord = mapper.readTree(row);

        for(int i = 0; i < 1000000; i++) {
            List<String> anonRow = DatasetGeneralizer.generalizeRow(csvRecord, columnInformationList, levels);
            count += anonRow.size();
        }

        System.out.println("Execution time:" + (System.currentTimeMillis() - start));
        System.out.println("memory after:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0);
        System.out.println(count);
    }

    @Test
    @Disabled
    public void testCheckConstraintsPerformance() {
        String rowString = "1,8,-6,2,3,-6,7,-2,-1,0,1,-6,9,1,1,-1,-1,0,6,-1,-4,3,1,9,9,9,7,9,8";

        List<String> row = Arrays.asList(rowString.split(","));
        List<List<String>> rows = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            rows.add(row);
        }


        List<PrivacyConstraint> privacyConstraints = new ArrayList<>();
        privacyConstraints.add(new KAnonymity(4));
        List<PrivacyMetric> metrics = new ArrayList<>();
        metrics.add(new KAnonymityMetric());

        long start = System.currentTimeMillis();
        System.out.println("memory before:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0);

        int count = 0;

        for(int i = 0; i < 1000000; i++) {
            boolean result = SparkAnonymityChecker.checkConstraints(metrics, privacyConstraints);
            if (result) {
                count++;
            }
        }

        System.out.println("Execution time:" + (System.currentTimeMillis() - start));
        System.out.println("memory after:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0);
        System.out.println(count);
    }

    @Test
    @Disabled
    public void testCreateMapToPairPerformance() throws Exception {
        String row = "1,8,-6,2,3,-6,7,-2,-1,0,1,-6,9,1,1,-1,-1,0,6,-1,-4,3,1,9,9,9,7,9,8";


        List<ColumnInformation> columnInformationList =  new ArrayList<>();

        MaterializedHierarchy col0 = new MaterializedHierarchy();
        col0.add("0", "0-1");
        col0.add("1", "0-1");

        MaterializedHierarchy col1 = new MaterializedHierarchy();
        col1.add("0", "0-2", "0-5", "0-10");
        col1.add("1", "0-2", "0-5", "0-10");
        col1.add("2", "2-4", "0-5", "0-10");
        col1.add("3", "2-4", "0-5", "0-10");
        col1.add("4", "4-6", "0-5", "0-10");
        col1.add("5", "4-6", "5-10", "0-10");
        col1.add("6", "6-8", "5-10", "0-10");
        col1.add("7", "6-8", "5-10", "0-10");
        col1.add("8", "8-10", "5-10", "0-10");
        col1.add("9", "8-10", "5-10", "0-10");

        MaterializedHierarchy col2 = new MaterializedHierarchy();
        col2.add("-0", "0--2",    "0--5",  "0--10", "-10-10");
        col2.add("-1", "0--2",    "0--5",  "0--10", "-10-10");
        col2.add("-2", "-2--4",   "0--5",  "0--10", "-10-10");
        col2.add("-3", "-2--4",   "0--5",  "0--10", "-10-10");
        col2.add("-4", "-4--6",   "0--5",  "0--10", "-10-10");
        col2.add("-5", "-4--6",  "-5--10", "0--10", "-10-10");
        col2.add("-6", "-6--8",  "-5--10", "0--10", "-10-10");
        col2.add("-7", "-6--8",  "-5--10", "0--10", "-10-10");
        col2.add("-8", "-8--10", "-5--10", "0--10", "-10-10");
        col2.add("-9", "-8--10", "-5--10", "0--10", "-10-10");
        col2.add("0", "0-2", "0-5", "0-10", "-10-10");
        col2.add("1", "0-2", "0-5", "0-10", "-10-10");
        col2.add("2", "2-4", "0-5", "0-10", "-10-10");
        col2.add("3", "2-4", "0-5", "0-10", "-10-10");
        col2.add("4", "4-6", "0-5", "0-10", "-10-10");
        col2.add("5", "4-6", "5-10", "0-10", "-10-10");
        col2.add("6", "6-8", "5-10", "0-10", "-10-10");
        col2.add("7", "6-8", "5-10", "0-10", "-10-10");
        col2.add("8", "8-10", "5-10", "0-10", "-10-10");
        col2.add("9", "8-10", "5-10", "0-10", "-10-10");

        columnInformationList.add(new CategoricalInformation(col0, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(col1, ColumnType.QUASI));
        columnInformationList.add(new CategoricalInformation(col2, ColumnType.QUASI));
        for(int i = 0; i < 26; i++) {
            columnInformationList.add(new DefaultColumnInformation());
        }

        List<Integer> quasiColumns = new ArrayList<>();
        quasiColumns.add(0);
        quasiColumns.add(1);
        quasiColumns.add(2);

        int[] levels = new int[]{1, 1, 2};
        int count = 0;

        List<PrivacyMetric> metrics = new ArrayList<>();
        metrics.add(new KAnonymityMetric());

        long start = System.currentTimeMillis();
        System.out.println("memory before:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0);

        for(int i = 0; i < 1000000; i++) {
            Tuple2<String, List<PrivacyMetric>> result = SparkAnonymityChecker.createMapToPair(row, ',', '"', columnInformationList, metrics, quasiColumns,
                    new ArrayList<Integer>(), levels, ContentRequirements.SENSITIVE);
            count += (result._2().size());
        }

        System.out.println("Execution time:" + (System.currentTimeMillis() - start));
        System.out.println("memory after:" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1000000.0);
        System.out.println(count);
    }

    @Test
    @Disabled
    public void testStringBuffer() {
        List<String> quasiValues = new ArrayList<>(5);

        for(int i = 0; i < 5; i++) {
            quasiValues.add("foo");
        }

        long startMillis = System.currentTimeMillis();

        for(int i = 0; i < 10000000; i++) {
            StringBuilder buffer = new StringBuilder();
            for(int j = 0; j < 5; j++) {
                buffer.append(quasiValues.get(j));
                buffer.append(":");
            }
            String key = buffer.toString();
        }

        long diff = System.currentTimeMillis() - startMillis;
        System.out.println("StringBuffer diff: " + diff);
    }
}
