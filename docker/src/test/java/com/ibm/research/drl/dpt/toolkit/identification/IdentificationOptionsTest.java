/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2020                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.toolkit.identification;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class IdentificationOptionsTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testShouldDeserializeCorrectly() throws IOException {
        final TreeNode treeNode;

        try(InputStream inputStream = getClass().getResourceAsStream("/configuration_identification_with_headers.json")) {
            treeNode = mapper.readTree(inputStream);
        }

        final TreeNode taskOptionsTreeNode = treeNode.get("taskOptions");

        final IdentificationOptions identificationOptions = mapper.treeToValue(taskOptionsTreeNode, IdentificationOptions.class);

        assertThat(identificationOptions, is(not(nullValue())));
        assertThat(identificationOptions.getLocalization(), is("en-US"));
        assertThat(identificationOptions.getFirstN(), is(-1));
    }
}