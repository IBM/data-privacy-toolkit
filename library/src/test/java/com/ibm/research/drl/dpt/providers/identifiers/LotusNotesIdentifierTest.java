/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.util.Tuple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class LotusNotesIdentifierTest {

    @Test
    public void identifiesValidId() {
        Identifier identifier = new LotusNotesIdentifier();

        for (String valid : Arrays.asList(
                "Name Surname/Country/Company",
                "Dario Gil/Organization/Company",
                "Stefano Braghin/Ireland/IBM@IBMIE"
        )) {
            assertTrue(identifier.isOfThisType(valid), valid);
        }
    }

    @Test
    public void correctlyExtractsPersonComponent() {
        IdentifierWithOffset identifier = new LotusNotesIdentifier();

        for (String[] valid : Arrays.asList(
                new String[]{"Name Surname/Country/Company", "Name Surname"},
                new String[]{"Dario Gil/Organization/Company", "Dario Gil"},
                new String[]{"Stefano Braghin/Ireland/IBM@IBMIE", "Stefano Braghin"}
        )) {
            Tuple<Boolean, Tuple<Integer, Integer>> match = identifier.isOfThisTypeWithOffset(valid[0]);
            assertTrue(match.getFirst(), valid[0]);
            String person = valid[0].substring(match.getSecond().getFirst(), match.getSecond().getFirst() + match.getSecond().getSecond());

            assertEquals(person, valid[1]);
        }
    }
}