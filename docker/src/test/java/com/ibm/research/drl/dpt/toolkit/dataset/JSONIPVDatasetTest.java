/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.dataset;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class JSONIPVDatasetTest {
    @Test
    public void loadArrayOfObjects() throws Exception {
        try (
                InputStream inputStream = getClass().getResourceAsStream("/json-dataset-array.json");
                Reader reader = new InputStreamReader(inputStream)) {
            IPVDataset dataset = JSONIPVDataset.load(reader);

            assertThat(dataset.getNumberOfColumns(), is(2));
            assertThat(dataset.getNumberOfRows(), is(3));
        }
    }

    @Test
    public void loadSequenceOfObjects() throws Exception {
        try (
                InputStream inputStream = getClass().getResourceAsStream("/json-dataset-objects.json");
                Reader reader = new InputStreamReader(inputStream)) {
            IPVDataset dataset = JSONIPVDataset.load(reader);

            assertThat(dataset.getNumberOfColumns(), is(2));
            assertThat(dataset.getNumberOfRows(), is(3));
        }
    }

    @Test
    public void loadObjectWithMissingFields() throws Exception {
        try (
                InputStream inputStream = getClass().getResourceAsStream("/json-dataset-missing-fields.json");
                Reader reader = new InputStreamReader(inputStream)) {
            IPVDataset dataset = JSONIPVDataset.load(reader);

            assertThat(dataset.getNumberOfColumns(), is(4));
            assertThat(dataset.getNumberOfRows(), is(3));
        }
    }
}