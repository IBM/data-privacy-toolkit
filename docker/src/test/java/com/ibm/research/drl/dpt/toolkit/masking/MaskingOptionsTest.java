/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.research.drl.dpt.util.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MaskingOptionsTest {
    @Test
    public void testShouldDeserializeCorrectly() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/configuration_masking_shift.json")) {
            JsonNode treeNode = JsonUtils.MAPPER.readTree(inputStream);
            JsonNode taskOptionsTreeNode = treeNode.get("taskOptions");

            MaskingOptions maskingOptions = JsonUtils.MAPPER.treeToValue(taskOptionsTreeNode, MaskingOptions.class);
            assertThat(maskingOptions, is(not(nullValue())));
        }
    }
}