<<<<<<< Updated upstream
/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2022                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.dataset;

import com.ibm.research.drl.dpt.datasets.IPVDataset;
import org.junit.jupiter.api.Test;
=======
package com.ibm.research.drl.prima.toolkit.dataset;

import com.ibm.research.drl.prima.datasets.IPVDataset;
import org.junit.Test;
>>>>>>> Stashed changes

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.CoreMatchers.is;
<<<<<<< Updated upstream
import static org.hamcrest.MatcherAssert.assertThat;

=======
import static org.junit.Assert.assertThat;

/*******************************************************************
 * IBM Confidential                                                *
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 * The source code for this program is not published or otherwise  *
 * divested of its trade secrets, irrespective of what has         *
 * been deposited with the U.S. Copyright Office.                  *
 *******************************************************************/
>>>>>>> Stashed changes
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