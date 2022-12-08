/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LocaleDateIdentifierTest {
    @Test
    public void italianDate() {
        String[] dates = {
                "10 settembre 2020",
                "5 maggio"
        };

        LocaleDateIdentifier identifier = new LocaleDateIdentifier();

        for (String date : dates) {
            assertThat(date, identifier.isOfThisType(date), is(true));
        }
    }

    @Test
    public void testDateTime() throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM YYYY", Locale.ITALIAN);

        System.out.println(formatter.parse("10 settembre 2010"));
    }
}