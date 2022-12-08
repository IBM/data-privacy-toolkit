/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2021                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractRegexBasedIdentifierTest {
    @Disabled
    @Test
    public void testRegExp() throws Exception {
        Pattern p = Pattern.compile("DUMMY\\d");

        Matcher matcher = p.matcher("DUMMY1");

        System.out.println(matcher.matches());
        System.out.println(matcher.find());
    }
}
