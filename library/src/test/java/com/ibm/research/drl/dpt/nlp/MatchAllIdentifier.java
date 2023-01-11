/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2018                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.nlp;


import com.ibm.research.drl.dpt.providers.ProviderType;
import com.ibm.research.drl.dpt.providers.identifiers.Identifier;

import java.util.Collection;

public class MatchAllIdentifier implements Identifier {
    @Override
    public ProviderType getType() {
        return null;
    }

    @Override
    public boolean isOfThisType(String data) {
        return true;
    }

    @Override
    public boolean isAppropriateName(String fieldName) {
        return false;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Collection<ProviderType> getLinkedTypes() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public int getMinimumCharacterRequirements() {
        return 0;
    }

    @Override
    public int getMinimumLength() {
        return 0;
    }

    @Override
    public int getMaximumLength() {
        return Integer.MAX_VALUE;
    }
}

