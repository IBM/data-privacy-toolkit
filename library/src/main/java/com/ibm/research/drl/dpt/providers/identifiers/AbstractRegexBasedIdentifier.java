/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import java.util.Collection;
import java.util.regex.Pattern;

public abstract class AbstractRegexBasedIdentifier extends AbstractIdentifier {

    /**
     * Gets patterns.
     *
     * @return the patterns
     */
    protected abstract Collection<Pattern> getPatterns();

    protected boolean quickCheck(String data) {
        return true;
    }

    /**
     * Matches boolean.
     *
     * @param data the data
     * @return the boolean
     */
    protected boolean matches(String data) {
        if (!quickCheck(data)) {
            return false;
        }

        for (Pattern p : getPatterns()) {
            if (p.matcher(data).matches()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOfThisType(String identifier) {
        return this.matches(identifier);
    }
}
