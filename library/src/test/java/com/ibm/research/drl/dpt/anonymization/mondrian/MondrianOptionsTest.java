/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.anonymization.mondrian;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MondrianOptionsTest {
    @Test
    public void serialize() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MondrianOptions options = new MondrianOptions();
        String s = mapper.writeValueAsString(options);

        System.out.println(s);

        MondrianOptions after = mapper.readValue(s, MondrianOptions.class);

        assertThat(mapper.writeValueAsString(after), is(s));
    }
}

