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
package com.ibm.research.drl.dpt.datasets;

import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchema;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchemaField;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IPVDatasetTest {

    @Test
    public void testLoad() throws Exception {
        IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);
    }

    @Test
    public void testLoad1() throws Exception {
        IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), true, ',', '"', false);
    }

    @Test
    public void testLoadSkipHeaders() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/100.csv")))) {
            IPVDataset dataset = IPVDataset.load(reader, true, ',', '"', false);
            assertEquals(99, dataset.getNumberOfRows());
        }
    }

    @Test
    public void loadDatasetWithHeader() throws Exception {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/test_with_header.csv")))) {
            IPVDataset dataset = IPVDataset.load(reader, true, ',', '"', false);
            assertEquals(2, dataset.getNumberOfRows());

            List<String> headers = dataset.schema.getFields().stream().map(IPVSchemaField::getName).collect(Collectors.toList());

            assertThat(headers.size(), is(3));
            assertThat(headers, hasItems("h1","h2","h3"));
        }
    }

    @Test
    public void testGetNumberOfColumnsAfterLoad() throws Exception {
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        assertEquals(5, dataset.getNumberOfColumns());
    }

    @Test
    public void testGetNumberOfRowsAfterLoad() throws Exception {
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);
        assertEquals(100, dataset.getNumberOfRows());
    }

    @Test
    public void testGet() throws Exception {
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        // 1998,1,1,0,6
        assertThat(dataset.get(0, 0).hashCode(), is("1998".hashCode()));
        assertThat(dataset.get(0, 0).hashCode(), is(not("1944".hashCode())));
        assertThat(dataset.get(0, 1).hashCode(), is("1".hashCode()));
        assertThat(dataset.get(0, 2).hashCode(), is("1".hashCode()));
        assertThat(dataset.get(0, 3).hashCode(), is("0".hashCode()));
        assertThat(dataset.get(0, 4).hashCode(), is("6".hashCode()));

        // 1944,1,1,12,1
        assertThat(dataset.get(99, 0).hashCode(), is("1944".hashCode()));
        assertThat(dataset.get(99, 1).hashCode(), is("1".hashCode()));
        assertThat(dataset.get(99, 2).hashCode(), is("1".hashCode()));
        assertThat(dataset.get(99, 3).hashCode(), is("12".hashCode()));
        assertThat(dataset.get(99, 4).hashCode(), is("1".hashCode()));
    }

    @Test
    public void testInMemoryDataset() {
        List<List<String>> values = new ArrayList<>();

        List<String> row1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            row1.add("" + i);
        }

        List<String> row2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            row2.add("" + i);
        }

        values.add(row1);
        values.add(row2);

        IPVDataset dataset = new IPVDataset(
                values,
                null,
                false
        );

        assertEquals(2, dataset.getNumberOfRows());
        assertEquals(10, dataset.getNumberOfColumns());

        assertEquals("5".hashCode(), dataset.get(0, 5).hashCode());
    }

    @Test
    public void serializeToCSV() throws Exception {
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        String datasetString = dataset.toString();

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(baos)
        ) {
            dataset.toCSV(new CSVDatasetOptions(false, ',', '"', false), writer);

            String datasetCSV = baos.toString();

            assertThat(datasetCSV, is(datasetString));
        }

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(baos)
        ) {
            dataset.toCSV(new CSVDatasetOptions(true, ',', '"', false), writer);

            String datasetCSV = baos.toString();

            assertThat(datasetCSV, is("Column 0,Column 1,Column 2,Column 3,Column 4\n" + datasetString));

            System.out.println(datasetCSV);
        }
    }

    @Test
    public void serializeToJSON() throws Exception {
        try (
                InputStream input = IPVDatasetTest.class.getResourceAsStream("/100.csv");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(baos)
        ) {
            IPVDataset dataset = IPVDataset.load(input, false, ',', '"', false);
            dataset.toJSON(new JSONDatasetOptions(), writer);

            assertThat(
                    baos.toString(),
                    is("[{\"Column 1\":\"1\",\"Column 0\":\"1998\",\"Column 3\":\"0\",\"Column 2\":\"1\",\"Column 4\":\"6\"},{\"Column 1\":\"1\",\"Column 0\":\"1926\",\"Column 3\":\"3\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1939\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1955\",\"Column 3\":\"0\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1938\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"2\"},{\"Column 1\":\"1\",\"Column 0\":\"1972\",\"Column 3\":\"10\",\"Column 2\":\"2\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1927\",\"Column 3\":\"10\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1958\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1960\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1932\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1941\",\"Column 3\":\"16\",\"Column 2\":\"4\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1943\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1954\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"2\",\"Column 0\":\"1934\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1952\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1939\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1980\",\"Column 3\":\"10\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1974\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1968\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1952\",\"Column 3\":\"13\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1985\",\"Column 3\":\"10\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1972\",\"Column 3\":\"17\",\"Column 2\":\"4\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1982\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1948\",\"Column 3\":\"1\",\"Column 2\":\"1\",\"Column 4\":\"4\"},{\"Column 1\":\"1\",\"Column 0\":\"1951\",\"Column 3\":\"2\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1944\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1960\",\"Column 3\":\"12\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1932\",\"Column 3\":\"15\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1951\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1993\",\"Column 3\":\"3\",\"Column 2\":\"1\",\"Column 4\":\"6\"},{\"Column 1\":\"2\",\"Column 0\":\"1960\",\"Column 3\":\"10\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1966\",\"Column 3\":\"13\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1922\",\"Column 3\":\"9\",\"Column 2\":\"1\",\"Column 4\":\"2\"},{\"Column 1\":\"1\",\"Column 0\":\"1977\",\"Column 3\":\"11\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1961\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1984\",\"Column 3\":\"10\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1934\",\"Column 3\":\"8\",\"Column 2\":\"1\",\"Column 4\":\"2\"},{\"Column 1\":\"1\",\"Column 0\":\"1927\",\"Column 3\":\"3\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1978\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1962\",\"Column 3\":\"13\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1990\",\"Column 3\":\"6\",\"Column 2\":\"1\",\"Column 4\":\"6\"},{\"Column 1\":\"2\",\"Column 0\":\"1983\",\"Column 3\":\"13\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1963\",\"Column 3\":\"7\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"2\",\"Column 0\":\"1959\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1935\",\"Column 3\":\"11\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1952\",\"Column 3\":\"12\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1950\",\"Column 3\":\"11\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1973\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1955\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"2\",\"Column 0\":\"1982\",\"Column 3\":\"13\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1963\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1957\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"2\",\"Column 0\":\"1960\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1963\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1944\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1976\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1949\",\"Column 3\":\"0\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1948\",\"Column 3\":\"12\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1942\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1932\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1950\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1946\",\"Column 3\":\"8\",\"Column 2\":\"2\",\"Column 4\":\"4\"},{\"Column 1\":\"1\",\"Column 0\":\"1982\",\"Column 3\":\"9\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1954\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1954\",\"Column 3\":\"16\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1950\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1942\",\"Column 3\":\"16\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1987\",\"Column 3\":\"10\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1919\",\"Column 3\":\"6\",\"Column 2\":\"4\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1928\",\"Column 3\":\"8\",\"Column 2\":\"1\",\"Column 4\":\"2\"},{\"Column 1\":\"1\",\"Column 0\":\"1953\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1976\",\"Column 3\":\"13\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1985\",\"Column 3\":\"11\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1979\",\"Column 3\":\"10\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1939\",\"Column 3\":\"12\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1943\",\"Column 3\":\"16\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1954\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1995\",\"Column 3\":\"1\",\"Column 2\":\"1\",\"Column 4\":\"6\"},{\"Column 1\":\"2\",\"Column 0\":\"1975\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1955\",\"Column 3\":\"16\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1928\",\"Column 3\":\"16\",\"Column 2\":\"4\",\"Column 4\":\"2\"},{\"Column 1\":\"2\",\"Column 0\":\"1982\",\"Column 3\":\"9\",\"Column 2\":\"1\",\"Column 4\":\"4\"},{\"Column 1\":\"1\",\"Column 0\":\"1936\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1948\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1945\",\"Column 3\":\"13\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1945\",\"Column 3\":\"14\",\"Column 2\":\"2\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1944\",\"Column 3\":\"12\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1981\",\"Column 3\":\"15\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1968\",\"Column 3\":\"6\",\"Column 2\":\"4\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1965\",\"Column 3\":\"16\",\"Column 2\":\"2\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1965\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1954\",\"Column 3\":\"13\",\"Column 2\":\"2\",\"Column 4\":\"3\"},{\"Column 1\":\"1\",\"Column 0\":\"1952\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"4\"},{\"Column 1\":\"1\",\"Column 0\":\"1966\",\"Column 3\":\"14\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1984\",\"Column 3\":\"11\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"1\",\"Column 0\":\"1972\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"5\"},{\"Column 1\":\"2\",\"Column 0\":\"1972\",\"Column 3\":\"17\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1934\",\"Column 3\":\"12\",\"Column 2\":\"4\",\"Column 4\":\"1\"},{\"Column 1\":\"2\",\"Column 0\":\"1980\",\"Column 3\":\"15\",\"Column 2\":\"1\",\"Column 4\":\"1\"},{\"Column 1\":\"1\",\"Column 0\":\"1944\",\"Column 3\":\"12\",\"Column 2\":\"1\",\"Column 4\":\"1\"}]")
            );
        }
    }

    @Test
    public void serializeToComplexJSON() throws Exception {
        IPVDataset dataset = new IPVDataset(
                Arrays.asList(
                        Arrays.asList("1998", "1", "1", "0", "6"),
                        Arrays.asList("1926", "1", "1", "3", "1"),
                        Arrays.asList("1939", "2", "1", "12", "3"),
                        Arrays.asList("1955", "1", "1", "0", "5"),
                        Arrays.asList("1938", "1", "1", "12", "2")
                ), new SimpleSchema(
                "123",
                Arrays.asList(
                        new SimpleSchemaField("date.year", IPVSchemaFieldType.INT),
                        new SimpleSchemaField("date.month", IPVSchemaFieldType.INT),
                        new SimpleSchemaField("date.day", IPVSchemaFieldType.INT),
                        new SimpleSchemaField("time.hour", IPVSchemaFieldType.INT),
                        new SimpleSchemaField("time.minute", IPVSchemaFieldType.INT)
                )
        ), true);

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(baos)
        ) {
            dataset.toJSON(new JSONDatasetOptions(), writer);

            assertThat(
                    baos.toString(),
                    is("[{\"date\":{\"month\":\"1\",\"year\":\"1998\",\"day\":\"1\"},\"time\":{\"hour\":\"0\",\"minute\":\"6\"}},{\"date\":{\"month\":\"1\",\"year\":\"1926\",\"day\":\"1\"},\"time\":{\"hour\":\"3\",\"minute\":\"1\"}},{\"date\":{\"month\":\"2\",\"year\":\"1939\",\"day\":\"1\"},\"time\":{\"hour\":\"12\",\"minute\":\"3\"}},{\"date\":{\"month\":\"1\",\"year\":\"1955\",\"day\":\"1\"},\"time\":{\"hour\":\"0\",\"minute\":\"5\"}},{\"date\":{\"month\":\"1\",\"year\":\"1938\",\"day\":\"1\"},\"time\":{\"hour\":\"12\",\"minute\":\"2\"}}]")
            );
        }
    }
}
