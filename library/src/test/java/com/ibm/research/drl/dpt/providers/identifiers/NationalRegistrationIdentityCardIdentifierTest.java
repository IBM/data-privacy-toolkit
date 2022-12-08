/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2019                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class NationalRegistrationIdentityCardIdentifierTest {
    @Test
    public void identifiesValidNumbers() {
        String[] validValues = {
                "S0000001I",
                "S0000002G",
                "S0000003E",
                "S0000004C",
                "S0000005A",
                "S0000006Z",
                "S0000007H",
        };

        Identifier identifier = new NationalRegistrationIdentityCardIdentifier();

        for (String validValue : validValues) {
            assertThat(validValue, identifier.isOfThisType(validValue), is(true));
        }
    }

    @Test
    public void missesInvalidValues() {
        String[] invalidValues = {
                "foo",
                "foo@gmail.com",
        };

        Identifier identifier = new NationalRegistrationIdentityCardIdentifier();

        for (String invalidValue : invalidValues) {
            assertThat(invalidValue, identifier.isOfThisType(invalidValue), is(false));
        }
    }
}