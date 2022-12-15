/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.providers.identifiers;

import com.ibm.research.drl.dpt.managers.Manager;

public abstract class AbstractManagerBasedIdentifier extends AbstractIdentifier {

    /**
     * Gets manager.
     *
     * @return the manager
     */
    protected abstract Manager getManager();

    @Override
    public boolean isOfThisType(String identifier) {
        if (this.getMinimumLength() > 0 && identifier.length() < this.getMinimumLength()) {
            return false;
        }

        if (this.getMaximumLength() > 0 && identifier.length() > this.getMaximumLength()) {
            return false;
        }

        return getManager().isValidKey(identifier);
    }

    @Override
    public int getMaximumLength() {
        return getManager().getMaximumLength();
    }

    @Override
    public int getMinimumLength() {
        return getManager().getMinimumLength();
    }
}
