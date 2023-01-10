/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.models.ValueClass;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PluggableLookupIdentifierTest {

    @Test
    public void testRegisterOKIgnoreCase() {
        String value = "xyzW";

        IdentifierFactory identifierFactory = IdentifierFactory.getDefaultIdentifierFactory();

        for(Identifier identifier: identifierFactory.availableIdentifiers()) {
            assertFalse(identifier.isOfThisType(value));
        }

        Collection<String> patterns = Arrays.asList("test", "xyzw");
        Identifier pluggableRegexIdentifier = new PluggableLookupIdentifier("FOOBAR", List.of(), patterns, true,
                ValueClass.TEXT);
        assertTrue(pluggableRegexIdentifier.isOfThisType(value));
        identifierFactory.registerIdentifier(pluggableRegexIdentifier);

        Identifier matchingIdentifier = null;
        for(Identifier identifier: identifierFactory.availableIdentifiers()) {
            if(identifier.isOfThisType(value)) {
                matchingIdentifier = identifier;
                break;
            }
        }

        assertTrue(matchingIdentifier instanceof PluggableLookupIdentifier);
        assertThat(matchingIdentifier.getType().name(), is("FOOBAR"));
    }

    @Test
    public void testRegisterOKMatchCase() {
        String value = "xyzw";
        final IdentifierFactory factory = IdentifierFactory.getDefaultIdentifierFactory();

        for(Identifier identifier: factory.availableIdentifiers()) {
            assertFalse(identifier.isOfThisType(value), identifier.getClass().getName());
        }

        Collection<String> patterns = Arrays.asList("test", "xyzw");
        Identifier pluggableRegexIdentifier = new PluggableLookupIdentifier("FOOBAR33", Collections.emptyList(), patterns, false,
                ValueClass.TEXT);
        assertTrue(pluggableRegexIdentifier.isOfThisType(value));
        factory.registerIdentifier(pluggableRegexIdentifier);

        Identifier matchingIdentifier = null;
        for(Identifier identifier: factory.availableIdentifiers()) {
            if(identifier.isOfThisType(value)) {
                matchingIdentifier = identifier;
                break;
            }
        }

        assertTrue(matchingIdentifier instanceof PluggableLookupIdentifier);
        assertEquals("FOOBAR33", matchingIdentifier.getType().name());

        value = "XYZw";
        assertFalse(pluggableRegexIdentifier.isOfThisType(value));

    }
}
