/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class OutlierRemovalOptionsTest {
    @Test
    @Disabled("Not sure why disabled")
    public void examples() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/outlierRemovalValid.json")) {
            OutlierRemovalOptions options = new ObjectMapper(new YAMLFactory()).readValue(inputStream, OutlierRemovalOptions.class);

            System.out.println(new ObjectMapper().writeValueAsString(options));
        }
    }

    @Test
    public void testValidConfiguration() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/outlierRemovalValid.json")) {
            OutlierRemovalOptions options = new ObjectMapper().readValue(inputStream, OutlierRemovalOptions.class);

            assertNotNull(options);
            List<String> identities = options.getFilters().get(0).getIdentityFields();
            assertNotNull(identities);
            assertFalse(identities.isEmpty());
            assertThat(identities.size(), is(1));
            assertTrue(identities.contains("id"));
        }
    }

    @Test
    public void test() throws Exception {
        System.out.println(new ObjectMapper().writeValueAsString(new OutlierRemovalOptions(Collections.singletonList(new OutlierRemovalFilter(
                Collections.singletonList("id"),
                Arrays.asList(
                        new ThresholdCondition(AggregationType.SUM, Condition.GT, 100.0, "purchaseamount", false)
                )
        )), "outliers")));
    }
}