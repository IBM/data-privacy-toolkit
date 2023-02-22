/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.providers.ProviderType;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractIdentifier implements Identifier, Serializable {
    /**
     * Gets appropriate names.
     *
     * @return the appropriate names
     */
    protected Collection<String> getAppropriateNames() {
        return Collections.emptyList();
    }

    @Override
    public int getPriority() {
        return 100;
    }


    @Override
    public boolean isAppropriateName(String fieldName) {
        Collection<String> appropriateNames = getAppropriateNames();

        for (String name : appropriateNames) {
            if (name.equalsIgnoreCase(fieldName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isTitlecase(String token) {
        if (token.isEmpty()) {
            return true;
        }

        if (token.startsWith("(") && token.endsWith(")")) {
            token = token.substring(1, token.length() - 1);
        }

        if (token.isEmpty()) {
            return true;
        }

        if (!Character.isUpperCase(token.charAt(0))) {
            return false;
        }

        int lowercaseCount = 0;

        for (int i = 1; i < token.length(); i++) {
            if (Character.isLetter(token.charAt(i)) && Character.isLowerCase(token.charAt(i))) {
                lowercaseCount++;
            }
        }

        return lowercaseCount > 0;
    }

    protected boolean isAllUppercase(String token) {
        if (token.isEmpty()) {
            return true;
        }

        for (int i = 0; i < token.length(); i++) {
            if (Character.isLetter(token.charAt(i)) && !Character.isUpperCase(token.charAt(i))) {
                return false;
            }
        }

        return true;
    }

}
