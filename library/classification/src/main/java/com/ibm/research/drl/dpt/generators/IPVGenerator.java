/*******************************************************************
 *                                                                 *
 * Copyright IBM Corp. 2015                                        *
 *                                                                 *
 *******************************************************************/
package com.ibm.research.drl.dpt.generators;

import java.util.Collection;
import java.util.Iterator;

/**
 * The interface Ipv generator.
 *
 */
public interface IPVGenerator extends Iterator<ItemSet> {
    boolean isBanned(ItemSet candidate);

    /**
     * Ban.
     *
     * @param itemSet the item set
     */
    void ban(ItemSet itemSet);

    /**
     * Gets banned.
     *
     * @return the banned
     */
    Collection<ItemSet> getBanned();
}
