/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.identification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.research.drl.dpt.configuration.DataTypeFormat;
import com.ibm.research.drl.dpt.datasets.CSVDatasetOptions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

public class IdentificationOptionsTest {

    @Test
    public void testSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // With GetFirstN

        IdentificationOptions identificationOptionsWithGetFirstN = new IdentificationOptions(
                true,
                10,
                true,
                true,
                false,
                0,
                DataTypeFormat.CSV,
                new CSVDatasetOptions(
                        true,
                        ',',
                        '"',
                        false
                ),
                null,
                null,
                false
        );

        String identificationOptionsStringWithGetFirstN = mapper.writeValueAsString(identificationOptionsWithGetFirstN);

        IdentificationOptions identificationOptionsRefWithGetFirstN = mapper.readValue(identificationOptionsStringWithGetFirstN, IdentificationOptions.class);

        assertThat(identificationOptionsRefWithGetFirstN, is(not(nullValue())));
        assertThat(identificationOptionsRefWithGetFirstN, instanceOf(IdentificationOptions.class));

        assertThat(identificationOptionsRefWithGetFirstN.isGetFirstN(), is(true));
        assertThat(identificationOptionsRefWithGetFirstN.getN(), is(10));
        assertThat(identificationOptionsRefWithGetFirstN.isDebug(), is(true));
        assertThat(identificationOptionsRefWithGetFirstN.isDumpSample(), is(true));
        assertThat(identificationOptionsRefWithGetFirstN.isDoSampling(), is(false));
        assertThat(identificationOptionsRefWithGetFirstN.getSampleFraction(), is(0.0));
        assertThat(identificationOptionsRefWithGetFirstN.getInputFormat(), is(DataTypeFormat.CSV));
        assertThat(identificationOptionsRefWithGetFirstN.getDatasetOptions(), is(not(nullValue())));

        // With Sampling

        IdentificationOptions identificationOptionsWithSampling = new IdentificationOptions(
                false,
                0,
                true,
                true,
                true,
                0.5,
                DataTypeFormat.CSV,
                new CSVDatasetOptions(
                        true,
                        ',',
                        '"',
                        false
                ),
                null,
                null,
                false
        );

        String identificationOptionsStringWithSampling = mapper.writeValueAsString(identificationOptionsWithSampling);

        IdentificationOptions identificationOptionsRefWithSampling = mapper.readValue(identificationOptionsStringWithSampling, IdentificationOptions.class);

        assertThat(identificationOptionsRefWithSampling, is(not(nullValue())));
        assertThat(identificationOptionsRefWithSampling, instanceOf(IdentificationOptions.class));

        assertThat(identificationOptionsRefWithSampling.isGetFirstN(), is(false));
        assertThat(identificationOptionsRefWithSampling.getN(), is(0));
        assertThat(identificationOptionsRefWithSampling.isDebug(), is(true));
        assertThat(identificationOptionsRefWithSampling.isDumpSample(), is(true));
        assertThat(identificationOptionsRefWithSampling.isDoSampling(), is(true));
        assertThat(identificationOptionsRefWithSampling.getSampleFraction(), is(0.5));
        assertThat(identificationOptionsRefWithSampling.getInputFormat(), is(DataTypeFormat.CSV));
        assertThat(identificationOptionsRefWithSampling.getDatasetOptions(), is(not(nullValue())));        
    }
}
