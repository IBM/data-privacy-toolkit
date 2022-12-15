/*******************************************************************
*                                                                 *
* Copyright IBM Corp. 2015                                        *
*                                                                 *
*******************************************************************/
package com.ibm.research.drl.dpt.datasets;

import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaField;
import com.ibm.research.drl.dpt.datasets.schema.IPVSchemaFieldType;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchema;
import com.ibm.research.drl.dpt.datasets.schema.impl.SimpleSchemaField;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
        IPVDataset dataset = IPVDataset.load(this.getClass().getResourceAsStream("/100.csv"), false, ',', '"', false);

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(baos)
        ) {
            dataset.toJSON(new JSONDatasetOptions(), writer);

            System.out.println(
                    baos
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

            System.out.println(
                    baos
            );
        }
    }
}
