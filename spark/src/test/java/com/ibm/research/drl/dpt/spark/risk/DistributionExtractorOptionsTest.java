/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.spark.risk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class DistributionExtractorOptionsTest {
    @Test
    public void testValidConfiguration() throws Exception {
        try (InputStream inputStream = getClass().getResourceAsStream("/distributionExtractionValid.yaml")) {
            DistributionExtractorOptions options = new ObjectMapper(new YAMLFactory()).readValue(inputStream, DistributionExtractorOptions.class);

            assertNotNull(options);
            List<String> identities = options.getIdentityFields();
            assertNotNull(identities);
            assertFalse(identities.isEmpty());
            assertThat(identities.size(), is(1));
            assertTrue(identities.contains("id"));
            assertEquals("purchase amount", options.getDimensionDescription());
            assertEquals("CC", options.getPrincipalDescription());
            assertEquals(DistributionExtractorOptions.BinningCondition.BinningType.SIZE, options.getBinningCondition().getType());
            assertEquals(10000.0, options.getBinningCondition().getBinSize(), Double.MIN_VALUE);
            assertEquals(100000, options.getBinningCondition().getBinNumber());
        }
    }
}